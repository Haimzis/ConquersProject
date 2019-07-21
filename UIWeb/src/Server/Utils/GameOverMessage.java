package Server.Utils;

public class GameOverMessage {
    private String winningPlayerName;
    private boolean draw;

    public GameOverMessage(String winningPlayerName, boolean draw) {
        this.winningPlayerName = winningPlayerName;
        this.draw = draw;
    }
}
