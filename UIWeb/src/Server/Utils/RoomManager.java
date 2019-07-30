package Server.Utils;

import GameEngine.GameManager;
import GameObjects.GameStatus;
import GameObjects.Player;
import Server.Chat.ChatManager;
import java.util.ArrayList;
import java.util.List;

public class RoomManager {
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
    private GameManager gameManager;
    private ChatManager chatManager;


    public RoomManager(GameManager gameManager, String creator, ChatManager chatManager) {
        this.gameManager = gameManager;
        this.id = gameManager.getGameManagerID();
        this.rows = gameManager.getGameDescriptor().getRows();
        this.cols = gameManager.getGameDescriptor().getColumns();
        this.registeredPlayers = 0;
        this.requiredPlayers = gameManager.getGameDescriptor().getMaxPlayers();
        this.moves = gameManager.getGameDescriptor().getTotalCycles();
        this.gameTitle = gameManager.getGameTitle();
        this.creatorName = creator;
        this.activePlayers = new ArrayList<>();
        this.status = GameStatus.WaitingForPlayers;
        this.chatManager = chatManager;
    }

    public GameManager getGameManager() {
        return gameManager;
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
    public synchronized void removePlayerByUserName(String userName) {
        Player playerToRemove = null;
        for (Player player : this.activePlayers) {
            if (player.getPlayerName().equals(userName)) {
                gameManager.getGameDescriptor().getColors().push(player.getColor());
                playerToRemove = player;
                break;
            }
        }
        if(playerToRemove != null) {
            activePlayers.remove(playerToRemove);
            registeredPlayers--;
        }
    }
    public Player getPlayerByUsername(String userName) {
        List<Player> result = new ArrayList<>();
        activePlayers.forEach(player -> {
            if(player.getPlayerName().equals(userName)) {
                result.add(player);
            }
        });
        return result.get(0);
    }

    public void resetChat() {
        this.chatManager.resetChat();
    }

    public ChatManager getChatManager() {
        return chatManager;
    }
}
