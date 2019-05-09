package SubComponents.MenuTable;

import MainComponents.AppController;
import SubComponents.Popups.BuyUnitsPopup.BuyUnitsPopupController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuController {

    @FXML private AnchorPane MenuComponent;
    @FXML private Button btn_BuyUnits;
    private AppController mainController;

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }
    public void showBuyUnitsPopup(int whoCalled) {
        try {
            //Load FXML
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/SubComponents/Popups/BuyUnitsPopup/BuyUnitsPopupFXML.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root, 242, 223);
            Stage stage = new Stage();
            stage.setTitle("New Window");
            stage.setScene(scene);

            //Wire up the controller and initialize game engine
            BuyUnitsPopupController buyUnitsComponentController= fxmlLoader.getController();
            buyUnitsComponentController.setMainController(mainController);
            buyUnitsComponentController.buildUnitDropdownList(whoCalled);
            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
