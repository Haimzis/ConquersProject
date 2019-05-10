package SubComponents.Popups.AttackPopup;

import GameEngine.GameEngine;
import MainComponents.AppController;
import SubComponents.Popups.ActionPopupController;
import javafx.fxml.FXML;


public class AttackPopupController implements ActionPopupController {
    private AppController mainController;
    private int typeOfAttack = 0;

    @Override
    public void startAction() {
        if(typeOfAttack == 1) {
            startWellTimedAttack();
        }
        else if(typeOfAttack == 2) {
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
        typeOfAttack = 1;
    }

    @FXML
    public void calculatedRiskAttackListener() {
        mainController.showBuyUnitsPopup(this);
        typeOfAttack = 2;
    }
}
