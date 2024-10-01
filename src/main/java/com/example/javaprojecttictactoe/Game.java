package com.example.javaprojecttictactoe;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Pair;

import java.util.*;

public class Game {

    private static Game INSTANCE;

    public enum VersusMode {
        VS_ROBOT,
        VS_RANDOM,
        VS_PLAYER
    }

    public enum StartMode {
        FIRST_START,
        SECOND_START,
        RANDOM_START
    }

    enum WinState {
        PENDING,
        DRAW,
        PLAYER1,
        PLAYER2
    }

    public static final int PLAYER1 = 1;
    public static final int PLAYER2 = -1;
    private static int currentPlayer = PLAYER1;

    private static StartMode startMode = StartMode.FIRST_START;
    private static VersusMode gameMode = VersusMode.VS_ROBOT;

    //Game symbols
    private static final String DEFAULT_SYMBOL_1 = "X";
    private static final String DEFAULT_SYMBOL_2 = "O";
    private static final String DEFAULT_SYMBOL_EMPTY = "~";
    private static final SimpleStringProperty playerOneSymbol = new SimpleStringProperty(DEFAULT_SYMBOL_1);
    private static final SimpleStringProperty playerTwoSymbol = new SimpleStringProperty(DEFAULT_SYMBOL_2);
    private static final SimpleStringProperty emptySymbol = new SimpleStringProperty(DEFAULT_SYMBOL_EMPTY);

    //Score trackers
    private static final SimpleIntegerProperty scorePlayer2 = new SimpleIntegerProperty(0);
    private static final SimpleIntegerProperty scorePlayer1 = new SimpleIntegerProperty(0);
    private static final SimpleIntegerProperty scoreTie = new SimpleIntegerProperty(0);
    private static WinState winCondition = WinState.PENDING;

    //Store last played move
    private static Button lastPlayed = new Button();
    private static final Color LAST_COLOR = Color.GOLD;
    private static final Color PLAYER1_COLOR = Color.DARKTURQUOISE;
    private static final Color PLAYER2_COLOR = Color.TOMATO;
    private static final Color EMPTY_COLOR = Color.BLACK;

    //Nodes from parent scene
    private static GridPane visual = null;
    private static Label updateText = null;

    //Note: we will use GridPane.getColumn(btn) and GridPane.getRow(btn) to find the appropriate cell
    private static int[][] gameBoard = new int[3][3]; //defaults 0 = empty, 1 = player1, -1 = player2
    private static int remaining = 9;
    private static final Random rand = new Random();

    private Game() {
    }

