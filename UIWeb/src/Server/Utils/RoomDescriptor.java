package Server.Utils;

import GameEngine.GameManager;
import GameObjects.GameStatus;
import GameObjects.Player;

import java.util.ArrayList;
import java.util.List;

public class RoomDescriptor {
    public int id;
    public int rows;
    public int cols;
    public int registeredPlayers;
    public int requiredPlayers;
    public int moves;
    public String gameTitle;
    public String creatorName;
    public List<Player> activePlayers;
    public GameStatus status;


    public RoomDescriptor(GameManager manager) {
        this.id = manager.getGameManagerID();
        this.rows = manager.getGameDescriptor().getRows();
        this.cols = manager.getGameDescriptor().getColumns();
        this.registeredPlayers = manager.getGameDescriptor().getCurrentPlayersInGame();
        this.requiredPlayers = manager.getGameDescriptor().getMaxPlayers();
        this.moves = manager.getGameDescriptor().getTotalCycles();
        this.gameTitle = manager.getGameTitle();
        this.creatorName = manager.creatorName;
        this.activePlayers = new ArrayList<>();
        this.status = manager.getStatus();
    }

    public void addPlayer(Player newPlayer) {
        activePlayers.add(newPlayer);
        registeredPlayers++;
    }
    public void removePlayer(int playerId) {
        activePlayers.remove(playerId);
    }
    public void checkStatus() {
        if(registeredPlayers == requiredPlayers) {
            status = GameStatus.Running;
        }
    }
}
