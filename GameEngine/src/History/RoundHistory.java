package History;
import GameEngine.GameDescriptor;
import GameObjects.Player;
import GameObjects.Territory;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class RoundHistory implements Serializable {
    private MapHistory mapHistory;
    private int roundNumber;
    private PlayerStatsHistory playerStatsHistory;

    public RoundHistory(GameDescriptor gameDescriptor,int roundNumber) {
        this.roundNumber = roundNumber;
        this.mapHistory = new MapHistory(gameDescriptor);
        this.playerStatsHistory = new PlayerStatsHistory(gameDescriptor);
    }
    //returns copy of history
    public Map<Integer,Territory> getCopyOfMap() {
        return mapHistory.getCopyOfMap();
    }
    public List<Player> getCopyOfPlayersList() {
        return playerStatsHistory.getCopyOfPlayersList();
    }
    //returns round number of the history
    public int getRoundNumber(){return roundNumber;}
    //returns references to history
    public Map<Integer, Territory> getMapHistory() {
        return mapHistory.getMapHistory();
    }
    public List<Player> getPlayerStatsHistory() {
        return playerStatsHistory.getPlayerStatsHistory();
    }
}
