package SubComponents.Popups.BuyUnitsPopup;

import MainComponents.AppController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;

public class BuyUnitsPopupController {
    @FXML private SplitMenuButton unitChoices;
    private AppController mainController;

    public void setMainController(AppController mainController) { this.mainController = mainController; }

    @FXML
    private void OnUnitsDropdownPressEvent(ActionEvent event) {
        unitChoices = new SplitMenuButton();
        for(int i = 0 ; i < mainController.getGameEngine().getDescriptor().getUnitMap().size() ; i++) {
            MenuItem unitToShowItem = new MenuItem(mainController.getGameEngine().getDescriptor().getUnitMap().get(i).getType());
            unitChoices.getItems().add(unitToShowItem);
        }
    }

}
