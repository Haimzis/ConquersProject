package Server.Utils;

public class EndGameMessage {
    private int round;
    private String winnerName;

    public EndGameMessage(int round, String winnerName) {
        this.round = round;
        this.winnerName = winnerName;
    }

}
