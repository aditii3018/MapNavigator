package com.example.mapnavigator;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class MapNavigator extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/mapnavigator/hello-view.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, 900, 700);
        scene.getStylesheets().add(getClass().getResource("/com/example/mapnavigator/styles.css").toExternalForm());
        primaryStage.setTitle("Map Navigator");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
