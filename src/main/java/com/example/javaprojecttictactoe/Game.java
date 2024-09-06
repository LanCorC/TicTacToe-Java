package com.example.javaprojecttictactoe;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

import java.util.Random;

public class Game {

    private static Game INSTANCE;

    public static enum VersusMode {
        VS_ROBOT,
        VS_RANDOM,
        VS_PLAYER
    }

    public static enum StartMode {
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

    //Dev note: under Controller.class, for text and state changes, use if output == Game.VS_ROBOT, etc
//    public static final int VS_ROBOT = 0;
//    public static final int VS_RANDOM = 1;
//    public static final int VS_PLAYER = 2;
//
//    public static final int FIRST_START = 0;
//    public static final int SECOND_START = 1;
//    public static final int RANDOM_START = 2;

    public static final int PLAYER1 = 1;
    public static final int PLAYER2 = -1;

    private static StartMode startMode = StartMode.FIRST_START;
    private static VersusMode gameMode = VersusMode.VS_ROBOT;
    private static int currentPlayer = PLAYER1;

//    public static final int PENDING = 0;
//    public static final int DRAW = 1;
//    public static final int PLAYER1WIN = 2;
//    public static final int PLAYER2WIN = 3;

    private static WinState winCondition = WinState.PENDING;
    private static String playerOneSymbol = "X";
    private static String playerTwoSymbol = "O";
//    private static int scorePlayer1 = 0;
//    private static int scorePlayer2 = 0;
//    private static int scoreTie = 0;

    private static SimpleIntegerProperty scorePlayer2 = new SimpleIntegerProperty(0);
    private static SimpleIntegerProperty scorePlayer1 = new SimpleIntegerProperty(0);
    private static SimpleIntegerProperty scoreTie = new SimpleIntegerProperty(0);

    public static int getScorePlayer2() {
        return scorePlayer2.get();
    }

    public static SimpleIntegerProperty scorePlayer2Property() {
        return scorePlayer2;
    }

    public static int getScorePlayer1() {
        return scorePlayer1.get();
    }

    public static SimpleIntegerProperty scorePlayer1Property() {
        return scorePlayer1;
    }

    public static int getScoreTie() {
        return scoreTie.get();
    }

    public static SimpleIntegerProperty scoreTieProperty() {
        return scoreTie;
    }
//    public static int getScorePlayer1() {
//        return scorePlayer1;
//    }
//
//    public static int getScorePlayer2() {
//        return scorePlayer2;
//    }
//
//    public static int getScoreTie() {
//        return scoreTie;
//    }

    //the parent scene
    private static Node root = null;
    private static GridPane visual = null;
    private static Label updateText = null;

    //Dev note: we will use Button.getColumn() and .getRow to find the appropriate cell
    private static int[][] gameBoard = new int[3][3]; //defaults 0 = empty, 1 = player1, -1 = player2
    private static int remaining = 9;
    private static final Random rand = new Random();

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
        BorderPane temp = (BorderPane) Game.root;
        updateText = (Label) temp.getBottom();
        VBox vtemp = (VBox) temp.getCenter();
        vtemp.getChildren().forEach(child->{
            if(child.getClass() == GridPane.class) {
                visual = (GridPane) child;
            }
        });
    }

    public static String getPlayerOneSymbol() {
        return playerOneSymbol;
    }

