package MainComponents;

import SubComponents.Header.HeaderController;
import SubComponents.InformationTable.InformationController;
import SubComponents.MapTable.MapController;
import SubComponents.MenuTable.MenuController;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;

public class AppController {
    @FXML private AnchorPane headerComponent;
    @FXML private HeaderController headerComponentController;
    @FXML private AnchorPane informationComponent;
    @FXML private InformationController informationComponentController;
    @FXML private ScrollPane mapComponent;
    @FXML private MapController mapComponentController;
    @FXML private AnchorPane menuComponent;
    @FXML private MenuController menuComponentController;

    @FXML
    public void initialize( ) {
        if (headerComponentController != null && informationComponentController != null
        && mapComponentController != null && menuComponentController != null) {
            headerComponentController.setMainController(this);
            informationComponentController.setMainController(this);
            mapComponentController.setMainController(this);
            menuComponentController.setMainController(this);
        }
    }

    public void setHeaderComponentController(HeaderController headerComponentController) {
        this.headerComponentController = headerComponentController;
    }

    public void setInformationComponentController(InformationController informationComponentController) {
        this.informationComponentController = informationComponentController;
    }

    public void setMapComponentController(MapController mapComponentController) {
        this.mapComponentController = mapComponentController;
    }

    public void setMenuComponentController(MenuController menuComponentController) {
        this.menuComponentController = menuComponentController;
    }
}
