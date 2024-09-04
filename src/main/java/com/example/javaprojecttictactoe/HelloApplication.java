package com.example.javaprojecttictactoe;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
//        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
//        Scene scene = new Scene(fxmlLoader.load(), 320, 240);

        GridPane gameGridVisual = new GridPane();
        gameGridVisual.setHgap(5);
        gameGridVisual.setVgap(5);

        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                gameGridVisual.add(new Text(i % 2 == 0 ? "X" : "O"), i, j);
            }
        }

//        gameGridVisual.addColumn(3, );
//        gameGridVisual.addRow(3, );

        stage.setTitle("XO Game");

        Scene scene = new Scene(gameGridVisual, 500, 500);
        stage.setScene(scene);
        stage.show();


    }

    public static void main(String[] args) {
        launch();
    }
}