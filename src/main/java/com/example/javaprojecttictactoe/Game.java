package com.example.javaprojecttictactoe;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.util.Pair;
import java.util.Random;

public class Game {

    private static Game INSTANCE;

    //Dev note: under Controller.class, for text and state changes, use if output == Game.VS_ROBOT, etc
    public static final int VS_ROBOT = 0;
    public static final int VS_RANDOM = 1;
    public static final int VS_PLAYER = 2;

    public static final int FIRST_START = 0;
    public static final int SECOND_START = 1;
    public static final int RANDOM_START = 2;

    public static final int PLAYER1 = 1;
    public static final int PLAYER2 = -1;

    private static final int startMode = FIRST_START;
    private static final int gameMode = VS_ROBOT;
    private static int currentPlayer = PLAYER1;
    public static String symbol = "X";
    //the parent scene
    private static Node root = null;

    //Dev note: we will use Button.getColumn() and .getRow to find the appropriate cell
    private int[][] gameBoard = new int[3][3]; //defaults 0 = empty, 1 = player1, -1 = player2
    //TBD if required; notice there are only 9 tiles, i can use this to determine RNG modulo
    private int remaining = 9;
    private Random rand = new Random();

    private Game() {
    }

    public static Game getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new Game();
        }
        return INSTANCE;
    }

    public void setRoot(Node root) {
        this.root = root;
    }

    public static int getGameMode() {
        return gameMode;
    }

    public static int getStartMode() {
        return startMode;
    }

    //note: Pair<row, column>; for automating the robot
    public static Pair<Integer, Integer> playTurn() {
        if(gameMode == VS_ROBOT) {
            return playRobot();
        } else {
            return playRandom();
        }
    }

    public static Pair<Integer, Integer> playRobot() {
        Pair<Integer, Integer> move = canWin(currentPlayer);
        if(move != null) {
            return play(move);
        }
        move = canWin(opponent());
        if(move != null) {
            return play(move);
        }

        //if cannot win, or deny, play random
        return playRandom();
    }

    public static Pair<Integer, Integer> playRandom() {
        //TODO:
        nextTurn();
        return null;
    }


    public static Pair<Integer, Integer> play(Pair<Integer, Integer> turn) {
        //TODO:
        nextTurn();
        return null;
    }

    //input 'player', so i can use 'another player' to check if THEY can win
    private static Pair<Integer, Integer> canWin(int player) {
        //TODO:
        //checks all rows, cols, diagonals, if "Current Player" can win

        return null;
    }

    private static int opponent() {
        return currentPlayer*-1;
    }

    //called at the end of a turn to 'switch' players
    //option in the future to select which symbol?
    private static void nextTurn() {
        currentPlayer *= -1;
        symbol = symbol.equals("X") ? "O" : "X";

        BorderPane bp = (BorderPane) root;
        Label lb = (Label) bp.getBottom();
        lb.setText(symbol + "'s turn!");
    }

    private void nextText() {

    }

    public static void restartGame() {
        //reset all text of grid, reset gameBoard, reset turn order
    }

    @Override
    public String toString() {
        String gm = gameMode == 0 ? "VS_ROBOT" :
                (gameMode == 1 ? "VS_RANDOM" : "VS_PLAYER");
        String sm = startMode == 0 ? "FIRST_START" :
                (startMode == 1 ? "SECOND_START" : "RANDOM_START");
        String p = currentPlayer == 1 ? "PLAYER1" : "PLAYER2";
        return """
                GameMode: %s
                FirstStart: %s
                CurrentPlayer: %s
                """.formatted(gm, sm, p);
    }

    //singleton? private constructor?

    //will include methods for: win validation, (return bool)
    //will include methods for: Bot behavior? random() or robot() and this will return what node to turn;
    //this will house the Settings, for any setting changes + determining if allowing player2, or immediately performing 'AI's turn

    //clicking 'settings' will query this class for its current settings, e.g. "currently [vs Random], [RandomStart], [etc]"
}
