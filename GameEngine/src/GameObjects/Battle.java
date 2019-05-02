package GameObjects;
import java.io.Serializable;

public abstract class Battle implements Serializable {
    protected Army currentConquerArmy,attackingArmy;
    protected Territory battleTerritory;
    protected boolean isAttackSucceed=false;

    Battle(Army newConquerArmy, Army newAttackingArmy, Territory newBattleTerritory){
        this.currentConquerArmy=newConquerArmy;
        this.attackingArmy=newAttackingArmy;
        this.battleTerritory=newBattleTerritory;
    }
    public abstract void startBattle();
    //returns true if Attacker won, Else false
    public boolean getResult() {
        return this.isAttackSucceed;
    }
    //returns if survivor army is strong enough to hold the territory
    public Boolean isWinnerArmyNotStrongEnoughToHoldTerritory() {
        return battleTerritory.isArmyTotalPowerUnderThreshold();
    }

}
