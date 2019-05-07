package DataContainersTypes;
import GameObjects.Territory;

import java.util.Map;

public class Board {
    public int getColumns() {
        return columns;
    }

    public int getRows() {
        return rows;
    }

    public Map<Integer, Territory> getTerritoryMap() {
        return territoryMap;
    }

    private int columns;
    private int rows;
    private Map<Integer, Territory> territoryMap;
    
    public Board(int columns, int rows, Map<Integer, Territory> territoryMap){
        this.columns = columns;
        this.rows = rows;
        this.territoryMap = territoryMap;
    } 
}
