package MainComponents;

import DataContainersTypes.Board;
import GameEngine.GameEngine;
import SubComponents.Header.HeaderController;
import SubComponents.InformationTable.InformationController;
import SubComponents.MapTable.MapController;
import SubComponents.Popups.ActionPopupController;
import SubComponents.Popups.AttackPopup.AttackPopupController;
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
    private GameEngine gameEngine;

    public Stage getPrimaryStage() {
        return primaryStage;
    }
    private Stage primaryStage;


    public HeaderController getHeaderComponentController() {
        return HeaderComponentController;
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
        && MapComponentController != null) {
            HeaderComponentController.setMainController(this);
            InformationComponentController.setMainController(this);
            MapComponentController.setMainController(this);
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
        HeaderComponentController.setCurrentPlayerInTurnLbl(GameEngine.gameManager.getCurrentPlayerTurn().getPlayer_name());
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
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showAttackPopup() {
        try {
            if (GameEngine.gameManager.isConquered()) {
                //Load FXML
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/SubComponents/Popups/AttackPopup/AttackPopupFXML.fxml"));
                Parent root = fxmlLoader.load();
                Scene scene = new Scene(root, 242, 223);
                Stage stage = new Stage();
                stage.setTitle("Attack");
                stage.setScene(scene);

                //Wire up the controller and initialize game engine
                AttackPopupController attackPopupController = fxmlLoader.getController();
                attackPopupController.setMainController(this);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.show();
            } else { // Neutral territory
                AttackPopupController attackPopupController = new AttackPopupController();
                attackPopupController.setMainController(this);
                showBuyUnitsPopup(attackPopupController);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void showBuyUnitsPopup(ActionPopupController parent) {
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
            buyUnitsComponentController.setParentController(parent);
            buyUnitsComponentController.buildUnitDropdownList();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadInformation() {
        InformationComponentController.loadInformation();
    }
}
