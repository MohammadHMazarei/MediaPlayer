package com.company;

import com.company.controller.MediaPageController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader root = new FXMLLoader(this.getClass().getResource("view/MediaPage.fxml"));

        root.load();

        MediaPageController controller = root.getController();

        primaryStage.setTitle("ORIPlayer ~Design by MMD~");
        primaryStage.setScene(new Scene(root.getRoot()));
        controller.setOnKeyboardPressed();
        primaryStage.show();
    }
}
