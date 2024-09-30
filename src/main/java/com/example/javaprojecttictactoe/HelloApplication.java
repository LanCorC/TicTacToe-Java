package com.example.javaprojecttictactoe;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.*;
import java.util.function.UnaryOperator;


public class HelloApplication extends Application {

    private static final String programName = "XO Game";

    @Override
    public void start(Stage stage) {

        //Set parent
        BorderPane root = new BorderPane();

        //Set bottom - main screen status messages; Game.resume() sets this value
        Label updateText = new Label();
        updateText.setFont(Font.font(25));
        BorderPane.setAlignment(updateText, Pos.TOP_CENTER);
        root.setBottom(updateText);

        //Set center, make the GameGrid
        HBox scoreBar = getScoreBoard();
        root.setCenter(new VBox(scoreBar, getGameGrid()));

        //Set top
        Button settings = new Button("Settings");
        root.setTop(getTopBar(settings));

        //Because of Game.setRoot(), .setRoot() must exist after Center, gameGrid, Bottom fulfilled
        Game.getInstance().setRoot(root);
        Scene mainScene = new Scene(root, 500, 550);
        //Initializes first updateText values
        Game.resume();

        //Configure settings update text
        Label settingsUpdateText = new Label();
        VBox.setVgrow(settingsUpdateText, Priority.ALWAYS);
        settingsUpdateText.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        settingsUpdateText.setFont(Font.font(15));
        settingsUpdateText.setAlignment(Pos.CENTER);

        //Create settings pane, settings button
        VBox settingsRoot = new VBox();
        Scene settingsScene = new Scene(settingsRoot, 500, 550);
        settings.setOnAction(actionEvent -> {
            stage.setScene(settingsScene);
            stage.setTitle(programName + " - Settings");
            settingsUpdateText.setText("Welcome to the Settings menu");
        });

        //Create returning button
        Button returnButton = new Button("Return");
        VBox.setMargin(returnButton, new Insets(5, 0, 0, 5));
        returnButton.setOnAction(actionEvent -> {
            stage.setScene(mainScene);
            stage.setTitle(programName);
            Game.resume();
        });
        settingsRoot.getChildren().add(returnButton);

        //Create HBox to house 'versus' and 'start' settings
        HBox versusBox = getVersusBox(settingsUpdateText);
        HBox startBox = getStartBox(settingsUpdateText);

        //Scoreboard setting buttons
        Button toggleShowScore = getToggleShowScore(scoreBar, settingsUpdateText);
        Button resetScore = new Button("Reset Score");
        resetScore.setOnAction(actionEvent -> {
            Game.resetScores();
            settingsUpdateText.setText("Scoreboard cleared!");
        });

        HBox scoreHBox = new HBox(toggleShowScore, resetScore);
        scoreHBox.setAlignment(Pos.CENTER);
        scoreHBox.setSpacing(15);
        VBox.setMargin(scoreHBox, new Insets(0, 25, 0, 25));

        //Configure user input fields into symbolTextHBox
        HBox symbolTextHBox = new HBox();
        TextField[] symbolFields = configureSymbolTextHBox(symbolTextHBox);

        //Get HBox containing "Apply" and "Return" buttons
        HBox symbolButtonHBox = getSymbolButtonHBox(symbolFields, settingsUpdateText);

        settingsRoot.getChildren().addAll(
                getLabel("Game Settings"), getSeparator(), versusBox, startBox,
                getLabel("Scoreboard Settings"), getSeparator(), scoreHBox,
                getLabel("Symbol Settings"), getSeparator(), symbolTextHBox, symbolButtonHBox, settingsUpdateText);
        settingsRoot.setSpacing(5);

        //Add icon
        Image icon = new Image(getClass().getClassLoader().getResourceAsStream("icon-round.png"));
        stage.getIcons().add(icon);

        stage.setTitle(programName);
        stage.setScene(mainScene);
        stage.show();
    }

