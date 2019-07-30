package Server.Utils;

import GameObjects.Army;

public class TerritoryActionMessage {
    private boolean success;
    private boolean draw;
    private boolean couldNotHold;
    private int targetTerritoryId;
    private Army conqueringArmy;
    private Army defendingArmy;
    private String conquerorName;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isDraw() {
        return draw;
    }

    public void setDraw(boolean draw) {
        this.draw = draw;
    }

    public int getTargetTerritoryId() {
        return targetTerritoryId;
    }

    public void setTargetTerritoryId(int targetTerritoryId) {
        this.targetTerritoryId = targetTerritoryId;
    }

    public Army getConqueringArmy() {
        return conqueringArmy;
    }

    public void setConqueringArmy(Army conqueringArmy) {
        this.conqueringArmy = conqueringArmy;
    }

    public Army getDefendingArmy() {
        return defendingArmy;
    }

    public void setDefendingArmy(Army defendingArmy) {
        this.defendingArmy = defendingArmy;
    }

    public String getConquerorName() {
        return conquerorName;
    }

    public void setConquerorName(String conquerorName) {
        this.conquerorName = conquerorName;
    }

    //Neutral
    public TerritoryActionMessage(boolean success, int targetTerritoryId, Army conqueringArmy, String conquerorName) {
        this.success = success;
        this.targetTerritoryId = targetTerritoryId;
        this.conqueringArmy = conqueringArmy;
        this.conquerorName = conquerorName;
    }

    //Enemy
    public TerritoryActionMessage(boolean success, int targetTerritoryId, Army conqueringArmy, Army defendingArmy, String conquerorName , boolean couldNotHold) {
        this.success = success;
        this.targetTerritoryId = targetTerritoryId;
        this.conqueringArmy = conqueringArmy;
        this.defendingArmy = defendingArmy;
        this.conquerorName = conquerorName;
        this.couldNotHold = couldNotHold;
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
