package MainComponents;

import DataContainersTypes.Board;
import GameEngine.GameEngine;
import GameObjects.Territory;
import SubComponents.Header.HeaderController;
import SubComponents.InformationTable.InformationController;
import SubComponents.MapTable.MapController;
import SubComponents.MenuTable.MenuController;
import SubComponents.Popups.BuyUnitsPopup.BuyUnitsPopupController;
import SubComponents.Popups.OwnTerrainPopup.OwnTerrainController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

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

    public Stage getPrimaryStage() {
        return primaryStage;
    }
    private Stage primaryStage;

    public MenuController getMenuComponentController() {
        return MenuComponentController;
    }

    public GameEngine getGameEngine() {
        return gameEngine;
    }
    public void setGameEngine(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
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
        //initialize CSS path
        MapComponent.getStylesheets().add(getClass().getResource("/SubComponents/MapTable/Map.css").toExternalForm());
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

    public void startGame() {
        gameEngine.newGame();
    }

    public void startRound() {
        GameEngine.gameManager.startOfRoundUpdates();
    }

    public void nextPlayer() {
        GameEngine.gameManager.nextPlayerInTurn();
    }

    public void showOwnTerritoryPopup() {
        try {
            //Load FXML
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/SubComponents/Popups/OwnTerrainPopup/OwnTerrainPopUpFXML.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root, 242, 223);
            Stage stage = new Stage();
            stage.setTitle("Own Territory");
            stage.setScene(scene);

            //Wire up the controller and initialize game engine
            OwnTerrainController ownTerrainController = fxmlLoader.getController();
            ownTerrainController.setMainController(this);
            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showAttackPopup() {

    }

    public void showBuyUnitsPopup() {
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
            buyUnitsComponentController.setMainController(this);
            buyUnitsComponentController.buildUnitDropdownList();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void actOnTerritory(Territory territory) {
        GameEngine.gameManager.setSelectedTerritoryForTurn(territory);
        if(!GameEngine.gameManager.getCurrentPlayerTerritories().isEmpty()) {
            if(GameEngine.gameManager.isTerritoryBelongsCurrentPlayer()) {
                actOnOwnTerritory();
            }
            else if(GameEngine.gameManager.isConquered()) {
                if(GameEngine.gameManager.isTargetTerritoryValid()) {
                    actOnEnemyTerritory();
                }
                else { //Not valid conquered

                }
            }
            else { //neutral territory
                if(GameEngine.gameManager.isTargetTerritoryValid()) {
                    if(GameEngine.gameManager.conquerNeutralTerritory()) { //Success
                        actOnNeutralTerritory();
                    }
                    else { //Failed to get neutral territory

                    }
                }
                else { // Not valid neutral

                }
            }
        }
        else { //Player has  no territories
            actOnAnyTerritory();
        }
    }

    private void actOnAnyTerritory() {
        if(GameEngine.gameManager.isConquered()) {
            actOnEnemyTerritory();
        }
         else {
             actOnNeutralTerritory();
        }
    }

    private void actOnNeutralTerritory() {
        showBuyUnitsPopup();

    }

    private void actOnEnemyTerritory() {
        showAttackPopup();
    }

    private void actOnOwnTerritory() {
        showOwnTerritoryPopup();

    }
}
