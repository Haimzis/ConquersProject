package GameObjects;

import java.io.Serializable;

public class WellTimedBattle extends Battle implements Serializable {
    private int StrongestRank;
    public WellTimedBattle(Army newConquerArmy, Army newAttackingArmy, Territory newBattleTerritory,Player currentConquer,int StrongestRank){
        super(newConquerArmy,newAttackingArmy,newBattleTerritory,currentConquer);
        this.StrongestRank = StrongestRank;
    }

    @Override
    public void startBattle() {
        int [] unitsTypesCounterOfAttackingArmy = new int[StrongestRank];
        int [] unitsTypesCounterOfCurrentConquerArmy = new int[StrongestRank];
        for(int unitsRank =0 ;unitsRank < StrongestRank;unitsRank++){
            final int currentRank = unitsRank;
            //Count units by rank
            unitsTypesCounterOfAttackingArmy[currentRank] = (int) attackingArmy.getUnits().stream()
                    .filter(unit -> currentRank + 1 == unit.getRank())
                    .count();
            unitsTypesCounterOfCurrentConquerArmy[currentRank] = (int) currentConquerArmy.getUnits().stream()
                    .filter(unit -> currentRank + 1 == unit.getRank())
                    .count();
            //Kill units by rank
            attackingArmy.getUnits().stream()
                    .filter(unit ->currentRank + 1 == unit.getRank())
                    .limit(unitsTypesCounterOfCurrentConquerArmy[currentRank])
                    .forEach(Unit::killUnit);
            currentConquerArmy.getUnits().stream()
                    .filter(unit ->currentRank + 1 == unit.getRank())
                    .limit(unitsTypesCounterOfAttackingArmy[currentRank])
                    .forEach(Unit::killUnit);
        }
        currentConquerArmy.buryDeadUnits();
        currentConquerArmy.updateArmyStats();
        attackingArmy.buryDeadUnits();
        attackingArmy.updateArmyStats();

        //Update result
        for(int unitsRank = StrongestRank -1 ; unitsRank >= 0; unitsRank-- ){
            int temp = unitsTypesCounterOfAttackingArmy[unitsRank];
            unitsTypesCounterOfAttackingArmy[unitsRank] -= unitsTypesCounterOfCurrentConquerArmy[unitsRank];
            unitsTypesCounterOfCurrentConquerArmy[unitsRank] -= temp;
            if(unitsTypesCounterOfAttackingArmy[unitsRank] > unitsTypesCounterOfCurrentConquerArmy[unitsRank]){//Attacker wins
                attackResult = 1;
                currentConquer.removeTerritory(battleTerritory.getID()); //Removes Defeated Conquer Army
                battleTerritory.setConquerArmyForce(attackingArmy);
                return;
            }
            else if(unitsTypesCounterOfAttackingArmy[unitsRank] < unitsTypesCounterOfCurrentConquerArmy[unitsRank]){//Attacker loss
                attackResult = 0;
                return;
            }
        }
        //DRAW
        currentConquer.removeTerritory(battleTerritory.getID()); //Removes Defeated Conquer Army
        battleTerritory.eliminateThisWeakArmy();
        attackResult=2;
    }
}