    public static Game getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Game();
        }
        return INSTANCE;
    }

    public void setRoot(Node root) {
        BorderPane temp = (BorderPane) root;
        updateText = (Label) temp.getBottom();
        VBox vtemp = (VBox) temp.getCenter();
        vtemp.getChildren().forEach(child -> {
            if (child.getClass() == GridPane.class) {
                visual = (GridPane) child;
            }
        });
    }

    public static SimpleIntegerProperty scorePlayer2Property() {
        return scorePlayer2;
    }

    public static SimpleIntegerProperty scorePlayer1Property() {
        return scorePlayer1;
    }

    public static SimpleIntegerProperty scoreTieProperty() {
        return scoreTie;
    }

    public static String getPlayerOneSymbol() {
        return playerOneSymbol.getValue();
    }

    public static String getPlayerTwoSymbol() {
        return playerTwoSymbol.getValue();
    }

    public static String getEmptySymbol() {
        return emptySymbol.getValue();
    }

    public static SimpleStringProperty playerOneSymbolProperty() {
        return playerOneSymbol;
    }

    public static SimpleStringProperty playerTwoSymbolProperty() {
        return playerTwoSymbol;
    }

    public static SimpleStringProperty emptySymbolProperty() {
        return emptySymbol;
    }

    public static VersusMode getGameMode() {
        return gameMode;
    }

    public static StartMode getStartMode() {
        return startMode;
    }

    public static void setStartMode(StartMode startMode) {
        Game.startMode = startMode;
    }

    public static void setGameMode(VersusMode gameMode) {
        Game.gameMode = gameMode;
        System.out.println("Gamemode changed!");
    }

    public static void playTurn() {
        if (winCondition != WinState.PENDING) {
            //Do nothing
        } else if (gameMode == VersusMode.VS_ROBOT) {
            playRobot();
        } else {
            playRandom();
        }
    }

    public static void resume() {
        if (winCondition != WinState.PENDING) {
            //The game has already ended
            return;
        }
        if (currentPlayer == PLAYER2 && gameMode != VersusMode.VS_PLAYER) {
            playTurn();
        } else if (currentPlayer == PLAYER1) {
            updateText.setText(currentSymbol() + "'s turn!");
            System.out.println("Player1's go!");
        }

    }

    public static void playRobot() {
        //Move to win
        Pair<Integer, Integer> move = canWin(currentPlayer);
        if (move != null) {
            play(move);
            return;
        }

        //Move to block
        move = canWin(opponent());
        if (move != null) {
            play(move);
            return;
        }

        //Any move
        playRandom();
    }

    public static void playRandom() {
        if (remaining == 0) {
            System.out.println("No more turns available(?)!");
            return;
        }

        //Rand includes 0, but logic requires non-zero
        int candidate = 0;
        while (candidate == 0) {
            candidate = (rand.nextInt(remaining + 1));
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                //If not taken, decrement
                if (gameBoard[i][j] == 0 && --candidate == 0) {
                    play(new Pair<>(i, j));
                    return;
                }
            }
        }

        System.out.println("Could not find a random empty spot!");
    }

    private static String currentSymbol() {
        return currentPlayer == PLAYER1 ? playerOneSymbol.getValue() : playerTwoSymbol.getValue();
    }


    public static void play(Pair<Integer, Integer> turn) {
        if (winCondition != WinState.PENDING) {
            //do nothing
            System.out.println("Game has already terminated, press 'Restart' to start a new match!");
            return;
        }

        if (!validTurn(turn)) {
            System.out.println(currentPlayer + " tried to play: " + turn);
            nextTurn(true);
            return; //skips all processing
        }

        //Update game trackers
        remaining--;
        gameBoard[turn.getKey()][turn.getValue()] = currentPlayer;
        if (winner()) winCondition = currentPlayer == PLAYER1 ? WinState.PLAYER1 : WinState.PLAYER2;

        System.out.println(currentSymbol() + " played Row: %d Col: %d".formatted(turn.getKey(), turn.getValue()));

        Node[] buttons = visual.getChildren().toArray(new Node[0]);
        for (Node button : buttons) {
            if (GridPane.getColumnIndex(button).equals(turn.getValue()) && GridPane.getRowIndex(button).equals(turn.getKey())) {
                ((Button) button).setText(currentSymbol());

                //Update visual
                lastPlayed.setTextFill(EMPTY_COLOR);
                lastPlayed = (Button) button;
                lastPlayed.setTextFill(currentPlayer == PLAYER1 ? PLAYER1_COLOR : PLAYER2_COLOR);
                break;
            }
        }

        if (winCondition == WinState.PENDING && remaining == 0) {
            winCondition = WinState.DRAW;
        } else if (winCondition == WinState.PENDING) {
            nextTurn();
            return;
        }

        winSequence();
    }

    //Checks the board for winner
    private static boolean winner() {
        int countRow;
        int countCol;

        //check rows
        for (int i = 0; i < 3; i++) {
            countRow = 0;
            countCol = 0;
            for (int j = 0; j < 3; j++) {
                //row
                if (gameBoard[i][j] == currentPlayer) {
                    countRow++;
                } else {
                    countRow = -3;
                }
                //column
                if (gameBoard[j][i] == currentPlayer) {
                    countCol++;
                } else {
                    countCol = -3;
                }
            }
            if (countRow == 3 || countCol == 3) {
                return true;
            }
        }

        int countDown = 0; //down slant, NorthWest to SouthEast
        int countUp = 0; //up slant, SouthWest to NorthEast
        for (int i = 0; i < 3; i++) {
            if (gameBoard[i][i] == currentPlayer) {
                countDown++;
            } else {
                countDown = -3;
            }

            if (gameBoard[2 - i][i] == currentPlayer) {
                countUp++;
            } else {
                countUp = -3;
            }
        }

        return countDown == 3 || countUp == 3;
    }

    //Input current, or opponent player, to check for a winning or blocking move (Robot)
    private static Pair<Integer, Integer> canWin(int player) {
        //Tally and final result
        int count;
        Pair<Integer, Integer> move;

        //Check all rows
        for (int i = 0; i < 3; i++) {
            //Reset tally
            count = 0;
            move = null;
            for (int j = 0; j < 3; j++) {
                if (gameBoard[i][j] == player) {
                    count++;
                } else if (gameBoard[i][j] == 0) {
                    move = new Pair<>(i, j);
                } else {
                    //Disqualify
                    count = 0;
                    break;
                }
            }

            if (validate(count, move)) return move;
        }

        //Check columns
        for (int i = 0; i < 3; i++) {
            count = 0;
            move = null;
            for (int j = 0; j < 3; j++) {
                int num = gameBoard[j][i];
                if (num == player) {
                    count++;
                } else if (num == 0) {
                    move = new Pair<>(j, i);
                } else {
                    count = 0;
                    break;
                }
            }

            if (validate(count, move)) return move;
        }

        //Check diagonal1 (NorthWest to SouthEast):
        count = 0;
        move = null;
        for (int i = 0; i < 3; i++) {
            int num = gameBoard[i][i];
            if (num == player) {
                count++;
            } else if (num == 0) {
                move = new Pair<>(i, i);
            } else {
                count = 0;
                break;
            }
        }

        if (validate(count, move)) return move;

        count = 0;
        move = null;
        //Check diagonal2 (SouthWest to NorthEast):
        for (int i = 0; i < 3; i++) {
            int num = gameBoard[2 - i][i];
            if (num == player) {
                count++;
            } else if (num == 0) {
                move = new Pair<>(2 - i, i);
            } else {
                count = 0;
                break;
            }
        }

        if (validate(count, move)) return move;

        return null;
    }

    //Win-making or win-blocking confirmed
    private static boolean validate(int count, Pair<Integer, Integer> move) {
        if (count == 2 && move != null) {
            return true;
        } else if (count == 3) {
            //'Redundant' branch, troubleshooting
            System.out.println("You should win here somewhere!");
            winCondition = currentPlayer == PLAYER1 ? WinState.PLAYER1 : WinState.PLAYER2;
        }
        return false;
    }

    //Note: small, but improves readability
    private static int opponent() {
        return currentPlayer * -1;
    }

    //Note: Row,Column
    private static boolean validTurn(Pair<Integer, Integer> move) {
        return gameBoard[move.getKey()][move.getValue()] == 0;
    }

    private static void nextTurn() {
        nextTurn(false);
    }

    private static void nextTurn(boolean badMove) {
        if (badMove) {
            //On player illegalMove
            updateText.setText("Bad move, " + currentSymbol() + ". Try again!");
            //Troubleshooting - should never show!
            if (currentPlayer == PLAYER2 && gameMode != VersusMode.VS_PLAYER) {
                updateText.setText("Robot made a mistake! Call the dev!");
                System.out.println("Robot made a mistake! Call the dev!");
            }
        } else {
            currentPlayer *= -1;
            switch (gameMode) {
                case VS_RANDOM, VS_ROBOT:
                    if (currentPlayer == PLAYER2) {
                        playTurn();
                    }
                default:
                    updateText.setText(currentSymbol() + "'s turn!");
            }
        }
    }

    public static void restartGame() {
        //Default game settings
        gameBoard = new int[3][3];
        remaining = 9;
        winCondition = WinState.PENDING;

        //Default all grid buttons
        visual.getChildren().forEach(x -> {
            if (x.getClass() == Button.class) {
                Button btn = (Button) x;
                btn.setText(emptySymbol.getValue());
                btn.setTextFill(EMPTY_COLOR);
            } else {
                System.out.println("Issue encountered trying to reset the buttons! " + x.getClass());
            }
        });

        //Load starting player
        switch (Game.startMode) {
            case FIRST_START -> currentPlayer = PLAYER1;
            case SECOND_START -> currentPlayer = PLAYER2;
            //random
            default -> {
                if (rand.nextInt() % 2 == 1) {
                    currentPlayer = PLAYER1;
                } else {
                    currentPlayer = PLAYER2;
                }
            }
        }
        updateText.setText(currentSymbol() + "'s turn!");

        System.out.println("~~Game restarted~~");
        resume();
    }

    private static void winSequence() {

        if (winCondition == WinState.DRAW) {
            updateText.setText("Draw! Click 'Restart'");
            scoreTie.set(scoreTie.get() + 1);
        } else {
            animateWin();
            String victor;
            if (winCondition == WinState.PLAYER1) {
                victor = "%s (Player1)".formatted(getPlayerOneSymbol());
                scorePlayer1.set(scorePlayer1.get() + 1);
            } else {
                scorePlayer2.set(scorePlayer2.get() + 1);
                switch (gameMode) {
                    case VS_ROBOT -> victor = "%s (Robot)";
                    case VS_RANDOM -> victor = "%s (Random)";
                    default -> victor = "%s (Player2)";
                }
                victor = victor.formatted(getPlayerTwoSymbol());
            }
            updateText.setText(victor + "'s win! Click 'Restart'");
        }

        System.out.println(scorePlayer1.get());
        System.out.println(scorePlayer2.get());
        System.out.println(scoreTie.get());
    }

    //Update gameGrid visuals to signal winning line(s), winning move
    private static void animateWin() {

        int lastRow = GridPane.getRowIndex(lastPlayed);
        int lastCol = GridPane.getColumnIndex(lastPlayed);
        String lastSymbol = lastPlayed.getText();

        //Store valid buttons
        List<Button> horiz = new ArrayList<>();
        List<Button> verti = new ArrayList<>();
        List<Button> diag1 = new ArrayList<>(); //from top left to bottom right
        List<Button> diag2 = new ArrayList<>(); //from bottom left to top right

        //Whether 'line' is still valid
        boolean horCheck = true; //always true
        boolean verCheck = true; //always true
        boolean diag1Check = false; //default
        boolean diag2Check = false; //default

        //Preliminary check on diagonals
        if (lastRow == lastCol && lastRow == 1) {
            diag1Check = true;
            diag2Check = true;
        } else if (lastRow == lastCol) {
            diag1Check = true;
        } else if (Math.min(lastRow, lastCol) == 0 && Math.max(lastRow, lastCol) == 2) {
            diag2Check = true;
        }

        //Check all lines
        Node[] nodes = visual.getChildren().toArray(new Node[0]);
        for (Node node : nodes) {
            Button btn = (Button) node;
            int btnCol = GridPane.getColumnIndex(node);
            int btnRow = GridPane.getRowIndex(node);

            //Column
            if (verCheck && btnCol == lastCol) {
                if (btn.getText().equals(lastSymbol)) {
                    verti.add(btn);
                } else {
                    verCheck = false;
                }
            }
            //Row
            if (horCheck && btnRow == lastRow) {
                if (btn.getText().equals(lastSymbol)) {
                    horiz.add(btn);
                } else {
                    horCheck = false;
                }
            }
            //Diag1
            if (diag1Check && btnRow == btnCol) { //symmetrical
                if (btn.getText().equals(lastSymbol)) {
                    diag1.add(btn);
                } else {
                    diag1Check = false;
                }
            }
            //Diag2
            if (diag2Check && ((btnRow == btnCol && btnRow == 1) //'center' node, or
                    || Math.min(btnCol, btnRow) == 0 && Math.max(btnCol, btnRow) == 2)) { //'corner' nodes of diag2
                if (btn.getText().equals(lastSymbol)) {
                    diag2.add(btn);
                } else {
                    diag2Check = false;
                }
            }
        }

        List<Pair<Boolean, List<Button>>> checks = new ArrayList<>();
        checks.add(new Pair<>(horCheck, horiz));
        checks.add(new Pair<>(verCheck, verti));
        checks.add(new Pair<>(diag1Check, diag1));
        checks.add(new Pair<>(diag2Check, diag2));

        for (Pair<Boolean, List<Button>> pair : checks) {
            //if passed, change the color fill each corresponding buttonText
            if (pair.getKey()) {
                for (Button btn : pair.getValue()) {
                    btn.setTextFill(winCondition == WinState.PLAYER1 ? PLAYER1_COLOR : PLAYER2_COLOR);
                }
            } //else, check did not pass. next.
        }
        lastPlayed.setTextFill(LAST_COLOR);
    }

    public static void resetScores() {
        scorePlayer1.set(0);
        scorePlayer2.set(0);
        scoreTie.set(0);
    }

    public static void resetSymbols() {
        setSymbols(DEFAULT_SYMBOL_1, DEFAULT_SYMBOL_2, DEFAULT_SYMBOL_EMPTY);
    }

    public static void setSymbols(String p1, String p2, String empty) {
        Set<String> set = new HashSet<>(Arrays.asList(p1, p2, empty));
        if (set.size() != 3) {
            System.out.println("New symbols denied! Duplicate found.");
        }

        playerOneSymbol.set(p1);
        playerTwoSymbol.set(p2);
        emptySymbol.set(empty);

        reloadGridSymbols();
        resume();
    }

    private static void reloadGridSymbols() {
        visual.getChildren().forEach(child -> {
            int x = GridPane.getRowIndex(child);
            int y = GridPane.getColumnIndex(child);
            String val;
            switch (gameBoard[x][y]) {
                case -1 -> val = playerTwoSymbol.getValue();
                case 0 -> val = emptySymbol.getValue();
                default -> val = playerOneSymbol.getValue();
            }
            ((Button) child).setText(val);
        });
    }

    //Troubleshooting
    @Override
    public String toString() {
        String gm = gameMode == VersusMode.VS_ROBOT ? "VS_ROBOT" :
                (gameMode == VersusMode.VS_RANDOM ? "VS_RANDOM" : "VS_PLAYER");
        String sm = startMode == StartMode.FIRST_START ? "FIRST_START" :
                (startMode == StartMode.SECOND_START ? "SECOND_START" : "RANDOM_START");
        String p = currentPlayer == 1 ? "PLAYER1" : "PLAYER2";
        return """
                GameMode: %s
                FirstStart: %s
                CurrentPlayer: %s
                """.formatted(gm, sm, p);
    }

}