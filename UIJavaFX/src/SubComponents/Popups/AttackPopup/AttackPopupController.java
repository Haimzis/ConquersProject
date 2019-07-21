package SubComponents.Popups.AttackPopup;

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
    public enum Result {WIN , LOSE , DRAW, WIN_COULD_NOT_HOLD}
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
        mainController.getCurrentGameManager().activateEventsHandler();
        MapController.actionBeenTaken = true;
    }

    private void startNeutralAttack() {
        if(mainController.getCurrentGameManager().conquerNeutralTerritory()) { // Got the territory
            mainController.getHeaderComponentController().writeIntoTextArea("Territory " + mainController.getCurrentGameManager().getSelectedTerritoryByPlayer().getID() + " has been conquered!" + "\n");
            resultOfBattle = Result.WIN;

        }
        else { // Failed to get territory ( Army is not big enough)
            mainController.getHeaderComponentController().writeIntoTextArea("Failed to conquer territory number " + mainController.getCurrentGameManager().getSelectedTerritoryByPlayer().getID() +"\n");
            resultOfBattle = Result.LOSE;
        }
        infoOfBattle = createText();
    }

    private void startCalculatedRiskAttack() {
        mainController.getHeaderComponentController().writeIntoTextArea("A battle has started upon territory number: " + mainController.getCurrentGameManager().getSelectedTerritoryByPlayer().getID()+ "\n");
        infoOfBattle = createText();
        int attackerWon =  mainController.getCurrentGameManager().attackConqueredTerritoryByCalculatedRiskBattle();
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
        if (mainController.getCurrentGameManager().getSelectedTerritoryByPlayer().getConquerArmyForce() == null) { //Check if there are enough to hold territory
            resultOfBattle = Result.WIN_COULD_NOT_HOLD;
        }
        else {
            resultOfBattle = Result.WIN;
            mainController.getHeaderComponentController().writeIntoTextArea("Territory " + mainController.getCurrentGameManager().getSelectedTerritoryByPlayer().getID() + " has been conquered!" + "\n");
        }
    }

    private String createText() {
        if(typeOfAttack == null) { //Enemy
            switch (resultOfBattle) {
                case WIN:
                    return "You have conquered neutral territory : " + mainController.getCurrentGameManager().getSelectedTerritoryByPlayer().getID();
                case LOSE:
                    return "You have FAILED to conquer  neutral territory : " + mainController.getCurrentGameManager().getSelectedTerritoryByPlayer().getID();
            }
        }
        int defendingArmySize = mainController.getCurrentGameManager().getSelectedTerritoryByPlayer().getConquerArmyForce().getUnits().size();
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
        for (Unit unit : mainController.getCurrentGameManager().getSelectedTerritoryByPlayer().getConquerArmyForce().getUnits()) {
            unitNamesOnTerritoryList.add(unit.getType());
        }
        String defendingUnitsString = String.join(" ", unitNamesOnTerritoryList);
        return defendingUnitsResult.concat(defendingUnitsString);

    }

    private void startWellTimedAttack() {
        infoOfBattle = createText();
        mainController.getHeaderComponentController().writeIntoTextArea("A battle has started upon territory number: " + mainController.getCurrentGameManager().getSelectedTerritoryByPlayer().getID() + "\n");
        int attackerWon =  mainController.getCurrentGameManager().attackConqueredTerritoryByWellTimedBattle();
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
