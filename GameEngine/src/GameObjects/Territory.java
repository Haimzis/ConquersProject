package GameObjects;

import Events.EventObject;
import Events.EventTerritoryReleased;
import java.io.Serializable;

public class Territory implements Serializable {
    private int ID;
    private int armyThreshold;
    private int profit;
    private Player conquer;
    private Army conquerArmyForce;

    public Territory(int ID,int profit, int armyThreshold) {
        this.ID= ID;
        this.armyThreshold= armyThreshold;
        this.profit = profit;
        this.conquerArmyForce= null;
        this.conquer = null;
    }
    public Territory(Territory territory) {
        this.ID= territory.getID();
        this.armyThreshold= territory.getArmyThreshold();
        this.profit = territory.getProfit();
        if(territory.getConquerArmyForce()!= null)
            this.conquerArmyForce= new Army(territory.getConquerArmyForce());
        if(territory.getConquer() != null)
            this.conquer = new Player(territory.getConquer());
    }
    //**************************//
    /*    Getters & Setters     */
    //**************************//
    public int getArmyThreshold() {
        return armyThreshold;
    }
    public int getProfit() {
        return profit;
    }
    public int getID() {
        return ID;
    }
    public Army getConquerArmyForce() {return conquerArmyForce;}
    public Player getConquer() {
        return conquer;
    }
    public void setConquer(Player conquer) {
        this.conquer = conquer;
    }
    public void setConquerArmyForce(Army conquerArmyForce) {
        this.conquerArmyForce = conquerArmyForce;
    }

    //**************************//
    /*          Methods         */
    //**************************//
    @Override
    public boolean equals(Object o) {

        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

        /* Check if o is an instance of Complex or not
          "null instanceof [type]" also returns false */
        if (!(o instanceof Territory)) {
            return false;
        }

        // typecast o to Complex so that we can compare data members
        Territory c = (Territory) o;

        // Compare the data members and return accordingly
        return this.ID == c.getID();
    }
    public Boolean isConquered() {
        return conquer != null;
    }
    //Returns if Conquer GameObjects.Army is too weak to hold this territory returns True. Else False.
    public Boolean isArmyTotalPowerUnderThreshold() {
        return conquerArmyForce.getTotalPower() < this.getArmyThreshold();
    }
    //Update Conquer,And his army to null. remove territory from conquer list.
    public EventObject eliminateThisWeakArmy() {
        conquerArmyForce.destroyArmy();
        conquerArmyForce=null;
        conquer=null;
        return new EventTerritoryReleased(this.ID);
    }

    //After fight, removes territory from conquer list- but pay him Funds as units amount
    public EventObject xChangeFundsForUnitsAndHold() {
        conquer.incrementFunds(conquerArmyForce.getArmyValueInFunds());
        return eliminateThisWeakArmy();
    }
    //update GameObjects.Army competence of territory
    public void reduceCompetence() {
        conquerArmyForce.reduceCompetence();
    }
    public void rehabilitateConquerArmy() { conquerArmyForce.rehabilitateArmy();}
    public int getRehabilitationArmyPriceInTerritory()
    {
        return this.conquerArmyForce.calculateRehabilitationPrice();
    }
}
