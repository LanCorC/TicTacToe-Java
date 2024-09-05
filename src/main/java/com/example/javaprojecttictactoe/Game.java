package com.example.javaprojecttictactoe;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.util.Pair;
import org.controlsfx.control.spreadsheet.Grid;

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
    private static final int gameMode = VS_RANDOM;
    private static int currentPlayer = PLAYER1;
    public static String symbol = "X";
    //the parent scene
    private static Node root = null;

    //Dev note: we will use Button.getColumn() and .getRow to find the appropriate cell
    private static int[][] gameBoard = new int[3][3]; //defaults 0 = empty, 1 = player1, -1 = player2
    //TBD if required; notice there are only 9 tiles, i can use this to determine RNG modulo
    private static int remaining = 9;
    private static Random rand = new Random();

    private Game() {
    }

    public static Game getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new Game();
        }
        return INSTANCE;
    }

    public void setRoot(Node root) {
        Game.root = root;
    }

    public static int getGameMode() {
        return gameMode;
    }

    public static int getStartMode() {
        return startMode;
    }

    //note: Pair<row, column>; for automating the robot
    public static void playTurn() {
        if(gameMode == VS_ROBOT) {
            playRobot();
        } else {
            playRandom();
        }
    }

    public static void playRobot() {
        Pair<Integer, Integer> move = canWin(currentPlayer);
        if(move != null) {
            play(move);
        }
        move = canWin(opponent());
        if(move != null) {
            play(move);
        }

        //if cannot win, or deny, play random
        playRandom();
//        return null;
    }

    public static void playRandom() {
        //TODO:
        if(remaining == 0) {
            System.out.println("No more turns available(?)!");
            return;
        }

        //rng counts 0, but i need non-zero
        int candidate = 0;
        while(candidate == 0) {
            candidate = (rand.nextInt(remaining+1));
        }

        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                //If not taken,
                if(gameBoard[i][j] == 0 && --candidate == 0) {
                    play(new Pair<>(i, j));
                    return;
                }
            }
        }
        System.out.println("Could not find a random empty spot!");

//        return null;
    }


    public static void play(Pair<Integer, Integer> turn) {
        //TODO:
        if(!validTurn(turn)) {
            System.out.println(currentPlayer + " tried to play: " + turn);
            nextTurn(true);
            return; //skips all processing
        }
        remaining--;
        System.out.println(currentPlayer + " played Row: %d Col: %d".formatted(turn.getKey(), turn.getValue()));
        gameBoard[turn.getKey()][turn.getValue()] = currentPlayer;

        GridPane gp = (GridPane) ((BorderPane) root).getCenter();
        gp.getChildren().forEach((x)->{
            if(GridPane.getColumnIndex(x).equals(turn.getValue()) && GridPane.getRowIndex(x).equals(turn.getKey())) {
                ((Button) x).setText(symbol);
                //Todo: can be improved by turning into list, For each, break once found
            }
        });

        nextTurn();
//        return turn;
    }

    //input 'player', so i can use 'another player' to check if THEY can win
    private static Pair<Integer, Integer> canWin(int player) {
        //TODO:
        //checks all rows, cols, diagonals, if "Current Player" can win

        //Tally and final result
        int count = 0;
        Pair<Integer, Integer> move;

        //Check all rows
        for(int i = 0; i < 3; i++) {
            //Reset tally
            count = 0;
            move = null;
            for(int j = 0; j < 3; j++) {
                if(gameBoard[i][j]==player) {
                    count++;
                } else if(gameBoard[i][j] == 0) {
                    move = new Pair<>(i, j);
                } else {
                    //Disqualify
                    count = 0;
                    break;
                }
            }

            //Validate
            if(validate(count, move)) return move;
        }

        //Check columns
        for(int i = 0; i < 3; i++) {
            count = 0;
            move = null;
            for(int j = 0; j < 3; j++) {
                int num = gameBoard[j][i];
                if(num == player) {
                    count++;
                } else if (num == 0) {
                    move = new Pair<>(i, j);
                } else {
                    count = 0;
                    break;
                }
            }

            //Validate
            if(validate(count, move)) return move;
        }

        //Check diagonal 1:
        count = 0;
        move = null;
        for(int i = 0; i < 3; i++) {
            int num = gameBoard[i][i];
            if(num == player) {
                count++;
            } else if(num == 0) {
                move = new Pair<>(i, i);
            } else {
                count = 0;
                break;
            }
        }
        //validate
        if(validate(count, move)) return move;

        count = 0;
        move = null;
        //Check diagonal 2:
        for(int i = 0; i < 3; i++) {
            int num = gameBoard[2-i][i];
            if(num == player) {
                count++;
            } else if (num == 0) {
                move = new Pair<>(2-i, i);
            } else {
                count = 0;
                break;
            }
        }
        if(validate(count, move)) return move;

        return null;
    }


    private static boolean validate(int count, Pair<Integer, Integer> move) {
        if(count == 2 && move != null) {
            return true;
        } else if (count == 3) {
            //TODO: winning line, e.g. small animation, or msg update! + pause all button pressing until restart, or option to auto-restart and tally wins
            System.out.println("You should win here somewhere!");
        }
        return false;
    }

    private static int opponent() {
        return currentPlayer*-1;
    }

    //Note: Row,Column
    private static boolean validTurn(Pair<Integer, Integer> move) {
        return gameBoard[move.getKey()][move.getValue()] == 0;
    }

    private static void nextTurn() {
        nextTurn(false);
    }

    private static void nextTurn(boolean badMove) {
        BorderPane bp = (BorderPane) root;
        Label lb = (Label) bp.getBottom();
        if(badMove) {
            //Should only trigger on player misclick
            lb.setText("Bad move, " + symbol + ". Try again!");
            //For troubleshooting
            if(currentPlayer == PLAYER2 && gameMode != VS_PLAYER) {
                lb.setText("Robot made a mistake! Call the dev!");
                System.out.println("Robot made a mistake! Call the dev!");
            }
        } else {
            currentPlayer *= -1;
            symbol = symbol.equals("X") ? "O" : "X";
            switch(gameMode) {
                case VS_PLAYER: lb.setText(symbol + "'s turn!");
                    break;
                default:
                    if(currentPlayer == PLAYER2) {
                        playTurn();
                    }

            }

        }
    }

    public static void restartGame() {
        //reset all text of grid, reset gameBoard, reset turn order
        //Game class gameBoard
        gameBoard = new int[3][3];
        currentPlayer = PLAYER1;
        symbol = "X";
        remaining = 9;
        //UI gameBoard
        BorderPane bp = (BorderPane) root;
        GridPane gridPane = (GridPane) bp.getCenter();
        gridPane.getChildren().forEach(x-> {
            if(x.getClass() == Button.class) {
                Button btn = (Button) x;
                btn.setText("=");
            } else {
                System.out.println("hey! " + x.getClass());
            }
        });
        ((Label) bp.getBottom()).setText(symbol + "'s turn!");
    }

    //Troubleshooting
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
