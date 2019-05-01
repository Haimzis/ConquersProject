package History;
import GameEngine.GameDescriptor;
import GameObjects.Territory;

import java.io.Serializable;
import java.util.*;

public class MapHistory implements Serializable {
    Map<Integer,Territory> mapHistory;
    public MapHistory(GameDescriptor gameDescriptor) {
        this.mapHistory = new HashMap<>();
        gameDescriptor.getTerritoryMap().forEach((integer, territory) -> this.mapHistory.put(integer,new Territory(territory)));
    }
    //returns reference of the history
    public Map<Integer,Territory> getMapHistory() {
        return mapHistory;
    }
    //returns copy of the history
    public Map<Integer,Territory> getCopyOfMap() {
        Map<Integer,Territory> copyOfUpdatedMap = new HashMap<>();
        this.mapHistory.forEach(((integer, territory) -> copyOfUpdatedMap.put(integer,new Territory(territory))));
        return copyOfUpdatedMap;
    }

}
