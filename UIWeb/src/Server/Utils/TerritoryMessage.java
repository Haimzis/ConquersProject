package Server.Utils;

public class TerritoryMessage {
    private boolean isNeutral;
    private boolean isConquered;
    private boolean isBelongToCurrentPlayer;
    private boolean isValid;
    private String message;

    public TerritoryMessage(boolean isNeutral, boolean isConquered, boolean isBelongToCurrentPlayer , boolean isValid , String message) {
        this.isNeutral = isNeutral;
        this.isConquered = isConquered;
        this.isBelongToCurrentPlayer = isBelongToCurrentPlayer;
        this.isValid = isValid;
        this.message = message;
    }

}
