package GameObjects;

import java.util.Random;

public class CalculatedRiskBattle extends Battle{

    public CalculatedRiskBattle(Army newConquerArmy, Army newAttackingArmy, Territory newBattleTerritory){
        super(newConquerArmy,newAttackingArmy,newBattleTerritory);
    }

    @Override
    //updates players armies after battle
    public void startBattle() {
        if(isAttackSucceed()){//updates stats after attacker is won
            battleTerritory.getConquer().getTerritoriesID().remove(new Integer(battleTerritory.getID())); //Removes Defeated Conquer Army
            if(attackingArmy.getTotalPower() >= currentConquerArmy.getTotalPower()) // Goliath effect
                attackingArmy.reduceCompetenceByPercent(1-((double)currentConquerArmy.getTotalPower()) / ((double)attackingArmy.getTotalPower()));
            else attackingArmy.reduceCompetenceByPercent(0.5);
            battleTerritory.setConquerArmyForce(attackingArmy);
            attackResult = 1;
        }
        else {//updates stats after attacker is lost
            if(attackingArmy.getTotalPower() <= currentConquerArmy.getTotalPower())
                currentConquerArmy.reduceCompetenceByPercent(1-((double)attackingArmy.getTotalPower()) / ((double)currentConquerArmy.getTotalPower()));
            else currentConquerArmy.reduceCompetenceByPercent(0.5);
            attackResult = 0;
        }

    }

    //returns if attacker is won, relies on the random calculations
    private boolean isAttackSucceed() {
        int totalArmiesForces = currentConquerArmy.getTotalPower() + attackingArmy.getTotalPower();
        Random rand = new Random();
        int randomSide = rand.nextInt(totalArmiesForces) +1;
        return randomSide > currentConquerArmy.getTotalPower();
    }
}