package SubComponents.Popups.BuyUnitsPopup;

import MainComponents.AppController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;

import java.util.List;

public class BuyUnitsPopupController  {
    @FXML private MenuButton unitChoices;
    @FXML private TextField amountToBuy;
    @FXML private Button purchaseBtn;
    private AppController mainController;

    public void setMainController(AppController mainController) { this.mainController = mainController; }

    @FXML
    public void initialize(){
        buildUnitDropdownList();
    }
    private void setListenerForPressOnMenuItem() {
        List<MenuItem> unitsAvailableToBuy = unitChoices.getItems();
        for(MenuItem unit : unitsAvailableToBuy) {

        }
    }


    private void  buildUnitDropdownList() {
        for (String key : mainController.getGameEngine().getDescriptor().getUnitMap().keySet()) {
            MenuItem unitToShowItem = new MenuItem(key);
            unitChoices.getItems().add(unitToShowItem);
        }
    }
}
