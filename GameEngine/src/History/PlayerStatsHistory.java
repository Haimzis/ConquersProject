package History;

import GameEngine.GameDescriptor;
import GameObjects.Player;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class PlayerStatsHistory implements Serializable {
    private List<Player> playerStats;
    public PlayerStatsHistory(GameDescriptor gameDescriptor) {
        this.playerStats = new LinkedList<>();
        gameDescriptor.getPlayersList().forEach(player -> this.playerStats.add(new Player(player)));
    }
    //returns reference of the history
    public List<Player> getPlayerStatsHistory() {
        return playerStats;
    }
    //returns copy of the history
    public List<Player> getCopyOfPlayersList() {
        List<Player> copyOfPlayersList = new LinkedList<>();
        this.playerStats.forEach(player -> copyOfPlayersList.add(new Player(player)));
        return copyOfPlayersList;
    }
}
