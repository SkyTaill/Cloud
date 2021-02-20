package io.Server;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import io.Client.Network;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class CloudController implements Initializable {

    /**
     * ls
     * touch file.txt
     * cat file.txt
     * cd path
     *
     * */

    public TextField input;
    public TextArea output;
    private Network network;

    public void sendCommand(ActionEvent actionEvent) throws IOException {
        String text = input.getText();
        input.clear();
        network.write(text);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            network = Network.get();
            new Thread(() -> {
                try {
                    while (true) {
                        String message = network.read();
                        Platform.runLater(() -> output.appendText(message));
                        if (message.equals("/quit")) {
                            network.close();
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
