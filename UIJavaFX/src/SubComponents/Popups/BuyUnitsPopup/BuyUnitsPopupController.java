package SubComponents.Popups.BuyUnitsPopup;

import MainComponents.AppController;
import SubComponents.Popups.ActionPopupController;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

import java.util.regex.Pattern;

public class BuyUnitsPopupController {
    @FXML private MenuButton unitChoices;
    @FXML private TextField amountToBuy;
    @FXML private Label lblError;
    @FXML private Button btnDone;
    @FXML private Label lblFirePower;
    @FXML private Label lblReduction;
    @FXML private Label lblCost;
    @FXML private Label lblTitleCost;
    @FXML private Label lblTitleCompetenceReduction;
    @FXML private Label lblTitleFirePower;

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
        for(String key : mainController.getCurrentGameManager().getGameDescriptor().getUnitMap().keySet()) {
            MenuItem unitToShowItem = new MenuItem(key);
            unitToShowItem.setOnAction(event -> {
                unitChoices.setText(unitToShowItem.getText());
                isUnitSelected=true;
                populateUnitInformation();
                amountToBuy.setDisable(false);
            });
            unitChoices.getItems().add(unitToShowItem);
        }
    }

    private void populateUnitInformation() {
        lblFirePower.setText(Integer.toString(mainController.getCurrentGameManager().getGameDescriptor().getUnitMap().get(unitChoices.getText()).getMaxFirePower()));
        lblReduction.setText(Integer.toString(mainController.getCurrentGameManager().getGameDescriptor().getUnitMap().get(unitChoices.getText()).getCompetenceReduction()));
        lblCost.setText(Integer.toString(mainController.getCurrentGameManager().getGameDescriptor().getUnitMap().get(unitChoices.getText()).getPurchase()));
        lblTitleCost.setVisible(true);
        lblTitleCompetenceReduction.setVisible(true);
        lblTitleFirePower.setVisible(true);
        lblFirePower.setVisible(true);
        lblReduction.setVisible(true);
        lblCost.setVisible(true);
    }

    @FXML
    public void btnDoneStartAction(){
        parentPopupController.startAction();
        mainController.updateInformation();
        mainController.getCurrentGameManager().setSelectedArmyForce(null);
        closePopup();
    }


    @FXML
    public void purchaseBtnAction(){
        if(isUnitSelected) {
            if(amountToBuy.getText().matches("-?\\d+(\\.\\d+)?")){//checks if str is number
                int amount , cost;
                amount = Integer.parseInt(amountToBuy.getText());
                cost = Integer.parseInt(lblCost.getText());
                if(amount >0){
                    if(mainController.getCurrentGameManager().getCurrentPlayerFunds() >= amount*cost) {
                        mainController.getCurrentGameManager().buyUnits(
                                mainController.getCurrentGameManager().getGameDescriptor().getUnitMap().get(unitChoices.getText()),
                                amount);
                        showLabel("Success!");
                        btnDone.setDisable(false);
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
        lblError.setText(s);
        lblError.setVisible(true);
    }

    private void closePopup() {
        Stage stage = (Stage) btnDone.getScene().getWindow();
        stage.close();
    }
}
