package Server.Utils;

import GameObjects.Army;

public class TerritoryActionMessage {
    private boolean success;
    private boolean draw;
    private int targetTerritoryId;
    private Army conqueringArmy;
    private Army defendingArmy;
    private String conquerorName;

    //Neutral
    public TerritoryActionMessage(boolean success, int targetTerritoryId, Army conqueringArmy, String conquerorName) {
        this.success = success;
        this.targetTerritoryId = targetTerritoryId;
        this.conqueringArmy = conqueringArmy;
        this.conquerorName = conquerorName;
    }

    //Enemy
    public TerritoryActionMessage(boolean success, int targetTerritoryId, Army conqueringArmy, Army defendingArmy, String conquerorName) {
        this.success = success;
        this.targetTerritoryId = targetTerritoryId;
        this.conqueringArmy = conqueringArmy;
        this.defendingArmy = defendingArmy;
        this.conquerorName = conquerorName;
    }

    //Rehabilitate || Enforce
    public TerritoryActionMessage(boolean success, int targetTerritoryId) {
        this.success = success;
        this.targetTerritoryId = targetTerritoryId;
    }

    public TerritoryActionMessage(boolean draw, int targetTerritoryId, Army conqueringArmy , Army defendingArmy) {
        this.draw = draw;
        this.targetTerritoryId = targetTerritoryId;
        this.conqueringArmy = conqueringArmy;
        this.defendingArmy = defendingArmy;
    }
}