    //Program buttons for Symbol Settings
    private HBox getSymbolButtonHBox(TextField[] symbolFields, Label settingsUpdateText) {
        Button applySymbol = new Button("Apply");
        applySymbol.setOnAction(actionEvent -> processSymbols(symbolFields[0], symbolFields[1], symbolFields[2], settingsUpdateText));
        Button resetSymbol = new Button("Reset");
        resetSymbol.setOnAction(actionEvent -> {
            Game.resetSymbols();
            settingsUpdateText.setText("Play symbols reset");
        });

        HBox symbolButtonHBox = new HBox(applySymbol, resetSymbol);
        symbolButtonHBox.setSpacing(15);
        symbolButtonHBox.setAlignment(Pos.CENTER);
        return symbolButtonHBox;
    }


    private GridPane getGameGrid() {
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
        for (int i = 0; i < 3; i++) {

            //Apply column and row constraints * 3
            gp.getColumnConstraints().add(cc);
            gp.getRowConstraints().add(rc);

            //Construct play buttons
            for (int j = 0; j < 3; j++) {
                Button button = new Button(Game.getEmptySymbol());
                button.widthProperty().addListener((observableValue, oldValue, newValue) ->
                        button.setFont(new Font(newValue.doubleValue() / 3.0)));
                button.setStyle("-fx-background-color: %s; -fx-background-radius: 0".formatted("WHITESMOKE"));

                GridPane.setConstraints(
                        button, i, j, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
                button.maxWidthProperty().bind(gp.widthProperty());
                button.maxHeightProperty().bind(gp.heightProperty());
                button.setOnAction(
                        (actionEvent) -> Game.play(new Pair<>(GridPane.getRowIndex(button), GridPane.getColumnIndex(button))));
                gp.add(button, i, j);
            }
        }
        return gp;
    }

    private HBox getScoreBoard() {
        HBox scoreBox = new HBox();

        //Create children
        Label textP1 = new Label();
        textP1.textProperty().bind(Bindings.format("%s wins: %d", Game.playerOneSymbolProperty(), Game.scorePlayer1Property()));
        Label textP2 = new Label();
        textP2.textProperty().bind(Bindings.format("  %s wins: %d", Game.playerTwoSymbolProperty(), Game.scorePlayer2Property()));
        Label textDraw = new Label();
        textDraw.textProperty().bind(Bindings.format("  Draws: %d", Game.scoreTieProperty()));

        scoreBox.getChildren().addAll(textP1, textP2, textDraw);
        scoreBox.getChildren().forEach(node -> ((Label) node).setFont(Font.font(25)));
        scoreBox.setSpacing(5);
        scoreBox.setAlignment(Pos.CENTER);

        return scoreBox;
    }

    private void processSymbols(TextField p1, TextField p2, TextField p3, Label updateText) {
        List<TextField> fields = Arrays.asList(p1, p2, p3);
        Set<String> count = new HashSet<>();
        for (TextField tf : fields) {
            if (tf.getText().isEmpty()) {
                updateText.setText("Invalid symbols: missing value");
                return;
            }
            count.add(tf.getText().strip());
        }

        if (count.size() != 3) {
            updateText.setText("Invalid symbols: duplicate values");
            return;
        }

        //else, all valid
        Game.setSymbols(p1.getText(), p2.getText(), p3.getText());
        updateText.setText("Symbols updated!");
    }

    //Create main menu bar
    private HBox getTopBar(Button settings) {
        Button restart = new Button("Restart");
        restart.setOnAction(actionEvent -> Game.restartGame());
        HBox.setHgrow(settings, Priority.ALWAYS);
        HBox topBar = new HBox(settings, restart);
        BorderPane.setMargin(topBar, new Insets(5, 0, 0, 5));
        topBar.setSpacing(5);
        topBar.setAlignment(Pos.CENTER_LEFT);

        return topBar;
    }

    private HBox getVersusBox(Label updateText) {
        Label versusText = new Label("Select Opponent:");
        ComboBox<String> versusCBox = new ComboBox<>();
        versusCBox.getItems().addAll("Robot", "Player", "Random");

        //Fetch initial value
        String val;
        switch (Game.getGameMode()) {
            case VS_ROBOT -> val = "Robot";
            case VS_PLAYER -> val = "Player";
            default -> val = "Random";
        }
        versusCBox.setValue(val);

        versusCBox.setOnAction(actionEvent -> {
            String tempVal = versusCBox.getValue();
            switch (versusCBox.getValue()) {
                case "Robot" -> Game.setGameMode(Game.VersusMode.VS_ROBOT);
                case "Player" -> Game.setGameMode(Game.VersusMode.VS_PLAYER);
                default -> Game.setGameMode(Game.VersusMode.VS_RANDOM);
            }
            updateText.setText("GameMode set to '%s'".formatted(tempVal));
        });

        HBox versusBox = new HBox(versusText, versusCBox);
        versusBox.setSpacing(10);
        versusBox.setAlignment(Pos.CENTER);

        return versusBox;
    }

    private HBox getStartBox(Label updateText) {
        Label startText = new Label("Who goes first:");
        ComboBox<String> startCBox = new ComboBox<>();
        startCBox.getItems().addAll("Player1", "Player2", "Random");

        //Fetch initial value
        String val;
        switch (Game.getStartMode()) {
            case FIRST_START -> val = "Player1";
            case SECOND_START -> val = "Player2";
            default -> val = "Random";
        }
        startCBox.setValue(val);

        startCBox.setOnAction(actionEvent -> {
            String tempVal = startCBox.getValue();
            switch (tempVal) {
                case "Player1" -> Game.setStartMode(Game.StartMode.FIRST_START);
                case "Player2" -> Game.setStartMode(Game.StartMode.SECOND_START);
                default -> Game.setStartMode(Game.StartMode.RANDOM_START);
            }
            updateText.setText("Starting player set to '%s'".formatted(tempVal));
        });

        HBox startBox = new HBox(startText, startCBox);
        startBox.setSpacing(10);
        startBox.setAlignment(Pos.CENTER);

        return startBox;
    }


    private Separator getSeparator() {
        Separator line = new Separator();
        VBox.setMargin(line, new Insets(0, 15, 0, 15));
        return line;
    }

    private Label getLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font(15));
        VBox.setMargin(label, new Insets(0, 25, 0, 25));
        return label;
    }

    private Button getToggleShowScore(HBox scoreBar, Label settingsUpdateText) {
        Button toggleShowScore = new Button("Hide Score");
        //Make button interactive
        toggleShowScore.setOnAction(actionEvent -> {
            scoreBar.getChildren().forEach(child -> child.setVisible(!child.isVisible()));
            if (toggleShowScore.textProperty().get().equals("Hide Score")) {
                toggleShowScore.setText("Show Score");
                settingsUpdateText.setText("Scoreboard hidden!");
            } else {
                toggleShowScore.setText("Hide Score");
                settingsUpdateText.setText("Scoreboard revealed!");
            }
        });
        return toggleShowScore;
    }

    //Returns textFields for new symbol assignment
    private TextField[] configureSymbolTextHBox(HBox hBox) {
        //Configure styling
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(5);
        VBox.setMargin(hBox, new Insets(0, 25, 0, 25));

        TextField[] textFields = new TextField[3];

        //Populate hBox with label and corresponding textField
        for (int i = 0; i < 3; i++) {

            Label label = new Label();
            TextField textField = getTextField();

            switch (i) {
                case 0 -> {
                    label.setText("Player 1:");
                    textField.promptTextProperty().bind(Game.playerOneSymbolProperty());
                }
                case 1 -> {
                    label.setText("Player 2:");
                    textField.promptTextProperty().bind(Game.playerTwoSymbolProperty());
                }
                default -> {
                    label.setText("Empty:");
                    textField.promptTextProperty().bind(Game.emptySymbolProperty());
                }
            }
            textFields[i] = textField;

            hBox.getChildren().addAll(label, textField);
        }

        //Filter: max string length for textField
        UnaryOperator<TextFormatter.Change> filter = change -> {
            if (change.getControlNewText().length() > 2) {
                return null;
            }
            return change;
        };

        for (TextField textField : textFields) {
            textField.setTextFormatter(new TextFormatter<>(filter));
        }

        return textFields;
    }

    private TextField getTextField() {
        TextField textField = new TextField();
        textField.setMaxWidth(35);
        textField.setAlignment(Pos.CENTER);
        return textField;
    }

    public static void main(String[] args) {
        launch();
    }
}