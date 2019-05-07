package SubComponents.Popups.BuyUnitsPopup;

import MainComponents.AppController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

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

    public void show() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            URL url = getClass().getResource("/SubComponents/Popups/BuyUnitsPopup/buyUnitsPopupFXML.fxml");
            fxmlLoader.setLocation(url);
            Parent root = fxmlLoader.load(url.openStream());
            Stage stage = new Stage();
            stage.setTitle("Buy units window");
            stage.setScene(new Scene(root, 242, 223));
            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
