package SubComponents.Popups.AttackPopup;

import GameEngine.GameEngine;
import MainComponents.AppController;
import SubComponents.Popups.ActionPopupController;
import javafx.fxml.FXML;


public class AttackPopupController implements ActionPopupController {
    private AppController mainController;
    private String typeOfAttack = null;
    private static final String WELL_TIMED = "Well Timed Attack";
    private static final String CALCULATED_RISK = "Calculated Risk Attack";

    @Override
    public void startAction() {
        if(typeOfAttack == WELL_TIMED) {
            startWellTimedAttack();
        }
        else if(typeOfAttack == CALCULATED_RISK) {
            startCalculatedRiskAttack();
        }
        else {
            startNeutralAttack();
        }
    }

    private void startNeutralAttack() {
        if(GameEngine.gameManager.conquerNeutralTerritory()) { // Got the territory
            mainController.getHeaderComponentController().writeIntoTextArea("Territory " + GameEngine.gameManager.getSelectedTerritoryByPlayer().getID() + " has been conquered!" + "\n");
        }
        else { // Failed to get territory ( Army is not big enough)
            mainController.getHeaderComponentController().writeIntoTextArea("Failed to conquer territory number  " + GameEngine.gameManager.getSelectedTerritoryByPlayer().getID() +"\n");
        }
    }

    private void startCalculatedRiskAttack() {
        String infoToSend = createText();
        int attackerWon =  GameEngine.gameManager.attackConqueredTerritoryByCalculatedRiskBattle();
        if(attackerWon == 1) { //Win
            if(GameEngine.gameManager.getSelectedTerritoryByPlayer().getConquerArmyForce() == null) { //Check if there are enough to hold territory

            }
        }
        else if(attackerWon == 0) { //Defeat

        }
        else { // Draw

        }

    }

    private String createText() {
        int defendingArmySize = GameEngine.gameManager.getSelectedTerritoryByPlayer().getConquerArmyForce().getUnits().size();
        String defendingUnitsResult = "Defending army had: " + defendingArmySize + "\n";
        return null;


    }

    private void startWellTimedAttack() {
        int attackerWon =  GameEngine.gameManager.attackConqueredTerritoryByWellTimedBattle();
        if(attackerWon == 1) { //Win
            if(GameEngine.gameManager.getSelectedTerritoryByPlayer().getConquerArmyForce() == null) { //Check if there are enough to hold territory

            }
        }
        else if(attackerWon == 0) { //Defeat

        }
        else { // Draw

        }
    }

    public void setMainController(AppController appController) { this.mainController = appController; }

    @FXML
    public void wellTimedAttackListener() {
        mainController.showBuyUnitsPopup(this);
        typeOfAttack = WELL_TIMED;
    }

    @FXML
    public void calculatedRiskAttackListener() {
        mainController.showBuyUnitsPopup(this);
        typeOfAttack = CALCULATED_RISK;
    }
}
