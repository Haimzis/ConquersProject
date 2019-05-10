package SubComponents.Popups.BuyUnitsPopup;

import GameEngine.GameEngine;
import MainComponents.AppController;
import SubComponents.Popups.ActionPopupController;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class BuyUnitsPopupController {
    @FXML private MenuButton unitChoices;
    @FXML private TextField amountToBuy;
    @FXML private Label errorLabel;
    @FXML private Button doneBtn;

    private AppController mainController;
    private boolean isUnitSelected=false;
    private ActionPopupController parentPopupController;

    public void setParentController(ActionPopupController parent) {this.parentPopupController = parent; }
    public void setMainController(AppController mainController) { this.mainController = mainController; }

    public void buildUnitDropdownList() {
        for(String key : mainController.getGameEngine().getDescriptor().getUnitMap().keySet()) {
            MenuItem unitToShowItem = new MenuItem(key);
            unitToShowItem.setOnAction(event -> {
                unitChoices.setText(unitToShowItem.getText());
                isUnitSelected=true;
            });
            unitChoices.getItems().add(unitToShowItem);
        }
    }

    @FXML
    public void btnDoneStartAction(){
        parentPopupController.startAction();
        mainController.updateInformation();
        closePopup();
    }

    @FXML
    public void purchaseBtnAction(){
        if(isUnitSelected) {
            if(amountToBuy.getText().matches("-?\\d+(\\.\\d+)?")){//checks if str is number
                int amount;
                amount = Integer.parseInt(amountToBuy.getText());
                if(amount >0){
                    if(GameEngine.gameManager.getCurrentPlayerFunds() >= amount) {
                        GameEngine.gameManager.buyUnits(
                                mainController.getGameEngine().getDescriptor().getUnitMap().get(unitChoices.getText()),
                                amount);
                        showLabel("Success!");
                    }
                    else {
                        //do some exception : amount is not not enough
                        showLabel("Not enough funds");
                    }
                }
                else {
                    //do some exception : amount is negative
                    showLabel("Invalid amount , please enter positive number.");
                }
            }
            else{
                //do some exception : amount isn't number
                showLabel("Please enter an valid number");
            }
        }
        else{
            //do some exception : unit isn't selected
            showLabel("Please select a unit to purchase");
        }
    }

    private void showLabel(String s) {
        errorLabel.setText(s);
        errorLabel.setVisible(true);
    }

    private void closePopup() {
        Stage stage = (Stage) doneBtn.getScene().getWindow();
        stage.close();
    }
}
