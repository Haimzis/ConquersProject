package MainComponents;

import DataContainersTypes.Board;
import GameEngine.GameEngine;
import SubComponents.Header.HeaderController;
import SubComponents.InformationTable.InformationController;
import SubComponents.MapTable.MapController;
import SubComponents.MenuTable.MenuController;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;

public class AppController {
    @FXML private AnchorPane HeaderComponent;
    @FXML private HeaderController HeaderComponentController;
    @FXML private AnchorPane InformationComponent;
    @FXML private InformationController InformationComponentController;
    @FXML private ScrollPane MapComponent;
    @FXML private MapController MapComponentController;
    @FXML private AnchorPane MenuComponent;
    @FXML private MenuController MenuComponentController;
    private GameEngine gameEngine;

    public GameEngine getGameEngine() {
        return gameEngine;
    }

    public AppController() {
        this.gameEngine = new GameEngine();
    }

    @FXML
    public void initialize() {
        if (HeaderComponentController != null && InformationComponentController != null
        && MapComponentController != null && MenuComponentController != null) {
            HeaderComponentController.setMainController(this);
            InformationComponentController.setMainController(this);
            MapComponentController.setMainController(this);
            MenuComponentController.setMainController(this);
        }
        gameEngine.loadXML("C:/Users/Haim Zisman/Desktop/Check files/ex1-small.xml");
        //initialize CSS path
        MapComponent.getStylesheets().add(getClass().getResource("/SubComponents/MapTable/Map.css").toExternalForm());
        createMap();
    }

    public void setHeaderComponentController(HeaderController headerComponentController) {
        this.HeaderComponentController = headerComponentController;
    }

    public void setInformationComponentController(InformationController informationComponentController) {
        this.InformationComponentController = informationComponentController;
    }

    public void setMapComponentController(MapController mapComponentController) {
        this.MapComponentController = mapComponentController;
    }

    public void setMenuComponentController(MenuController menuComponentController) {
        this.MenuComponentController = menuComponentController;
    }
    public void createMap(){
        MapComponentController.setMap(
                new Board(
                        gameEngine.getDescriptor().getColumns(),
                        gameEngine.getDescriptor().getRows(),
                        gameEngine.getDescriptor().getTerritoryMap()
                ));
        MapComponentController.createMap();
    }
}
