package Server.Utils;

import GameObjects.Unit;

import java.util.ArrayList;
import java.util.List;

public class BuyUnitsMessage {
    private List<Unit> unitsBought = new ArrayList<>();
    private int unitsBoughtAmount;
    private int fundsAfterPurchase;
    private String buyerName;
    private boolean success;

    public BuyUnitsMessage(List<Unit> unitsBought , int fundsAfterPurchase , String buyerName , boolean success) {
        this.unitsBought = unitsBought;
        this.fundsAfterPurchase = fundsAfterPurchase;
        this.unitsBoughtAmount = unitsBought.size();
        this.buyerName = buyerName;
        this.success = success;
    }


    public BuyUnitsMessage(boolean success) {
        this.success = success;
    }
}
