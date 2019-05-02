package GameObjects;
import java.io.Serializable;
import java.util.*;

public class Army implements Serializable {
    private List<Unit> units;
    private int totalPower;
    private int potentialTotalPower;

    public Army(List<Unit> newArmy) {
        this.units = newArmy;
        updateArmyStats();
    }

    public Army(Army army) {
        this.units = new ArrayList<>();
        army.getUnits().forEach(unit -> units.add(new Unit(unit)));
        this.totalPower = army.getTotalPower();
        this.potentialTotalPower = army.getPotentialTotalPower();
    }
    public Army() {
        this.units = new ArrayList<>();
        updateArmyStats();
    }

    //**************************//
    /*    Getters & Setters     */
    //**************************//
    public List<Unit> getUnits() {
        return units;
    }
    public int getTotalPower() {
        return totalPower;
    }
    public int getPotentialTotalPower() {
        return potentialTotalPower;
    }

    //**************************//
    /*          Methods         */
    //**************************//
    //evaluate army
    public void updateArmyStats(){
        calculatePotentialTotalPower();
        calculateTotalPower();
    }
    //returns Army value in Funds
    public int getArmyValueInFunds() {
        return Optional.ofNullable(units).orElse(Collections.emptyList()).stream()
                .mapToInt(Unit::getPurchase)
                .sum();
    }
    //enforce army with units
    public void addUnit(Unit newUnit) {
        units.add(newUnit);
        updateArmyStats();
    }
    //reduce units by percents
    public void reduceCompetenceByPercent(double lossPercentage) {
        Optional.ofNullable(units).orElse(Collections.emptyList()).forEach(unit -> unit.reduceCompetenceByPercent(lossPercentage));
        buryDeadUnits();
        updateArmyStats();
    }
    //reduce units by rules definition
    public void reduceCompetence() {
        Optional.ofNullable(units).orElse(Collections.emptyList()).forEach(Unit::reduceCompetence);
        buryDeadUnits();
        updateArmyStats();
    }
    //clear dead units
    public void buryDeadUnits() {
        if(!units.isEmpty()) {
            Iterator unit = units.iterator();
            Unit deadUnit;
            while (unit.hasNext()) {
                deadUnit = (Unit) unit.next();
                if ("Dead".equals(deadUnit.getType())) {
                    unit.remove();
                    break;
                }
            }
        }
    }
    public void calculateTotalPower() {
        this.totalPower=
                Optional.ofNullable(units).orElse(Collections.emptyList()).parallelStream()
                        .mapToInt(Unit::getCurrentFirePower)
                        .sum();
    }
    public void calculatePotentialTotalPower() {
        this.potentialTotalPower=
                Optional.ofNullable(units).orElse(Collections.emptyList()).parallelStream()
                        .mapToInt(Unit::getMaxFirePower)
                        .sum();
    }
    public int calculateRehabilitationPrice() {
        double cost=0;
        if(units.size() == 0) return 0;
        for(Unit unit:units) {
            cost += unit.calculateRehabilitationPrice();
        }
        return (int)cost;
    }
    public void destroyArmy() {
        units = null;
        totalPower=potentialTotalPower=0;
    }
    public void uniteArmies(Army SelectedArmyForce) {
        this.units.addAll(SelectedArmyForce.units);
        SelectedArmyForce.destroyArmy();
        updateArmyStats();
    }
    public void rehabilitateArmy(){
        Optional.ofNullable(units).orElse(Collections.emptyList()).parallelStream().forEach(Unit::rehabilitateUnit);
        updateArmyStats();
    }
}
