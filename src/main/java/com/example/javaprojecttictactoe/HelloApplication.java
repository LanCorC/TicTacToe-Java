package com.example.javaprojecttictactoe;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Pair;
import com.example.javaprojecttictactoe.Game;


import java.io.IOException;


public class HelloApplication extends Application {

    private static final String programName = "XO Game";

    @Override
    public void start(Stage stage) throws IOException {
//        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
//        Scene mainScene = new Scene(fxmlLoader.load(), 320, 240);

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



        //Set status messages
        //e.g. "Player's turn" or "X wins!"
//        Label updateText = new Label("'s turn!");
        String val;
        if(Game.getCurrentPlayer() == 1) {
            val = Game.getPlayerOneSymbol();
        } else {
            val = Game.getPlayerTwoSymbol();
        }
        Label updateText = new Label(val + "'s turn!");
        updateText.setFont(Font.font(25));
        BorderPane.setAlignment(updateText, Pos.TOP_CENTER);
        root.setBottom(updateText);


        HBox topBar = new HBox();
        BorderPane.setMargin(topBar, new Insets(5, 0, 0, 5));
                //Set buttons on top, e.g. Menu, restart, potentially a win counter
            //Mobile app version will have buttons and corresponding popup, not a menu drop-down
            //else, just create a single anchored button on the corner and have it call a popup menu to 'emulate' mobile in PC
        Button settings = new Button("Setting");
//        settings.setOnAction(x->stage.hide());
        Button restart = new Button("Restart");
        restart.setOnAction(x-> Game.restartGame());
        Label textP1 = new Label(Game.getPlayerOneSymbol() + " wins:");
        Label pointsP1 = new Label();
        pointsP1.textProperty().bind(Game.scorePlayer1Property().asString());
        Label textP2 = new Label("  " + Game.getPlayerTwoSymbol() + " wins:");
        Label pointsP2 = new Label();
        pointsP2.textProperty().bind(Game.scorePlayer2Property().asString());
        Label textDraw = new Label("  Draws:");
        Label pointsDraw = new Label();
        pointsDraw.textProperty().bind(Game.scoreTieProperty().asString());

        HBox middleBar = new HBox();
        middleBar.getChildren().addAll(textP1, pointsP1, textP2, pointsP2, textDraw, pointsDraw);
        middleBar.getChildren().forEach(x-> ((Label) x).setFont(Font.font(25)));
        middleBar.setSpacing(5);
        middleBar.setAlignment(Pos.CENTER);
        VBox vb = new VBox();
        vb.getChildren().addAll(middleBar, gameGridVisual);

        VBox.setVgrow(gameGridVisual, Priority.ALWAYS);
        BorderPane.setMargin(gameGridVisual, new Insets(15, 15, 15, 15));
        root.setCenter(vb);

//        settings.setPadding(new Insets(50,50,50,50));
//        HBox.setMargin(settings, new Insets(5, 0, 0, 5));
        topBar.setSpacing(5);
        HBox.setHgrow(settings, Priority.ALWAYS);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.getChildren().addAll(settings, restart);
//        topBar.getChildren().forEach(x->{
//            if(x.getClass() == Label.class) {
//                Label lb = (Label) x;
//                lb.setFont(Font.font(15));
//            }
//        });
//        settings.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        root.setTop(topBar);


        gameGridVisual.setBackground(new Background(new BackgroundFill(Color.BLACK, new CornerRadii(4, false), new Insets(10, 10, 10, 10))));


        Game.getInstance().setRoot(root);
        Scene mainScene = new Scene(root, 500, 550);

        //Create settings pane
        VBox settingsRoot = new VBox();
        Scene settingsScene = new Scene(settingsRoot, 500, 550);
        settings.setOnAction(x->{
            stage.setScene(settingsScene);
            stage.setTitle(programName + " - Settings");
//            stage.setScene(mainScene);
        });
//        settingsRoot.setOnMouseClicked(x->stage.setScene(mainScene));
        //TODO:

//        Settings UI:
        Button returnButton = new Button("Return");
        returnButton.setOnAction(x->{
            stage.setScene(mainScene);
            stage.setTitle(programName);
            Game.resume();
        });
        VBox.setMargin(returnButton, new Insets(5,0,0,5));
        settingsRoot.getChildren().add(returnButton);
        HBox versusBox = new HBox();
        HBox startBox = new HBox();
        HBox toggleBoxes = new HBox(); //e.g. single button, reset, hide,... night mode / daymode?
//        Versus: (dropdown or radio buttons)
        Label versusText = new Label("Select Opponent:");
        ComboBox<String> versusCBox= new ComboBox<>();
        versusBox.setSpacing(10);
        versusCBox.getItems().addAll("Robot", "Player", "Random");

        //'val' is a previously declared String for temporary values
        switch (Game.getGameMode()) {
            case VS_ROBOT -> val = "Robot";
            case VS_PLAYER -> val = "Player";
            default -> val = "Random";
        }
        versusCBox.setValue(val);

        versusCBox.setOnAction(x->{
            String tempVal = versusCBox.getValue();
            switch(tempVal) {
                case "Robot" -> Game.setGameMode(Game.VersusMode.VS_ROBOT);
                case "Player" -> Game.setGameMode(Game.VersusMode.VS_PLAYER);
                default -> Game.setGameMode(Game.VersusMode.VS_RANDOM);
            }
        });
        //TODO: way to pre-select current live mode
        versusBox.getChildren().addAll(versusText, versusCBox);
        versusBox.setAlignment(Pos.CENTER);
        //Additional: when switching to Robot/Random AND it is Player2's turn, immediately play turn... wait until dialog is closed?

//        Start: (dropdown or radio buttons)
        Label startText = new Label("Who goes first:");
        ComboBox<String> startCBox = new ComboBox<>();
        startBox.setSpacing(10);
        startCBox.getItems().addAll("Player1", "Player2", "Random");
        switch (Game.getStartMode()) {
            case FIRST_START -> val = "Player1";
            case SECOND_START -> val = "Player2";
            default -> val = "Random";
        }
        startCBox.setValue(val);
        startCBox.setOnAction(x->{
            String tempVal = startCBox.getValue();
            switch (tempVal) {
                case "Player1" -> Game.setStartMode(Game.StartMode.FIRST_START);
                case "Player2" -> Game.setStartMode(Game.StartMode.SECOND_START);
                default -> Game.setStartMode(Game.StartMode.RANDOM_START);
            }
        });
        //TODO: code the 'onStart'. imagine this affects "restartGame"
        startBox.getChildren().addAll(startText, startCBox);
        startBox.setAlignment(Pos.CENTER);

        //TODO: make a 'factory' for these headers here, too repetitive
        Label settingsHeader = new Label("Game Settings");
        settingsHeader.setFont(Font.font(15));
        VBox.setMargin(settingsHeader, new Insets(0, 25, 0, 25));
        Separator line = new Separator();
        VBox.setMargin(line, new Insets(0, 15, 0, 15));

        //TODO: make a 'factory' for these headers here, too repetitive
        Label scoreboardHeader = new Label("Scoreboard Settings");
        scoreboardHeader.setFont(Font.font(15));
        VBox.setMargin(scoreboardHeader, new Insets(0, 25, 0, 25));
        Separator line1 = new Separator();
        VBox.setMargin(line1, new Insets(0, 15, 0, 15));
        //



        settingsRoot.getChildren().addAll(settingsHeader, line, versusBox, startBox, scoreboardHeader, line1);
        settingsRoot.setSpacing(5);
        //for now, see if clicking correctly on the buttons/comboboxes do not trigger the setOnmouseClicked
            //if it does trigger, remove that. include a "return" button


        stage.setTitle("XO Game");
        stage.setScene(mainScene);
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