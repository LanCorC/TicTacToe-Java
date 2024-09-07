package com.example.javaprojecttictactoe;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Pair;


import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


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

                Button button = new Button(Game.getEmptySymbol());
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
        //TODO: experimental format binding
        Label textP1 = new Label();
        textP1.textProperty().bind(Bindings.format("%s wins: %d", Game.playerOneSymbolProperty(), Game.scorePlayer1Property()));
        Label textP2 = new Label();
        textP2.textProperty().bind(Bindings.format("  %s wins: %d", Game.playerTwoSymbolProperty(), Game.scorePlayer2Property()));
        Label textDraw = new Label();
        textDraw.textProperty().bind(Bindings.format("  Draws: %d", Game.scoreTieProperty()));

        HBox scoreBar = new HBox();
        scoreBar.getChildren().addAll(textP1, textP2, textDraw);
        scoreBar.getChildren().forEach(x-> ((Label) x).setFont(Font.font(25)));
        scoreBar.setSpacing(5);
        scoreBar.setAlignment(Pos.CENTER);
        VBox vb = new VBox();
        vb.getChildren().addAll(scoreBar, gameGridVisual);

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

        Label settingsUpdateText = new Label();
        VBox.setVgrow(settingsUpdateText, Priority.ALWAYS);
        settingsUpdateText.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        settingsUpdateText.setFont(Font.font(15));
        settingsUpdateText.setAlignment(Pos.CENTER);

        //Create settings pane
        VBox settingsRoot = new VBox();
        Scene settingsScene = new Scene(settingsRoot, 500, 550);
        settings.setOnAction(x->{
            stage.setScene(settingsScene);
            stage.setTitle(programName + " - Settings");
            settingsUpdateText.setText("Welcome to the Settings menu");
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
            settingsUpdateText.setText("GameMode set to '%s'".formatted(tempVal));
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
            settingsUpdateText.setText("Starting player set to '%s'".formatted(tempVal));
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
        Button resetScore = new Button("Reset Score");
        resetScore.setOnAction(x->{
            Game.resetScores();
            settingsUpdateText.setText("Scoreboard cleared!");
        });
        Button toggleShowScore = new Button("Hide Score");
        toggleShowScore.setOnAction(x->{
            scoreBar.getChildren().forEach(child-> child.setVisible(!child.isVisible()));
            if(toggleShowScore.textProperty().get().equals("Hide Score")) {
                toggleShowScore.setText("Show Score");
                settingsUpdateText.setText("Scoreboard hidden!");
            } else {
                toggleShowScore.setText("Hide Score");
                settingsUpdateText.setText("Scoreboard revealed!");
            }
        });
        HBox scoreHBox = new HBox();
        scoreHBox.getChildren().addAll(resetScore, toggleShowScore);
        scoreHBox.setAlignment(Pos.CENTER);
        scoreHBox.setSpacing(15);
        VBox.setMargin(scoreHBox, new Insets(0, 25, 0, 25));

        //TODO: make a 'factory' for these headers here, too repetitive
        Label symbolsHeader = new Label("Symbol Settings");
        symbolsHeader.setFont(Font.font(15));
        VBox.setMargin(symbolsHeader, new Insets(0, 25, 0, 25));
        Separator line2 = new Separator();
        VBox.setMargin(line2, new Insets(0, 15, 0, 15));

        //TODO symbol interface
        // [Player1: " "] [Player 2: " "] [Empty: " "] (Apply) (Reset)
        HBox symbolHBox = new HBox();
        symbolHBox.setSpacing(5);
        VBox.setMargin(symbolHBox, new Insets(0, 25, 0, 25));
        symbolHBox.setAlignment(Pos.CENTER);
        Label play1label = new Label("Player 1:");
        TextField play1text = new TextField();
        play1text.promptTextProperty().bind(Game.playerOneSymbolProperty());
        play1text.setMaxWidth(35);
        play1text.setAlignment(Pos.CENTER);
        Label play2label = new Label("Player 2:");
        TextField play2text = new TextField();
        play2text.promptTextProperty().bind(Game.playerTwoSymbolProperty());
        play2text.setMaxWidth(35);
        play2text.setAlignment(Pos.CENTER);
        Label play3label = new Label("Player 3:");
        TextField play3text = new TextField();
        play3text.promptTextProperty().bind(Game.emptySymbolProperty());
        play3text.setMaxWidth(35);
        play3text.setAlignment(Pos.CENTER);
        Button applySymbol = new Button("Apply");
        applySymbol.setOnAction(x->processSymbols(play1text, play2text, play3text, settingsUpdateText));
        Button resetSymbol = new Button("Reset");
        resetSymbol.setOnAction(x->{
            Game.resetSymbols();
            settingsUpdateText.setText("Play symbols reset");
        });
        symbolHBox.getChildren().addAll(play1label,play1text,play2label,play2text,play3label,play3text);

        HBox symbolButtonHBox = new HBox();
        symbolButtonHBox.setSpacing(15);
        symbolButtonHBox.setAlignment(Pos.CENTER);
        symbolButtonHBox.getChildren().addAll(applySymbol,resetSymbol);


        settingsRoot.getChildren().addAll(settingsHeader, line, versusBox, startBox, scoreboardHeader, line1,
                scoreHBox, symbolsHeader, line2, symbolHBox, symbolButtonHBox, settingsUpdateText);
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

    public void processSymbols(TextField p1, TextField p2, TextField p3, Label updateText) {
        List<TextField> fields = Arrays.asList(p1, p2, p3);
        Set<String> count = new HashSet<>();
        for(TextField tf : fields) {
            if(tf.getText().isEmpty()) {
                updateText.setText("Invalid symbols: missing value");
                return;
            }
            count.add(tf.getText().strip());
        }

        if(count.size() != 3) {
            updateText.setText("Invalid symbols: duplicate values");
            return;
        }

        //else, all valid
        Game.setSymbols(p1.getText(), p2.getText(), p3.getText());
        updateText.setText("Symbols updated!");
    }

    public static void main(String[] args) {
        launch();
    }


}