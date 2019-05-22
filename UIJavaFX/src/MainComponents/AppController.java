package MainComponents;

import DataContainersTypes.Board;
import Events.EventHandler;
import Events.EventObject;
import GameEngine.GameEngine;
import Resources.ResourceConstants;
import SubComponents.Header.HeaderController;
import SubComponents.InformationTable.InformationController;
import SubComponents.MapTable.MapController;
import SubComponents.Popups.ActionPopupController;
import SubComponents.Popups.AttackPopup.AttackPopupController;
import SubComponents.Popups.BuyUnitsPopup.BuyUnitsPopupController;
import SubComponents.Popups.OwnTerrainPopup.OwnTerrainController;
import SubComponents.Popups.ResultPopup.ResultPopupController;
import SubComponents.ReplayComponent.ReplayController;
import WelcomeScreen.WelcomeScreenController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class AppController {
    @FXML private AnchorPane HeaderComponent;
    @FXML private HeaderController HeaderComponentController;
    @FXML private AnchorPane InformationComponent;
    @FXML private InformationController InformationComponentController;
    @FXML private ScrollPane MapComponent;
    @FXML private MapController MapComponentController;
    @FXML private AnchorPane ReplayComponent;
    @FXML private ReplayController ReplayComponentController;
    private GameEngine gameEngine;
    private Stage primaryStage;

    public InformationController getInformationComponentController() {
        return InformationComponentController;
    }
    public Stage getPrimaryStage() {
        return primaryStage;
    }
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
    public ReplayController getReplayComponentController() {
        return ReplayComponentController;
    }
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
        && MapComponentController != null && ReplayComponentController!= null) {
            ReplayComponentController.setMainController(this);
            HeaderComponentController.setMainController(this);
            InformationComponentController.setMainController(this);
            MapComponentController.setMainController(this);
        }
        //initialize CSS path
        MapComponent.getStylesheets().add("/SubComponents/MapTable/Map.css");
        HeaderComponent.getStylesheets().add("/SubComponents/Header/Header.css");
        InformationComponent.getStylesheets().add("/SubComponents/InformationTable/InformationTable.css");
        ReplayComponent.getStylesheets().add("/SubComponents/ReplayComponent/Replay.css");
        HeaderComponentController.getBtnAnimationToggle().setSelected(true);

    }
    public String getColorByPlayerName(String playerName){
        return InformationComponentController.getColorByPlayerName(playerName);

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
    public void loadRoundHistory(){
        GameEngine.gameManager.peekHistory();
        getInformationComponentController().loadRoundHistory();
        getMapComponentController().clearMap();
        createMap();
    }
    public void loadCurrentInformation(){
        InformationComponentController.loadBinding();
        getInformationComponentController().loadRoundHistory();
        getMapComponentController().clearMap();
        createMap();
    }
    public void updateInformation(){
        InformationComponentController.updatePlayersData();
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
        GameEngine.gameManager.setEventListenerHandler(eventObject -> {
            MapComponentController.unColorTerritory(eventObject.getIdentity());
            HeaderComponentController.writeIntoTextArea("Territory " + eventObject.getIdentity() + " is fair play!" + "\n");
        });
    }

    public void startRound() {
        GameEngine.gameManager.startOfRoundUpdates();
        updateInformation();
        setBtnReplayAccessible();
        nextPlayer();
    }

    private void setBtnReplayAccessible() {
        if(GameEngine.gameManager.isLastRound()){
            HeaderComponentController.setBtnReplayAccessible();
        }
    }

    public void nextPlayer() {
        GameEngine.gameManager.nextPlayerInTurn();
        HeaderComponentController.setCurrentPlayerInTurnLbl(GameEngine.gameManager.getCurrentPlayerTurn().getPlayerName());
    }
    public void endOfRoundUpdates(){
        GameEngine.gameManager.endOfRoundUpdates();
        InformationComponentController.incCurrentRoundProperty();
    }
    public MapController getMapComponentController() {
        return MapComponentController;
    }

    public void showOwnTerritoryPopup() {
        try {
            //Load FXML
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/SubComponents/Popups/OwnTerrainPopup/OwnTerrainPopUpFXML.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root, 242, 223);

            //CSS
            switch (HeaderComponentController.currentTheme) {
                case "Ocean":
                    scene.getStylesheets().add("/SubComponents/Popups/OwnTerrainPopup/Own_Ocean.css");
                    break;
                case "Black Core":
                    scene.getStylesheets().add("/SubComponents/Popups/OwnTerrainPopup/Own_BlackCore.css");
                    break;
                case "Default":
                    break;
            }

            //Stage
            Stage stage = new Stage();
            stage.setTitle("Own Territory");
            stage.setScene(scene);

            //Wire up the controller and initialize game engine
            OwnTerrainController ownTerrainController = fxmlLoader.getController();
            ownTerrainController.setMainController(this);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
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

                //CSS
                switch (HeaderComponentController.currentTheme) {
                    case "Ocean":
                        scene.getStylesheets().add("/SubComponents/Popups/AttackPopup/Attack_Ocean.css");
                        break;
                    case "Black Core":
                        scene.getStylesheets().add("/SubComponents/Popups/AttackPopup/Attack_BlackCore.css");
                        break;
                    case "Default":
                        break;
                }

                //Stage
                Stage stage = new Stage();
                stage.setTitle("Attack");
                stage.setScene(scene);

                //Wire up the controller and initialize game engine
                AttackPopupController attackPopupController = fxmlLoader.getController();
                attackPopupController.setMainController(this);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setResizable(false);

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


            //Stage
            Stage stage = new Stage();
            stage.setTitle("Buy Units");
            stage.setScene(scene);

            //CSS
            switch (HeaderComponentController.currentTheme) {
                case "Ocean":
                    scene.getStylesheets().add("/SubComponents/Popups/BuyUnitsPopup/BuyUnits_Ocean.css");
                    break;
                case "Black Core":
                    scene.getStylesheets().add("/SubComponents/Popups/BuyUnitsPopup/BuyUnits_BlackCore.css");
                    break;
                case "Default":
                    break;
            }

            //Wire up the controller and initialize game engine
            BuyUnitsPopupController buyUnitsComponentController= fxmlLoader.getController();
            buyUnitsComponentController.setMainController(this);
            buyUnitsComponentController.setParentController(parent);
            buyUnitsComponentController.buildUnitDropdownList();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showResultsPopup(AttackPopupController.Result result , String info) {
        try {
            //Load FXML
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/SubComponents/Popups/ResultPopup/ResultPopupFXML.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root, 250, 586);

            //CSS
            switch (HeaderComponentController.currentTheme) {
                case "Ocean":
                    scene.getStylesheets().add("/SubComponents/Popups/ResultPopup/Result_Ocean.css");
                    break;
                case "Black Core":
                    scene.getStylesheets().add("/SubComponents/Popups/ResultPopup/Result_BlackCore.css");
                    break;
                case "Default":
                    break;
            }

            //Stage
            Stage stage = new Stage();
            stage.setTitle(result.toString());
            stage.setScene(scene);

            //Wire up the controller and initialize game engine
            ResultPopupController resultPopupController = fxmlLoader.getController();
            resultPopupController.setMainController(this);
            resultPopupController.populateInfoBasedOnResult(info , result);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.show();
        }  catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void loadInformation() {
        InformationComponentController.loadInformation();
        HeaderComponentController.loadBinding();
    }

    public boolean isGameOver() {
        return GameEngine.gameManager.isGameOver();
    }

    public void launchWelcomeScreen() {
        FXMLLoader fxmlLoader = new FXMLLoader();

        //load Welcome Screen FXML
        URL welcomeScreen = getClass().getResource(ResourceConstants.WELCOMESCREEN_FXML_INCLUDE_RESOURCE);
        fxmlLoader.setLocation(welcomeScreen);
        Parent root = null;
        try {
            root = fxmlLoader.load(welcomeScreen.openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        // wire up primary stage
        primaryStage.setHeight(500);
        primaryStage.setWidth(450);
        WelcomeScreenController welcomeScreenController = fxmlLoader.getController();
        welcomeScreenController.setPrimaryStage(primaryStage);

        //set stage
        primaryStage.setTitle("Conquerors");
        assert root != null;
        Scene scene = new Scene(root, 450, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
