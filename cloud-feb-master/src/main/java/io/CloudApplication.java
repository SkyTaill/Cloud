package io;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CloudApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent parent = FXMLLoader.load(getClass().getResource("cloud_feb.fxml"));
        primaryStage.setScene(new Scene(parent));
        primaryStage.setTitle("Cloud");
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(r -> {
            try {
                Network.get().write("/quit");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        primaryStage.show();
    }
}
