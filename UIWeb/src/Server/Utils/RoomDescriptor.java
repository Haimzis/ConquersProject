package Server.Utils;

import GameEngine.GameManager;
import GameObjects.GameStatus;
import GameObjects.Player;

import java.util.ArrayList;
import java.util.Iterator;
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
    private GameManager manager;


    public RoomDescriptor(GameManager manager , String creator) {
        this.manager = manager;
        this.id = manager.getGameManagerID();
        this.rows = manager.getGameDescriptor().getRows();
        this.cols = manager.getGameDescriptor().getColumns();
        this.registeredPlayers = 0;
        this.requiredPlayers = manager.getGameDescriptor().getMaxPlayers();
        this.moves = manager.getGameDescriptor().getTotalCycles();
        this.gameTitle = manager.getGameTitle();
        this.creatorName = creator;
        this.activePlayers = new ArrayList<>();
        this.status = GameStatus.WaitingForPlayers;
    }

    public GameManager getManager() {
        return manager;
    }

    public void addPlayer(Player newPlayer) {
        activePlayers.add(newPlayer);
        registeredPlayers++;
    }
    public void checkStatus() {
        if(registeredPlayers == requiredPlayers) {
            status = GameStatus.Running;
        }
    }
    public boolean hasPlayerByName(String userName) {
        boolean[] result = new boolean[1];
        activePlayers.forEach(player -> {
            if(player.getPlayerName().equals(userName)) {
                result[0] = true;
            }
        });
        return result[0];
    }
    public void removePlayerByUserName(String userName) {
        Player playerToRemove = null;
        for (Player player : this.activePlayers) {
            if (player.getPlayerName().equals(userName)) {
                playerToRemove = player;
                break;
            }
        }
        if(playerToRemove != null) {
            activePlayers.remove(playerToRemove);
            registeredPlayers--;
        }
    }
}
