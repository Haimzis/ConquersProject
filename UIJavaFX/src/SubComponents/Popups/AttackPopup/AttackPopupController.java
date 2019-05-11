package SubComponents.Popups.AttackPopup;

import GameEngine.GameEngine;
import GameObjects.Unit;
import MainComponents.AppController;
import SubComponents.Popups.ActionPopupController;
import javafx.fxml.FXML;

import java.util.ArrayList;
import java.util.List;


public class AttackPopupController implements ActionPopupController {
    private AppController mainController;
    private String typeOfAttack = null;
    private static final String WELL_TIMED = "Well Timed Attack";
    private static final String CALCULATED_RISK = "Calculated Risk Attack";
    public enum Result {WIN , LOSE , DRAW}
    private Result resultOfBattle;


    @Override
    public void startAction() {
        if(typeOfAttack != null) {
            if(typeOfAttack.equals(WELL_TIMED)) {
                startWellTimedAttack();
            }
            else if(typeOfAttack.equals(CALCULATED_RISK)) {
                startCalculatedRiskAttack();
            }
        }
        else {
            startNeutralAttack();
        }
        mainController.showResultsPopup(resultOfBattle , createText());
    }

    private void startNeutralAttack() {
        if(GameEngine.gameManager.conquerNeutralTerritory()) { // Got the territory
            mainController.getHeaderComponentController().writeIntoTextArea("Territory " + GameEngine.gameManager.getSelectedTerritoryByPlayer().getID() + " has been conquered!" + "\n");
            resultOfBattle = Result.WIN;
        }
        else { // Failed to get territory ( Army is not big enough)
            mainController.getHeaderComponentController().writeIntoTextArea("Failed to conquer territory number  " + GameEngine.gameManager.getSelectedTerritoryByPlayer().getID() +"\n");
            resultOfBattle = Result.LOSE;
        }
    }

    private void startCalculatedRiskAttack() {
        int attackerWon =  GameEngine.gameManager.attackConqueredTerritoryByCalculatedRiskBattle();
        if(attackerWon == 1) { //Win
            if(GameEngine.gameManager.getSelectedTerritoryByPlayer().getConquerArmyForce() == null) { //Check if there are enough to hold territory
                resultOfBattle = Result.WIN;
            }
        }
        else if(attackerWon == 0) { //Defeat
            resultOfBattle = Result.LOSE;
        }
        else { // Draw
            resultOfBattle = Result.DRAW;
        }

    }

    private String createText() {
        if(typeOfAttack == null) { //Neutral
            switch (resultOfBattle) {
                case WIN:
                    return "You have conquered neutral territory : " + GameEngine.gameManager.getSelectedTerritoryByPlayer().getID();
                case LOSE:
                    return "You have FAILED to conquer  neutral territory : " + GameEngine.gameManager.getSelectedTerritoryByPlayer().getID();
            }
        }
        int defendingArmySize = GameEngine.gameManager.getSelectedTerritoryByPlayer().getConquerArmyForce().getUnits().size();
        String defendingUnitsResult = "Type of attack was "
                + typeOfAttack
                + "\n"
                + "Defending army had: "
                + defendingArmySize
                + "\n"
                + "The defending territory units were: "
                + "\n";
        List<String> unitNamesOnTerritoryList = new ArrayList<>();
        for(Unit unit : GameEngine.gameManager.getSelectedTerritoryByPlayer().getConquerArmyForce().getUnits()) {
            unitNamesOnTerritoryList.add(unit.getType());
        }
        String defendingUnitsString = String.join("" , unitNamesOnTerritoryList);
        return defendingUnitsResult.concat(defendingUnitsString);
    }

    private void startWellTimedAttack() {
        int attackerWon =  GameEngine.gameManager.attackConqueredTerritoryByWellTimedBattle();
        if(attackerWon == 1) { //Win
            if(GameEngine.gameManager.getSelectedTerritoryByPlayer().getConquerArmyForce() == null) { //Check if there are enough to hold territory
                resultOfBattle = Result.WIN;
            }
        }
        else if(attackerWon == 0) { //Defeat
            resultOfBattle = Result.LOSE;
        }
        else { // Draw
            resultOfBattle = Result.DRAW;
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
