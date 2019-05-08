package SubComponents.Popups.BuyUnitsPopup;

import GameEngine.GameEngine;
import MainComponents.AppController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class BuyUnitsPopupController  {
    @FXML private MenuButton unitChoices;
    @FXML private TextField amountToBuy;
    @FXML private Button purchaseBtn;
    @FXML private HBox buyUnitsPopupComponent;
    private AppController mainController;
    private String selectedUnit;
    private boolean isUnitSelected=false;

    public void setMainController(AppController mainController) { this.mainController = mainController; }
    private void setListenerForPressOnMenuItem() {
        List<MenuItem> unitsAvailableToBuy = unitChoices.getItems();
        for(MenuItem unit : unitsAvailableToBuy) {

        }
    }
    public void buildUnitDropdownList() {
        for(String key : mainController.getGameEngine().getDescriptor().getUnitMap().keySet()) {
            MenuItem unitToShowItem = new MenuItem(key);
            unitToShowItem.setOnAction(event -> {
                unitChoices.setText(unitToShowItem.getText());
                selectedUnit = unitToShowItem.getText();
                isUnitSelected=true;
            });
            unitChoices.getItems().add(unitToShowItem);
        }
    }
    @FXML
    public void purchaseBtnAction(){
        if(isUnitSelected) {
            if(amountToBuy.getText().matches("-?\\d+(\\.\\d+)?")){//checks if str is number
                int amount =0;
                amount = Integer.parseInt(amountToBuy.getText());
                if(amount >0){
                    GameEngine.gameManager.buyUnits(
                            mainController.getGameEngine().getDescriptor().getUnitMap().get(unitChoices.getText()),
                            amount);
                }
                else {
                    //do some exception : amount is negative
                }
            }
            else{
                //do some exception : amount isn't number
            }
        }
        else{
            //do some exception : unit didnt selected
        }
    }
}
