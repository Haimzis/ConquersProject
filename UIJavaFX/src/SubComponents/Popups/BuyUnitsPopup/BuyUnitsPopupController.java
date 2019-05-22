package SubComponents.Popups.BuyUnitsPopup;

import GameEngine.GameEngine;
import MainComponents.AppController;
import SubComponents.Popups.ActionPopupController;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

import java.util.regex.Pattern;

public class BuyUnitsPopupController {
    @FXML private Label sumLbl;
    @FXML private MenuButton unitChoices;
    @FXML private TextField amountToBuy;
    @FXML private Label errorLabel;
    @FXML private Button doneBtn;
    @FXML private Label powerLbl;
    @FXML private Label reductionLbl;
    @FXML private Label costLbl;
    @FXML private Label cost;
    @FXML private Label competence_reduction;
    @FXML private Label fire_power;

    private AppController mainController;
    private boolean isUnitSelected=false;
    private ActionPopupController parentPopupController;

    public void setParentController(ActionPopupController parent) {this.parentPopupController = parent; }
    public void setMainController(AppController mainController) { this.mainController = mainController; }

    public void buildUnitDropdownList() {
        //Set formatter for amount textfield
        amountToBuy.setTextFormatter(new TextFormatter<>(
                new IntegerStringConverter(),
                0,
                c -> Pattern.matches("\\d*", c.getText()) ? c : null ));

        //Build dropdown list of units and bind the total label
        for(String key : mainController.getGameEngine().getDescriptor().getUnitMap().keySet()) {
            MenuItem unitToShowItem = new MenuItem(key);
            unitToShowItem.setOnAction(event -> {
                unitChoices.setText(unitToShowItem.getText());
                isUnitSelected=true;
                populateUnitInformation();
                amountToBuy.setDisable(false);
            });
            unitChoices.getItems().add(unitToShowItem);
        }

        //bindTotal();
    }

    private void bindTotal() {
        //Bind the total label
        IntegerProperty price = new SimpleIntegerProperty();
        IntegerProperty quantity = new SimpleIntegerProperty();
        IntegerProperty total = new SimpleIntegerProperty();



        total.bind(price.multiply(quantity));
        sumLbl.textProperty().bind(total.asString());
    }


    private void populateUnitInformation() {
        powerLbl.setText(Integer.toString(mainController.getGameEngine().getDescriptor().getUnitMap().get(unitChoices.getText()).getMaxFirePower()));
        reductionLbl.setText(Integer.toString(mainController.getGameEngine().getDescriptor().getUnitMap().get(unitChoices.getText()).getCompetenceReduction()));
        costLbl.setText(Integer.toString(mainController.getGameEngine().getDescriptor().getUnitMap().get(unitChoices.getText()).getPurchase()));
        cost.setVisible(true);
        competence_reduction.setVisible(true);
        fire_power.setVisible(true);
        powerLbl.setVisible(true);
        reductionLbl.setVisible(true);
        costLbl.setVisible(true);
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
                int amount , cost;
                amount = Integer.parseInt(amountToBuy.getText());
                cost = Integer.parseInt(costLbl.getText());
                if(amount >0){
                    if(GameEngine.gameManager.getCurrentPlayerFunds() >= amount*cost) {
                        GameEngine.gameManager.buyUnits(
                                mainController.getGameEngine().getDescriptor().getUnitMap().get(unitChoices.getText()),
                                amount);
                        showLabel("Success!");
                        doneBtn.setDisable(false);
                    }
                    else {
                        //do some exception : amount is not not enough
                        showLabel("Not enough funds");
                    }
                }
                else {
                    //do some exception : amount is negative
                    showLabel("Invalid amount.");
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
