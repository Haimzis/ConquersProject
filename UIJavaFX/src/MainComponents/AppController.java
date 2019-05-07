package MainComponents;

import DataContainersTypes.Board;
import GameEngine.GameEngine;
import SubComponents.Header.HeaderController;
import SubComponents.InformationTable.InformationController;
import SubComponents.MapTable.MapController;
import SubComponents.MenuTable.MenuController;
import SubComponents.Popups.BuyUnitsPopup.BuyUnitsPopupController;
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
    private BuyUnitsPopupController BuyUnitsComponentController;
    private GameEngine gameEngine;

    public GameEngine getGameEngine() {
        return gameEngine;
    }

    public AppController() {
        this.gameEngine = new GameEngine();
    }

    @FXML
    public void initialize() {
        BuyUnitsComponentController = new BuyUnitsPopupController();
        BuyUnitsComponentController.setMainController(this);
        if (HeaderComponentController != null && InformationComponentController != null
        && MapComponentController != null && MenuComponentController != null) {
            HeaderComponentController.setMainController(this);
            InformationComponentController.setMainController(this);
            MapComponentController.setMainController(this);
            MenuComponentController.setMainController(this);
        }
        gameEngine.loadXML("C:\\Users\\Ran Tzur\\Desktop\\Projects\\קבצי בדיקה\\EX 2\\ex2-small.xml");
        createMap();
        //BuyUnitsComponentController.show();
    }


    public void setBuyUnitsComponentController(BuyUnitsPopupController buyUnitsComponentController) {
        this.BuyUnitsComponentController = buyUnitsComponentController;
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
