package GameObjects;

import java.io.Serializable;

public class Unit implements Serializable {
    private String type;
    private int rank;
    private int purchase;
    private int maxFirePower;
    private int competenceReduction;
    private int currentFirePower;

    public Unit(String type, int rank, int purchase, int maxFirePower, int competenceReduction) {
        this.type = type;
        this.rank = rank;
        this.purchase = purchase;
        this.maxFirePower = maxFirePower;
        this.competenceReduction = competenceReduction;
        this.currentFirePower = maxFirePower;
    }
    public Unit(Unit unit) {
        this.type = unit.getType();
        this.rank = unit.getRank();
        this.purchase = unit.getPurchase();
        this.maxFirePower = unit.getMaxFirePower();
        this.competenceReduction = unit.getCompetenceReduction();
        this.currentFirePower = unit.getCurrentFirePower();
    }

    //**************************//
    /*    Getters & Setters     */
    //**************************//
    public int getRank() {return rank;}
    public String getType() {
        return type;
    }
    public int getPurchase() {
        return purchase;
    }
    public int getMaxFirePower() {
        return maxFirePower;
    }
    public int getCurrentFirePower() {
        return currentFirePower;
    }
    public int getCompetenceReduction() {
        return competenceReduction;
    }

    //**************************//
    /*          Methods         */
    //**************************//
    public double calculateRehabilitationPrice() {
        return ((double)this.purchase / (double)this.maxFirePower)*(this.getMaxFirePower() - this.getCurrentFirePower());
    }
    public void reduceCompetence() {
        if(this.currentFirePower >= competenceReduction)
            this.currentFirePower -= competenceReduction;
        else //The unit should die next turn!
            this.type = "Dead";
    }
    public void reduceCompetenceByPercent(double lossPercentage) {
        if(this.currentFirePower >= competenceReduction)
            this.currentFirePower = (int)((currentFirePower* lossPercentage)+0.5);
        else //The unit should die next turn!
            this.type = "Dead";
    }
    public void rehabilitateUnit() {currentFirePower = maxFirePower;}
}
