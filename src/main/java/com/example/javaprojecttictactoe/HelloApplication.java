package com.example.javaprojecttictactoe;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Pair;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;


public class HelloApplication extends Application {

    private static final String programName = "XO Game";

    @Override
    public void start(Stage stage)  {

        //Set parent
        BorderPane root = new BorderPane();

        //Set bottom - main screen status messages; see Game.resume() further down sets this value
        Label updateText = new Label();
        updateText.setFont(Font.font(25));
        BorderPane.setAlignment(updateText, Pos.TOP_CENTER);
        root.setBottom(updateText);

        //Set center
        HBox scoreBar = configureScoreBoard();
        root.setCenter(new VBox(scoreBar, configureGrid()));

        //Set top
        Button settings = new Button("Settings");
        root.setTop(configureTopBar(settings));

        //Because of Game.setRoot(), command must exist after VB is finished, Grid is finished, updateText is finished,
        Game.getInstance().setRoot(root);
        Scene mainScene = new Scene(root, 500, 550);
        //Initializes first updateText values
        Game.resume();

        //TODO (note): too short to export to a method?
        Label settingsUpdateText = new Label();
        VBox.setVgrow(settingsUpdateText, Priority.ALWAYS);
        settingsUpdateText.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        settingsUpdateText.setFont(Font.font(15));
        settingsUpdateText.setAlignment(Pos.CENTER);

        //TODO (note): too short to export to a method?
        //Create settings pane
        VBox settingsRoot = new VBox();
        Scene settingsScene = new Scene(settingsRoot, 500, 550);
        settings.setOnAction(actionEvent ->{
            stage.setScene(settingsScene);
            stage.setTitle(programName + " - Settings");
            settingsUpdateText.setText("Welcome to the Settings menu");
        });

        //TODO (note): too short to export to a method?
//        Settings UI:
        Button returnButton = new Button("Return");
        VBox.setMargin(returnButton, new Insets(5,0,0,5));
        returnButton.setOnAction(actionEvent ->{
            stage.setScene(mainScene);
            stage.setTitle(programName);
            Game.resume();
        });
        settingsRoot.getChildren().add(returnButton);

        //TODO (note): export these to methods, separately.
        HBox versusBox = new HBox();
        HBox startBox = new HBox();
//        Versus: (dropdown or radio buttons)
        Label versusText = new Label("Select Opponent:");
        ComboBox<String> versusCBox= new ComboBox<>();
        versusBox.setSpacing(10);
        versusCBox.getItems().addAll("Robot", "Player", "Random");

        String val;
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
        toggleShowScore.setOnAction(actionEvent ->{
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
        //TODO: textFormatter and textFormatter.Change

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
        Label play3label = new Label("Empty:");
        TextField play3text = new TextField();
        play3text.promptTextProperty().bind(Game.emptySymbolProperty());
        play3text.setMaxWidth(35);
        play3text.setAlignment(Pos.CENTER);
        Button applySymbol = new Button("Apply");
        applySymbol.setOnAction(actionEvent->processSymbols(play1text, play2text, play3text, settingsUpdateText));
        Button resetSymbol = new Button("Reset");
        resetSymbol.setOnAction(x->{
            Game.resetSymbols();
            settingsUpdateText.setText("Play symbols reset");
        });
//        Filter: max string length for textField
        UnaryOperator<TextFormatter.Change> filter = change -> {
            if(change.getControlNewText().length() > 2) {
                return null;
            }
            return change;
        };
        play1text.setTextFormatter(new TextFormatter<>(filter));
        play2text.setTextFormatter(new TextFormatter<>(filter));
        play3text.setTextFormatter(new TextFormatter<>(filter));
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


        stage.setTitle(programName);
        stage.setScene(mainScene);
        stage.show();
    }

    public GridPane configureGrid() {
        GridPane gp = new GridPane();
        //Configure grid item, rows, column gaps
        gp.setHgap(5);
        gp.setVgap(5);
        gp.setAlignment(Pos.CENTER);
        VBox.setVgrow(gp, Priority.ALWAYS);
        //Configure grid styling
        gp.setBackground(new Background(new BackgroundFill(
                Color.BLACK, new CornerRadii(4, false), new Insets(10, 10, 10, 10))));
        //Set column rules
        ColumnConstraints cc = new ColumnConstraints();
        cc.setPercentWidth(33);
        RowConstraints rc = new RowConstraints();
        rc.setPercentHeight(33);
        for(int i = 0; i < 3; i++) {

            //Apply column and row constraints * 3
            gp.getColumnConstraints().add(cc);
            gp.getRowConstraints().add(rc);

            //Construct buttons
            for(int j = 0; j < 3; j++) {
                Button button = new Button(Game.getEmptySymbol());
                button.widthProperty().addListener((observableValue, oldValue, newValue) ->
                        button.setFont(new Font(newValue.doubleValue()/3.0)));
                button.setStyle("-fx-background-color: %s; -fx-background-radius: 0".formatted("WHITESMOKE"));

                GridPane.setConstraints(
                        button, i, j, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
                button.maxWidthProperty().bind(gp.widthProperty());
                button.maxHeightProperty().bind(gp.heightProperty());
                button.setOnAction(
                        (x)-> Game.play(new Pair<>(GridPane.getRowIndex(button), GridPane.getColumnIndex(button))));
                gp.add(button, i, j);
            }
        }
        return gp;
    }

    public HBox configureScoreBoard() {
        HBox scoreBox = new HBox();

        //Create children
        Label textP1 = new Label();
        textP1.textProperty().bind(Bindings.format("%s wins: %d", Game.playerOneSymbolProperty(), Game.scorePlayer1Property()));
        Label textP2 = new Label();
        textP2.textProperty().bind(Bindings.format("  %s wins: %d", Game.playerTwoSymbolProperty(), Game.scorePlayer2Property()));
        Label textDraw = new Label();
        textDraw.textProperty().bind(Bindings.format("  Draws: %d", Game.scoreTieProperty()));

        scoreBox.getChildren().addAll(textP1, textP2, textDraw);
        scoreBox.getChildren().forEach(node-> ((Label) node).setFont(Font.font(25)));
        scoreBox.setSpacing(5);
        scoreBox.setAlignment(Pos.CENTER);

        return scoreBox;
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

    public HBox configureTopBar(Button settings) {
        Button restart = new Button("Restart");
        restart.setOnAction(x-> Game.restartGame());
        HBox.setHgrow(settings, Priority.ALWAYS);
        HBox topBar = new HBox(settings, restart);
        BorderPane.setMargin(topBar, new Insets(5, 0, 0, 5));
        topBar.setSpacing(5);
        topBar.setAlignment(Pos.CENTER_LEFT);

        return topBar;
    }

    public static void main(String[] args) {
        launch();
    }


}