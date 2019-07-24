package WelcomeScreen;

import GameEngine.GameEngine;
import MainComponents.AppController;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static Resources.ResourceConstants.APP_FXML_INCLUDE_RESOURCE;

public class WelcomeScreenController {
@FXML private FlowPane WelcomeScreenComponent;
@FXML private TextField tbx_path;
@FXML private Label lbl_message;
@FXML private Button btn_choosePath;
@FXML private Button buttonStartGame;
@FXML private Button btn_loadXML;
@FXML private Button btn_loadGame;
    private Stage primaryStage;
    private SimpleStringProperty messageProperty;
    private SimpleStringProperty selectedFilePathProperty;
    private SimpleBooleanProperty isFileSelectedProperty;
    private SimpleBooleanProperty isLoadSucceedProperty;
    private GameEngine gameEngine;
    private GameLoader gameLoader;
    private Boolean gameAlreadyLoaded=false;
    private static String lastKnownGoodString;

    public WelcomeScreenController(){
        gameEngine = new GameEngine();
        //binds
        isLoadSucceedProperty = new SimpleBooleanProperty(false);
        isFileSelectedProperty = new SimpleBooleanProperty(false);
        selectedFilePathProperty = new SimpleStringProperty("");
        messageProperty = new SimpleStringProperty("");
    }
    @FXML
    public void initialize(){
        buttonStartGame.disableProperty().bind(isLoadSucceedProperty.not());
        btn_loadXML.disableProperty().bind(isFileSelectedProperty.not());
        btn_loadGame.disableProperty().bind(isFileSelectedProperty.not());
        tbx_path.textProperty().bind(selectedFilePathProperty);
        lbl_message.textProperty().bind(messageProperty);
    }
    public void setGameLoader(GameLoader gameLoader) {this.gameLoader = gameLoader;}
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
    @FXML
    public void btn_choosePathAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select XML/Game Saves ");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*"));
        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile == null) {
            return;
        }
        selectedFilePathProperty.set(selectedFile.getPath());
        isFileSelectedProperty.set(true);
    }

    @FXML
    public void btn_loadXMLAction(){
        gameAlreadyLoaded = false;
        gameLoader.loadXML(gameEngine,
                        messageProperty,
                        selectedFilePathProperty,
                        isLoadSucceedProperty);
    }

    public void setForNewGame(){
        selectedFilePathProperty.set(lastKnownGoodString);
        isFileSelectedProperty.set(true);
    }

    @FXML
    public void setBtn_loadGameAction(){
        gameAlreadyLoaded = true;
        gameLoader.loadSavedGame(gameEngine,
                        messageProperty,
                        selectedFilePathProperty,
                        isLoadSucceedProperty);
    }

    @FXML
    private void startGame(){
            try {
                //Load FXML of Root(on stage)
                FXMLLoader fxmlLoader = new FXMLLoader();
                URL url = getClass().getResource(APP_FXML_INCLUDE_RESOURCE);
                fxmlLoader.setLocation(url);
                Parent root1 = fxmlLoader.load(url.openStream());
                //create Scene
                Scene scene = new Scene(root1, 500, 550);
                //set new size of this stage
                primaryStage.setHeight(650);
                primaryStage.setWidth(950);
                primaryStage.setScene(scene);

                //CSS
                scene.getStylesheets().add("/Resources/Default.css");

                //wire up game engine to appController
                AppController appController = fxmlLoader.getController();
                appController.setPrimaryStage(primaryStage);
                appController.setGameEngine(gameEngine);
                lastKnownGoodString = gameEngine.getLastGameDescriptor().getLastKnownGoodString();
                //start game
                if(!gameAlreadyLoaded) { //Check if its not a loaded game
                    appController.startGame(gameEngine.getLastGameDescriptor());
                    //first load of xml into UI
                    appController.loadInformation();
                    appController.createMap();
                }
                else { //It's a loaded game
                    appController.loadInformation();
                    appController.createMap();
                    //Wire the listener again since it's not saved
                    appController.getCurrentGameManager().setEventListenerHandler(eventObject -> {
                        appController.getMapComponentController().unColorTerritory((Integer)eventObject.getIdentity());
                        appController.getHeaderComponentController().writeIntoTextArea("Territory " + eventObject.getIdentity() + " is fair play!" + "\n");
                    });
                    appController.getHeaderComponentController().getBtnSave().setDisable(false);
                    appController.getHeaderComponentController().getBtnUndo().setDisable(false);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            primaryStage.show(); // show game

    }

}
