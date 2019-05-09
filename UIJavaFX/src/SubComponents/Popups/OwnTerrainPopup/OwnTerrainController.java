package SubComponents.Popups.OwnTerrainPopup;

import GameEngine.GameEngine;
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
    @FXML private Label infoLabel;
    @FXML private Button enforceArmyBtn;


    public void setMainController(AppController mainController) { this.mainController = mainController; }

    @FXML
    public void rehabilitateArmyOnTerrain() {
        Supplier<Integer> enoughMoney = () -> GameEngine.gameManager.getRehabilitationArmyPriceInTerritory(GameEngine.gameManager.getSelectedTerritoryByPlayer());
        if(GameEngine.gameManager.isSelectedPlayerHasEnoughMoney(enoughMoney)) {
            GameEngine.gameManager.rehabilitateSelectedTerritoryArmy();
            showLabelWhenDone("Done!" , Color.BLACK);
        }
        else { // Not enough funds
            showLabelWhenDone("Not enough funds." , Color.RED);
        }
    }

    @FXML
    public void enforceArmyOnTerritory() {
        mainController.showBuyUnitsPopup();
        closePopup();

    }

    private void closePopup() {
        Stage stage = (Stage) enforceArmyBtn.getScene().getWindow();
        stage.close();
    }

    private void showLabelWhenDone(String s, Paint color) {
        infoLabel.setText(s);
        infoLabel.setVisible(true);
        infoLabel.setTextFill(color);
    }

}
