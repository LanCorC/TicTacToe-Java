package com.example.javaprojecttictactoe;

import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.adapter.JavaBeanProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
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
import javafx.util.Pair;

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

        gameGridVisual.setPrefSize(stage.getWidth(), stage.getHeight());

        gameGridVisual.setAlignment(Pos.CENTER);
//        gameGridVisual.setPadding(new Insets(15, 15, 15, 15));
        //TODO: temporary. i want lines inside the grids, between grids, not a 'border'
//        gameGridVisual.setBorder(new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.FULL)));

//        gameGridVisual.;
        Game.getInstance().setRoot(root);
        Node[][] gameGrid = new Text[3][3];

        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
//                Text text = new Text((i + j)% 2 == 0 ? "X" : "O");
//                text.setTextAlignment(TextAlignment.CENTER);
//                gameGrid[i][j] = text;
//                text.setStyle("-fx-background-color: aqua");
//                gameGridVisual.add(text, i, j);
//                text.setFont(new Font(15));

                Button button = new Button("~");
//                button.setText((i + j)% 2 == 0 ? "X" : "O");
//                button.fontProperty().bind();
//                button.setAlignment(Pos.CENTER);
                button.widthProperty().addListener((observableValue, oldValue, newValue) -> button.setFont(new Font(newValue.doubleValue()/3.0)));
                button.setStyle("-fx-background-color: %s; -fx-background-radius: 0".formatted("WHITESMOKE"));
//                button.setBackground(Background.EMPTY);

                GridPane.setConstraints(button, i, j, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
//                button.maxWidthProperty().bind(gameGridVisual.widthProperty().divide(5));
//                button.maxHeightProperty().bind(gameGridVisual.heightProperty().divide(5));
                button.maxWidthProperty().bind(gameGridVisual.widthProperty());
                button.maxHeightProperty().bind(gameGridVisual.heightProperty());
                button.setOnAction((x)->{
                    Game.play(new Pair<>(GridPane.getRowIndex(button), GridPane.getColumnIndex(button)));
                });
                gameGridVisual.add(button, i, j);
            }
        }

        //TODO: do not include in final, debugging
//        gameGridVisual.setGridLinesVisible(true);

        //Add constraints to all columns
        ColumnConstraints cc = new ColumnConstraints();
        cc.setPercentWidth(33);
        RowConstraints rc = new RowConstraints();
        rc.setPercentHeight(33);
        for(int i = 0; i < 3; i++) {
            gameGridVisual.getColumnConstraints().add(cc);
            gameGridVisual.getRowConstraints().add(rc);
        }

        BorderPane.setMargin(gameGridVisual, new Insets(15, 15, 15, 15));
        root.setCenter(gameGridVisual);


        HBox topBar = new HBox();
        BorderPane.setMargin(topBar, new Insets(5, 0, 0, 5));
                //Set buttons on top, e.g. Menu, restart, potentially a win counter
            //Mobile app version will have buttons and corresponding popup, not a menu drop-down
            //else, just create a single anchored button on the corner and have it call a popup menu to 'emulate' mobile in PC
        Button settings = new Button("Setting");
        Button restart = new Button("Restart");
        restart.setOnAction(x-> Game.restartGame());
        Label textP1 = new Label(Game.getPlayerOneSymbol() + " wins: ");
        Label pointsP1 = new Label();
        pointsP1.textProperty().bind(Game.scorePlayer1Property().asString());
        Label textP2 = new Label(Game.getPlayerTwoSymbol() + " wins: ");
        Label pointsP2 = new Label();
        pointsP2.textProperty().bind(Game.scorePlayer2Property().asString());
        Label textDraw = new Label("Draws: ");
        Label pointsDraw = new Label();
        pointsDraw.textProperty().bind(Game.scoreTieProperty().asString());


//        settings.setPadding(new Insets(50,50,50,50));
//        HBox.setMargin(settings, new Insets(5, 0, 0, 5));
        topBar.setSpacing(5);
        HBox.setHgrow(settings, Priority.ALWAYS);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.getChildren().addAll(settings, restart, textP1, pointsP1, textP2, pointsP2, textDraw, pointsDraw);
//        settings.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        root.setTop(topBar);


        gameGridVisual.setBackground(new Background(new BackgroundFill(Color.BLACK, new CornerRadii(4, false), new Insets(5, 5, 5, 5))));

        //Set status messages
            //e.g. "Player's turn" or "X wins!"
        Label updateText = new Label("X's turn!");
        updateText.setFont(Font.font(25));
        BorderPane.setAlignment(updateText, Pos.TOP_CENTER);
        root.setBottom(updateText);


        Scene scene = new Scene(root, 500, 500);
        stage.setTitle("XO Game");
        stage.setScene(scene);
        stage.show();

//        //TODO: this is for debugging
//        System.out.println(gameGridVisual.getWidth());
//        gameGridVisual.getChildren().forEach(x-> {
//            if(x.getClass() != Button.class) {
//                System.out.println("skip");
//            } else {
//                Button btn = (Button) x;
//                System.out.println(btn.getWidth());
//                btn.setFont(new Font(31));
//                System.out.println(btn.fontProperty());
//                System.out.println(btn.widthProperty());
//            }
//
//        });

    }

    public static void main(String[] args) {
        launch();
    }
}