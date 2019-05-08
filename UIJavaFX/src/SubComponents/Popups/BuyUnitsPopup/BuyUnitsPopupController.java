package SubComponents.Popups.BuyUnitsPopup;

import MainComponents.AppController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
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
    private void buildUnitDropdownList() {
        for(String key : mainController.getGameEngine().getDescriptor().getUnitMap().keySet()) {
            MenuItem unitToShowItem = new MenuItem(key);
            unitChoices.getItems().add(unitToShowItem);
        }
    }
}
