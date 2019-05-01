package GameObjects;

import java.io.Serializable;
import java.util.Random;

public class Battle implements Serializable {
    private static Army currentConquerArmy=null,attackingArmy=null;
    private static Territory battleTerritory=null;
    //set attacking army, defending army and battleground territory for once with static members
    public static void preparedToBattle(Army newConquerArmy,Army newAttackingArmy,Territory newBattleTerritory) {
        currentConquerArmy=newConquerArmy;
        attackingArmy=newAttackingArmy;
        battleTerritory=newBattleTerritory;
    }

    //updates stats after attacker is lost
    public static void updateArmiesAfterAttackerDefeat()
    {
        if(attackingArmy.getTotalPower() <= currentConquerArmy.getTotalPower())
            currentConquerArmy.reduceCompetenceByPercent(1-((double)attackingArmy.getTotalPower()) / ((double)currentConquerArmy.getTotalPower()));
        else currentConquerArmy.reduceCompetenceByPercent(0.5);
    }
    //updates stats after attacker is won
    public static void updateArmiesAfterAttackerVictory()
    {
        battleTerritory.getConquer().getTerritoriesID().remove(new Integer(battleTerritory.getID())); //Removes Defeated Conquer Army
        if(attackingArmy.getTotalPower() >= currentConquerArmy.getTotalPower()) // Goliat effect
            attackingArmy.reduceCompetenceByPercent(1-((double)currentConquerArmy.getTotalPower()) / ((double)attackingArmy.getTotalPower()));
        else attackingArmy.reduceCompetenceByPercent(0.5);
        battleTerritory.setConquerArmyForce(attackingArmy);
    }
    //returns if attacker is won, relies on the random calculations
    public static boolean isAttackSucceed()
    {
        int totalArmiesForces = currentConquerArmy.getTotalPower() + attackingArmy.getTotalPower();
        Random rand = new Random();
        int randomSide = rand.nextInt(totalArmiesForces) +1;
        return randomSide > currentConquerArmy.getTotalPower();
    }
    //returns if survivor army is strong enough to hold the territory
    public static Boolean isWinnerArmyNotStrongEnoughToHoldTerritory() {
        return battleTerritory.isArmyTotalPowerUnderThreshold();
    }
}
