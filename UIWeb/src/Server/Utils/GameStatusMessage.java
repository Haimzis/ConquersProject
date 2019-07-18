package Server.Utils;

import GameObjects.GameStatus;

public class GameStatusMessage {
    GameStatus status;
    String currentPlayerTurnName;

    public GameStatusMessage(GameStatus status, String currentPlayerTurnName) {
        this.status = status;
        this.currentPlayerTurnName = currentPlayerTurnName;
    }

    public GameStatus getStatus() {
        return this.status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public String getCurrentPlayerTurnName() {
        return this.currentPlayerTurnName;
    }

    public void setCurrentPlayerTurnName(String currentPlayerTurnName) {
        this.currentPlayerTurnName = currentPlayerTurnName;
    }
}