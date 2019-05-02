package GameObjects;

public class WellTimedBattle extends Battle {
    private int StrongestRank;
    public WellTimedBattle(Army newConquerArmy, Army newAttackingArmy, Territory newBattleTerritory,int StrongestRank){
        super(newConquerArmy,newAttackingArmy,newBattleTerritory);
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
                    .forEach(unit->unit.reduceCompetenceByPercent(0));
            currentConquerArmy.getUnits().stream()
                    .filter(unit ->currentRank + 1 == unit.getRank())
                    .limit(unitsTypesCounterOfAttackingArmy[currentRank])
                    .forEach(unit->unit.reduceCompetenceByPercent(0));
        }
        //Update result
        for(int unitsRank = StrongestRank -1 ; unitsRank >= 0; unitsRank-- ){
            int temp = unitsTypesCounterOfAttackingArmy[unitsRank];
            unitsTypesCounterOfAttackingArmy[unitsRank] -= unitsTypesCounterOfCurrentConquerArmy[unitsRank];
            unitsTypesCounterOfCurrentConquerArmy[unitsRank] -= temp;
            if(unitsTypesCounterOfAttackingArmy[unitsRank] > unitsTypesCounterOfCurrentConquerArmy[unitsRank]){
                isAttackSucceed = true;
                return;
            }
            else if(unitsTypesCounterOfAttackingArmy[unitsRank] < unitsTypesCounterOfCurrentConquerArmy[unitsRank]){
                isAttackSucceed = false;
                return;
            }
        }
        isAttackSucceed = true;
    }
}
