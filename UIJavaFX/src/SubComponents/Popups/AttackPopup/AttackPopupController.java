package SubComponents.Popups.AttackPopup;

import GameEngine.GameEngine;
import MainComponents.AppController;
import SubComponents.Popups.ActionPopupController;
import javafx.fxml.FXML;

import java.util.InputMismatchException;
import java.util.Scanner;

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
        if(GameEngine.gameManager.conquerNeutralTerritory()) { // Got the territor

        }
        else { // Failed to get territory ( Army is not big enough)

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