    public static String getPlayerTwoSymbol() {
        return playerTwoSymbol;
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

    //note: Pair<row, column>; for automating the robot
    public static void playTurn() {
        if(winCondition != WinState.PENDING) {
            //do nothing
        } else if(gameMode == VersusMode.VS_ROBOT) {
            playRobot();
        } else {
            playRandom();
        }
    }

    public static void resume() {
        if(currentPlayer != PLAYER1 && gameMode != VersusMode.VS_PLAYER) {
            playTurn();
        }
    }

    public static void playRobot() {
        Pair<Integer, Integer> move = canWin(currentPlayer);
        if(move != null) {
            play(move);
            return;
        }
        move = canWin(opponent());
        if(move != null) {
            play(move);
            return;
        }

        //if cannot win, or deny, play random
        playRandom();
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
    }

    private static String currentSymbol() {
        return currentPlayer == PLAYER1 ? playerOneSymbol : playerTwoSymbol;
    }


    public static void play(Pair<Integer, Integer> turn) {
        if(winCondition != WinState.PENDING) {
            //do nothing
            return;
        }

        if(!validTurn(turn)) {
            System.out.println(currentPlayer + " tried to play: " + turn);
            nextTurn(true);
            return; //skips all processing
        }
        remaining--;
        System.out.println(currentSymbol() + " played Row: %d Col: %d".formatted(turn.getKey(), turn.getValue()));
        gameBoard[turn.getKey()][turn.getValue()] = currentPlayer;

        //Not efficient, but re-uses code
        if(winner()) winCondition = currentPlayer == PLAYER1 ? WinState.PLAYER1 : WinState.PLAYER2;

//        GridPane gp = (GridPane) ((BorderPane) root).getCenter();
        visual.getChildren().forEach((x)->{
            if(GridPane.getColumnIndex(x).equals(turn.getValue()) && GridPane.getRowIndex(x).equals(turn.getKey())) {
                ((Button) x).setText(currentSymbol());
                //Todo: can be improved by turning into list, For each, break once found
            }
        });

        if(winCondition == WinState.PENDING && remaining == 0) {
            winCondition = WinState.DRAW;
        } else if(winCondition == WinState.PENDING) {
            nextTurn();
            return;
        }

        winSequence();
    }

    //Checks the board for winning line/s
    //Space available to animate (recolor) winning lines
    private static boolean winner() {
        int countRow;
        int countCol;
        //check rows
        for(int i = 0; i < 3; i++) {
            countRow = 0;
            countCol = 0;
            for(int j = 0; j < 3; j++) {
                //row
                if(gameBoard[i][j] == currentPlayer) {
                    countRow++;
                } else {
                    countRow = -3;
                }
                //column
                if(gameBoard[j][i] == currentPlayer) {
                    countCol++;
                } else {
                    countCol = -3;
                }
            }
            if(countRow == 3 || countCol == 3){
                return true;
            }
        }

        //downslant
        int countDown = 0; //down slant, left right
        int countUp = 0; //up slant, left to right
        for(int i = 0; i < 3; i++) {
            if(gameBoard[i][i] == currentPlayer) {
                countDown++;
            } else {
                countDown = -3;
            }

            if(gameBoard[2-i][i] == currentPlayer) {
                countUp++;
            } else {
                countUp = -3;
            }
        }
        if(countDown == 3 || countUp == 3) return true;

        return false;
    }

    //Method to make a winning play, or deny a win, where possible
    //input 'player', so i can use 'another player' to check if THEY can win, for purposes of blocking a win
    private static Pair<Integer, Integer> canWin(int player) {
        //Tally and final result
        int count;
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
                    move = new Pair<>(j, i);
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

    //Win condition applied
    private static boolean validate(int count, Pair<Integer, Integer> move) {
        if(count == 2 && move != null) {
            return true;
        } else if (count == 3) {
            System.out.println("You should win here somewhere!");
            winCondition = currentPlayer == PLAYER1 ? WinState.PLAYER1 : WinState.PLAYER2;
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
//        Label lb = (Label) bp.getBottom();
        if(badMove) {
            //On player illegalMove
            updateText.setText("Bad move, " + currentSymbol() + ". Try again!");
            //Troubleshooting
            if(currentPlayer == PLAYER2 && gameMode != VersusMode.VS_PLAYER) {
                updateText.setText("Robot made a mistake! Call the dev!");
                System.out.println("Robot made a mistake! Call the dev!");
            }
        } else {
            currentPlayer *= -1;
            switch(gameMode) {
                case VS_RANDOM, VS_ROBOT:
                    if(currentPlayer == PLAYER2) {
//                        updateText.setText(currentSymbol() + "'s turn!");
                        playTurn();
                    }
                    break;
                default:
                    updateText.setText(currentSymbol() + "'s turn!");
            }

        }
    }

    public static void restartGame() {
        //reset all text of grid, reset gameBoard, reset turn order
        //Game class gameBoard
        gameBoard = new int[3][3];
        currentPlayer = PLAYER1;
        remaining = 9;
        winCondition = WinState.PENDING;
        //UI gameBoard
//        BorderPane bp = (BorderPane) root;
//        GridPane gridPane = (GridPane) bp.getCenter();
        visual.getChildren().forEach(x-> {
            if(x.getClass() == Button.class) {
                Button btn = (Button) x;
                btn.setText("~");
            } else {
                System.out.println("Issue encountered trying to reset the buttons! " + x.getClass());
            }
        });
        updateText.setText(currentSymbol() + "'s turn!");
        System.out.println("~~Game restarted~~");
    }

    private static void winSequence() {
        //TODO: regardless, game must restart or temporarily hold

        //Update text
//        Label lb = ((Label) ((BorderPane) root).getBottom());
        if(winCondition == WinState.DRAW) {
            updateText.setText("Draw! [Restart or Wait... W.I.P.]");
            scoreTie.set(scoreTie.get()+1);
        } else {
            String victor;
            if(winCondition == WinState.PLAYER1) {
                victor = "Player1";
                scorePlayer1.set(scorePlayer1.get()+1);
            } else {
                scorePlayer2.set(scorePlayer2.get()+1);
                switch (gameMode) {
                    case VS_ROBOT -> victor = "Robot";
                    case VS_RANDOM -> victor = "Random";
                    default -> victor = "Player2";
                }
            }
            updateText.setText(victor + "'s win! [Restart or Wait... W.I.P.]");
        }

        System.out.println(scorePlayer1.get());
        System.out.println(scorePlayer2.get());
        System.out.println(scoreTie.get());

        //TODO: animate winning line/s ?

        //TODO: prepare game restart after a few seconds (settings dependent?), prepare tally?

    }

    private static void resetScores() {
        scorePlayer1.set(0);
        scorePlayer2.set(0);
        scoreTie.set(0);
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

    //singleton? private constructor?

    //will include methods for: win validation, (return bool)
    //will include methods for: Bot behavior? random() or robot() and this will return what node to turn;
    //this will house the Settings, for any setting changes + determining if allowing player2, or immediately performing 'AI's turn

    //clicking 'settings' will query this class for its current settings, e.g. "currently [vs Random], [RandomStart], [etc]"
}