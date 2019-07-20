package Server.Utils;

import GameObjects.Unit;

import java.util.ArrayList;
import java.util.List;

public class BuyUnitsMessage {
    private List<Unit> unitsBought = new ArrayList<>();
    private int unitsBoughtAmount;
    private int fundsAfterPurchase;
    private String actionType;
    private String buyerName;

    public BuyUnitsMessage(List<Unit> unitsBought , int fundsAfterPurchase , String actionType , String buyerName) {
        this.unitsBought = unitsBought;
        this.fundsAfterPurchase = fundsAfterPurchase;
        this.unitsBoughtAmount = unitsBought.size();
        this.actionType = actionType;
        this.buyerName = buyerName;
    }
}
