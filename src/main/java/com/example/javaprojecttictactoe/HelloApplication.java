package com.example.javaprojecttictactoe;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
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
        gameGridVisual.setAlignment(Pos.CENTER);
        Node[][] gameGrid = new Text[3][3];

        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                Text text = new Text((i + j)% 2 == 0 ? "X" : "O");
                gameGrid[i][j] = text;
                gameGridVisual.add(text, i, j);
                text.setFont(new Font(50));
            }
        }

        ColumnConstraints cc = new ColumnConstraints();
        cc.setPercentWidth(33);
        RowConstraints rc = new RowConstraints();
        rc.setPercentHeight(33);

        gameGridVisual.getColumnConstraints().addAll(cc);
        gameGridVisual.getRowConstraints().add(rc);
//        gameGridVisual.get

        root.setCenter(gameGridVisual);

        //Set buttons on top, e.g. Menu, restart, potentially a win counter
            //Mobile app version will have buttons and corresponding popup, not a menu drop-down
        MenuBar menu = new MenuBar();
        Menu settings = new Menu("..."); //triple line settings, or cogwheel symbol
            //Modes- vs Player, vs Random, vs Robot
            //First turn- Player, Player2, Random

        Menu restart = new Menu("Restart"); //'loop back' emoji or symbol
        menu.getMenus().addAll(settings, restart);
        root.setTop(menu);
        //Set status messages
                //e.g. "Player's turn" or "X wins!"
        Scene scene = new Scene(root, 500, 500);
        stage.setTitle("XO Game");
        stage.setScene(scene);
        stage.show();


    }

    public static void main(String[] args) {
        launch();
    }
}