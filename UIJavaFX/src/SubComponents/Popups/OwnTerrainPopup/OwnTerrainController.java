package SubComponents.Popups.OwnTerrainPopup;

import MainComponents.AppController;
import SubComponents.MapTable.MapController;
import SubComponents.Popups.ActionPopupController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.util.function.Supplier;

public class OwnTerrainController implements ActionPopupController {
    private AppController mainController;
    @FXML private Label lblInfo;
    @FXML private Button btnEnforceArmy;
    @FXML private Button btnRehabilitate;


    public void setMainController(AppController mainController) { this.mainController = mainController; }

    @FXML
    public void rehabilitateArmyOnTerrain() {
        Supplier<Integer> enoughMoney = () -> mainController.getCurrentGameManager().getRehabilitationArmyPriceInTerritory(mainController.getCurrentGameManager().getSelectedTerritoryByPlayer());
        if(mainController.getCurrentGameManager().isSelectedPlayerHasEnoughMoney(enoughMoney)) {
            mainController.getCurrentGameManager().rehabilitateSelectedTerritoryArmy();
            showLabelWhenDone("Done!" , Color.BLACK);
            btnRehabilitate.setDisable(true);
            btnEnforceArmy.setDisable(true);
        }
        else { // Not enough funds
            showLabelWhenDone("Not enough funds." , Color.RED);
        }
    }

    @FXML
    public void enforceArmyOnTerritory() {
        mainController.showBuyUnitsPopup(this);
        closePopup();

    }

    private void closePopup() {
        Stage stage = (Stage) btnEnforceArmy.getScene().getWindow();
        stage.close();
    }

    private void showLabelWhenDone(String s, Paint color) {
        lblInfo.setText(s);
        lblInfo.setVisible(true);
        lblInfo.setTextFill(color);
    }

    @Override
    public void startAction() { //Enforce and print it's been enforced.
        mainController.getCurrentGameManager().transformSelectedArmyForceToSelectedTerritory();
        MapController.actionBeenTaken = true;
    }
}
