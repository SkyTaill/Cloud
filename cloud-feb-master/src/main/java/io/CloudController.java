package io;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

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

    private static final int BUFFER_SIZE = 1024;
    private static final String clientDir = "client/";

    public TextField input;
    public TextArea output;
    private Network network;
    private byte[] buffer;

    public void sendCommand(ActionEvent actionEvent) throws IOException {
        String text = input.getText();
        input.clear();
        network.write(text);
    }

    // getFile fileName -> file fileName fileSize fileBytes

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            buffer = new byte[BUFFER_SIZE];
            network = Network.get();
            new Thread(() -> {
                try {
                    while (true) {
                        String message = network.read();
                        if (message.equals("file")) {
                            String fileName = clientDir + network.read();
                            //
                            long fileSize = network.readLong();
                            // buffer_size = 10   (10 + 4 - 1) / 10 = 1
                            // bytes = 4, 11 - 1 / 10 + 1
                            try (FileOutputStream fos = new FileOutputStream(fileName)) {
                                for (int i = 0; i < (fileSize - 1) / BUFFER_SIZE + 1; i++) {
                                    int read = network.read(buffer);
                                    fos.write(buffer, 0, read);
                                }
                            }
                            String out = message + " " + fileName + " " + fileSize + " bytes " +
                                    "uploaded from server.\n";
                            Platform.runLater(() -> output.appendText(out));
                        } else {
                            Platform.runLater(() -> output.appendText(message));
                        }
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
