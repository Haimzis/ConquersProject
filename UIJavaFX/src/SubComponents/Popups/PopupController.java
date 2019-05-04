package SubComponents.Popups;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class PopupController {
    @FXML
    private Button wellTimedAttackSelected;
    @FXML
    private Button calculatedRiskAttack;
    @FXML
    private Button rehabilitateArmyBtn;
    @FXML
    private Button addUnitsToArmyBtn;

    @FXML
    private void calculatedRiskAttackListener(ActionEvent event) {
        calculatedRiskAttack.setText("I've been pressed!");
    }

    @FXML
    private void wellTimedAttackListener(ActionEvent event) {
        wellTimedAttackSelected.setText("I've been pressed!");
    }

    @FXML
    private void rehabilitateArmyBtnListener(ActionEvent event) {

    }
}
