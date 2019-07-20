package Server.Utils;

import GameEngine.GameManager;
import GameObjects.Player;
import GameObjects.Territory;
import GameObjects.Unit;

import java.util.List;
import java.util.Map;

public class SingleSimplifiedGameManager {
    private int roundNumber;
    private String gameTitle;
    private int initialFunds , totalCycles , columns , rows;
    private Map<Integer, Territory> territoryMap;
    private Map<String , Unit> unitMap;
    private List<Player> playersList;
    private int maxPlayers;

    public SingleSimplifiedGameManager(GameManager manager) {
        this.roundNumber = manager.getRoundNumber();
        this.gameTitle = manager.getGameTitle();
        this.initialFunds = manager.getGameDescriptor().getInitialFunds();
        this.totalCycles = manager.getGameDescriptor().getTotalCycles();
        this.columns = manager.getGameDescriptor().getColumns();
        this.rows = manager.getGameDescriptor().getRows();
        this.territoryMap = manager.getGameDescriptor().getTerritoryMap();
        this.unitMap = manager.getGameDescriptor().getUnitMap();
        this.playersList = manager.getGameDescriptor().getPlayersList();
        this.maxPlayers = manager.getGameDescriptor().getMaxPlayers();
    }
}
