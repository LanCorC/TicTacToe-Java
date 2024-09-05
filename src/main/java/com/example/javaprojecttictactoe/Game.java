package com.example.javaprojecttictactoe;

public class Game {

    //Dev note: under Controller.class, for text and state changes, use if output == Game.VS_ROBOT, etc
    public static final int VS_ROBOT = 0;
    public static final int VS_RANDOM = 1;
    public static final int VS_PLAYER = 2;

    private static int gameMode = VS_ROBOT;

    public static int getGameMode() {
        return gameMode;
    }

    public static final int FIRST_START = 0;
    public static final int SECOND_START = 1;
    public static final int RANDOM_START = 2;

    private static int startMode = FIRST_START;

    public static int getStartMode() {
        return startMode;
    }

    //Dev note: we will use Button.getColumn() and .getRow to find the appropriate cell
    private int[][] gameBoard = new int[3][3]; //defaults 0 = empty, 1 = player1, 2 = player2

    private Game() {
    }

    //singleton? private constructor?

    //will include methods for: win validation, (return bool)
    //will include methods for: Bot behavior? random() or robot() and this will return what node to turn;
    //this will house the Settings, for any setting changes + determining if allowing player2, or immediately performing 'AI's turn

    //clicking 'settings' will query this class for its current settings, e.g. "currently [vs Random], [RandomStart], [etc]"
}
