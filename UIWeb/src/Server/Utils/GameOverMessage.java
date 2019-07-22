package Server.Utils;

import GameObjects.GameStatus;

public class GameOverMessage {
    private String winningPlayerName;
    private boolean draw;
    private GameStatus status;

    public GameOverMessage(String winningPlayerName, boolean draw, GameStatus status) {
        this.winningPlayerName = winningPlayerName;
        this.draw = draw;
        this.status = status;
    }
}
