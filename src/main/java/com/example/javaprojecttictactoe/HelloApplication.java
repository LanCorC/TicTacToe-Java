package com.example.javaprojecttictactoe;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.IOException;


public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
//        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
//        Scene scene = new Scene(fxmlLoader.load(), 320, 240);

        //Set parent
        BorderPane root = new BorderPane();

        //Set center
        GridPane gameGridVisual = new GridPane();
        gameGridVisual.setHgap(5);
        gameGridVisual.setVgap(5);
//        gameGridVisual.setPrefHeight();
        gameGridVisual.setPrefSize(stage.getWidth(), stage.getHeight());
//        gameGridVisual.setStyle("-fx-border-color: black");
//        gameGridVisual.setStyle("-fx-background-color: aqua");
        gameGridVisual.setAlignment(Pos.CENTER);
        gameGridVisual.setPadding(new Insets(15, 15, 15, 15));

//        gameGridVisual.;
        Node[][] gameGrid = new Text[3][3];


        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
//                Text text = new Text((i + j)% 2 == 0 ? "X" : "O");
//                text.setTextAlignment(TextAlignment.CENTER);
//                gameGrid[i][j] = text;
//                text.setStyle("-fx-background-color: aqua");
//                gameGridVisual.add(text, i, j);
//                text.setFont(new Font(15));

                Button button = new Button();
                button.setText((i + j)% 2 == 0 ? "X" : "O");
                button.setAlignment(Pos.CENTER);

                GridPane.setConstraints(button, i, j, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
//                GridPane.setConstraints(button, i, j, 1, 1, HPos.CENTER, VPos.CENTER);
//                GridPane.setFillHeight(button, true);
//                GridPane.setFillWidth(button, true);
                button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                gameGridVisual.add(button, i, j);
            }
        }

        //TODO: do not include in final, debugging
        gameGridVisual.setGridLinesVisible(true);

        //Add constraints to all columns
        ColumnConstraints cc = new ColumnConstraints();
        cc.setPercentWidth(33);
        RowConstraints rc = new RowConstraints();
        rc.setPercentHeight(33);
        for(int i = 0; i < 3; i++) {
            gameGridVisual.getColumnConstraints().add(cc);
            gameGridVisual.getRowConstraints().add(rc);
        }

//        gameGridVisual.getChildren().forEach(btn -> GridPane.setConstraints(btn, ));



//        gameGridVisual.get

        root.setCenter(gameGridVisual);

        //Set buttons on top, e.g. Menu, restart, potentially a win counter
            //Mobile app version will have buttons and corresponding popup, not a menu drop-down
            //else, just create a single anchored button on the corner and have it call a popup menu to 'emulate' mobile in PC
        Button settings = new Button("Settings");
//        settings.setPadding(new Insets(50,50,50,50));
        BorderPane.setMargin(settings, new Insets(5, 0, 0, 5));
        BorderPane.setAlignment(settings, Pos.TOP_LEFT);
//        settings.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        root.setTop(settings);


        //Set status messages
            //e.g. "Player's turn" or "X wins!"
        Label updateText = new Label("X's turn!");
        BorderPane.setAlignment(updateText, Pos.TOP_CENTER);
        root.setBottom(updateText);




        Scene scene = new Scene(root, 500, 500);
        stage.setTitle("XO Game");
        stage.setScene(scene);
        stage.show();


    }

    public static void main(String[] args) {
        launch();
    }
}