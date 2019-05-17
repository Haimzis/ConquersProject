package SubComponents.Popups.AttackPopup;

import GameEngine.GameEngine;
import GameObjects.Unit;
import MainComponents.AppController;
import SubComponents.MapTable.MapController;
import SubComponents.Popups.ActionPopupController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;


public class AttackPopupController implements ActionPopupController {
    private AppController mainController;
    private String typeOfAttack = null;
    private static final String WELL_TIMED = "Well Timed Attack";
    private static final String CALCULATED_RISK = "Calculated Risk Attack";
    public enum Result {WIN , LOSE , DRAW, WINCOULDNOTHOLD}
    private Result resultOfBattle;
    private String infoOfBattle;
    @FXML private Button btnWellTimed;


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
        mainController.getMapComponentController().colorTerritory();
        mainController.showResultsPopup(resultOfBattle , infoOfBattle);
        mainController.updateInformation();
        //GameEngine.gameManager.activateEventsHandler();
        MapController.actionBeenTaken = true;
    }

    private void startNeutralAttack() {
        if(GameEngine.gameManager.conquerNeutralTerritory()) { // Got the territory
            mainController.getHeaderComponentController().writeIntoTextArea("Territory " + GameEngine.gameManager.getSelectedTerritoryByPlayer().getID() + " has been conquered!" + "\n");
            resultOfBattle = Result.WIN;

        }
        else { // Failed to get territory ( Army is not big enough)
            mainController.getHeaderComponentController().writeIntoTextArea("Failed to conquer territory number " + GameEngine.gameManager.getSelectedTerritoryByPlayer().getID() +"\n");
            resultOfBattle = Result.LOSE;
        }
        infoOfBattle = createText();
    }

    private void startCalculatedRiskAttack() {
        mainController.getHeaderComponentController().writeIntoTextArea("A battle has started upon territory number: " + GameEngine.gameManager.getSelectedTerritoryByPlayer().getID()+ "\n");
        infoOfBattle = createText();
        int attackerWon =  GameEngine.gameManager.attackConqueredTerritoryByCalculatedRiskBattle();
        if(attackerWon == 1) { //Win
            checkIfWinnerCanHold();
        }
        else if(attackerWon == 0) { //Defeat
            resultOfBattle = Result.LOSE;
        }
        else { // Draw
            resultOfBattle = Result.DRAW;
        }

    }

    private void checkIfWinnerCanHold() {
        if (GameEngine.gameManager.getSelectedTerritoryByPlayer().getConquerArmyForce() == null) { //Check if there are enough to hold territory
            resultOfBattle = Result.WINCOULDNOTHOLD;
        }
        else {
            resultOfBattle = Result.WIN;
            mainController.getHeaderComponentController().writeIntoTextArea("Territory " + GameEngine.gameManager.getSelectedTerritoryByPlayer().getID() + " has been conquered!" + "\n");
        }
    }

    private String createText() {
        if(typeOfAttack == null) { //Enemy
            switch (resultOfBattle) {
                case WIN:
                    return "You have conquered neutral territory : " + GameEngine.gameManager.getSelectedTerritoryByPlayer().getID();
                case LOSE:
                    return "You have FAILED to conquer  neutral territory : " + GameEngine.gameManager.getSelectedTerritoryByPlayer().getID();
            }
        }
        int defendingArmySize = GameEngine.gameManager.getSelectedTerritoryByPlayer().getConquerArmyForce().getUnits().size();
        String defendingUnitsResult = "Type of attack was: "
                + typeOfAttack
                + "\n"
                + "Defending army had: "
                + defendingArmySize
                + " units"
                + "\n"
                + "The defending territory units were: "
                + "\n";
        List<String> unitNamesOnTerritoryList = new ArrayList<>();
        for (Unit unit : GameEngine.gameManager.getSelectedTerritoryByPlayer().getConquerArmyForce().getUnits()) {
            unitNamesOnTerritoryList.add(unit.getType());
        }
        String defendingUnitsString = String.join(" ", unitNamesOnTerritoryList);
        return defendingUnitsResult.concat(defendingUnitsString);

    }

    private void startWellTimedAttack() {
        infoOfBattle = createText();
        mainController.getHeaderComponentController().writeIntoTextArea("A battle has started upon territory number: " + GameEngine.gameManager.getSelectedTerritoryByPlayer().getID() + "\n");
        int attackerWon =  GameEngine.gameManager.attackConqueredTerritoryByWellTimedBattle();
        if(attackerWon == 1) { //Win
            checkIfWinnerCanHold();
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
        closePopup();
    }

    @FXML
    public void calculatedRiskAttackListener() {
        mainController.showBuyUnitsPopup(this);
        typeOfAttack = CALCULATED_RISK;
        closePopup();
    }

    private void closePopup() {
        Stage stage = (Stage) btnWellTimed.getScene().getWindow();
        stage.close();
    }
}
