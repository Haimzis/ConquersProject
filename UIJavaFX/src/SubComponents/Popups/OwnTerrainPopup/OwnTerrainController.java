package SubComponents.Popups.OwnTerrainPopup;

import GameEngine.GameEngine;
import GameObjects.Territory;
import MainComponents.AppController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.util.function.Supplier;

public class OwnTerrainController {
    private AppController mainController;
    @FXML Label infoLabel;
    @FXML Button enforceArmyBtn;


    public void setMainController(AppController mainController) { this.mainController = mainController; }

    @FXML
    public void rehabilitateArmyOnTerrain(int territoryId) {
        Territory targetTerritory = mainController.getGameEngine().getDescriptor().getTerritoryMap().get(territoryId);
        GameEngine.gameManager.setSelectedTerritoryForTurn(targetTerritory);
        Supplier<Integer> enoughMoney = () -> GameEngine.gameManager.getRehabilitationArmyPriceInTerritory(targetTerritory);
        if(GameEngine.gameManager.isSelectedPlayerHasEnoughMoney(enoughMoney)) {
            GameEngine.gameManager.rehabilitateSelectedTerritoryArmy();
            showLabelWhenDone("Done!" , Color.BLACK);
        }
        else { // Not enough funds
            showLabelWhenDone("Not enough funds." , Color.RED);
        }
    }

    private void showLabelWhenDone(String s, Paint color) {
        infoLabel.setText(s);
        infoLabel.setVisible(true);
        infoLabel.setTextFill(color);
    }

    @FXML
    public void enforceArmyOnTerritory(int territoryId) {
        Territory targetTerritory = mainController.getGameEngine().getDescriptor().getTerritoryMap().get(territoryId);
        GameEngine.gameManager.setSelectedTerritoryForTurn(targetTerritory);
        mainController.getMenuComponentController().showBuyUnitsPopup();
        GameEngine.gameManager.transformSelectedArmyForceToSelectedTerritory();
        closePopup();

    }

    private void closePopup() {
        Stage stage = (Stage) enforceArmyBtn.getScene().getWindow();
        stage.close();
    }
}
