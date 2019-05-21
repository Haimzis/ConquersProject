package WelcomeScreen;

import Events.EventHandler;
import Events.EventObject;
import Exceptions.invalidInputException;
import GameEngine.GameEngine;
import MainComponents.AppController;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;

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
    //private Boolean loadSucceed;
    //private Boolean gameLoaded;

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
    public void hideMessage(){
        lbl_message.setStyle("-fx-opacity: 0;");
    }
    public void showMessage(){
        lbl_message.setStyle("-fx-opacity: 1;");
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

        String absolutePath = selectedFile.getAbsolutePath();
        selectedFilePathProperty.set(selectedFile.getPath());
        isFileSelectedProperty.set(true);
    }

    @FXML
    public void btn_loadXMLAction(){
        gameLoader.loadXML(gameEngine,
                        messageProperty,
                        selectedFilePathProperty,
                        isLoadSucceedProperty,
                        () -> {
                            toggleTaskButtons(false);
                            showMessage();
                        }
        );
    }

    @FXML
    public void setBtn_loadGameAction(){
        toggleTaskButtons(true);
        gameLoader.loadSavedGame(gameEngine,
                        messageProperty,
                        selectedFilePathProperty,
                        isLoadSucceedProperty,
                () -> {
                    toggleTaskButtons(false);
                    showMessage();
                }
        );
    }
    private void toggleTaskButtons(boolean disableState) {
        btn_loadXML.setDisable(disableState);
        btn_loadGame.setDisable(disableState);
    }
    @FXML
    private void startGame() {

    }
    /*public void bindTaskToUIComponents(Task<Boolean> aTask, Runnable onFinish) {
        // task message
        taskMessageLabel.textProperty().bind(aTask.messageProperty());

        // task progress bar
        taskProgressBar.progressProperty().bind(aTask.progressProperty());

        // task percent label
        progressPercentLabel.textProperty().bind(
                Bindings.concat(
                        Bindings.format(
                                "%.0f",
                                Bindings.multiply(
                                        aTask.progressProperty(),
                                        100)),
                        " %"));

        // task cleanup upon finish
        aTask.valueProperty().addListener((observable, oldValue, newValue) -> {
            onTaskFinished(Optional.ofNullable(onFinish));
        });
    }
    *///V
/*    @FXML
    public void btn_loadXMLAction(){
        if(!isFileSelected.getValue()) {
            lbl_message.setText("Yoo Matha fucka, choose file!");
            lbl_message.setStyle("-fx-opacity: 1;");
        }
        else {
            lbl_message.setStyle("-fx-opacity: 0;");
            try {
                gameEngine.loadXML(tbx_path.getText());
                loadSucceed = true;
                lbl_message.setText("XML Loaded");
                lbl_message.setStyle("-fx-opacity: 1;");
                buttonStartGame.setDisable(false);

            } catch (invalidInputException e) {
                loadSucceed = false;
                lbl_message.setText(e.getMessage());
                lbl_message.setStyle("-fx-opacity: 1;");
            }
        }
    }*/
/*    @FXML
    public void setBtn_loadGameAction(){
        if(!isFileSelected.getValue()) {
            lbl_message.setText("Choose saved game");
            lbl_message.setStyle("-fx-opacity: 1;");
        }
        else {
            lbl_message.setStyle("-fx-opacity: 0;");
            gameLoaded = gameEngine.loadGame(gameEngine.getLoadFilePath(tbx_path.getText()));
            if (!gameLoaded) {
                lbl_message.setText("Could not load saved game file!");
                lbl_message.setStyle("-fx-opacity: 1;");
            }
            else {
                gameEngine.setDescriptor(GameEngine.gameManager.getGameDescriptor());
                lbl_message.setText("Game loaded");
                lbl_message.setStyle("-fx-opacity: 1;");
                buttonStartGame.setDisable(false);
            }
        }
    }*/
/*    @FXML
    private void startGame(){
        if(gameLoaded || loadSucceed) {
            try {
                //Load FXML of Root(on stage)
                FXMLLoader fxmlLoader = new FXMLLoader();
                URL url = getClass().getResource(APP_FXML_INCLUDE_RESOURCE);
                fxmlLoader.setLocation(url);
                Parent root1 = fxmlLoader.load(url.openStream());
                //create Scene
                Scene scene = new Scene(root1, 500, 550);
                //set new size of this stage
                primaryStage.setHeight(600);
                primaryStage.setWidth(900);
                primaryStage.setScene(scene);

                //CSS
                scene.getStylesheets().add("/MainComponents/Default.css");

                //wire up game engine to appController
                AppController appController = fxmlLoader.getController();
                appController.setPrimaryStage(primaryStage);
                appController.setGameEngine(gameEngine);
                //start game
                if(!gameLoaded) { //Check if its not a loaded game
                    appController.startGame();
                    //first load of xml into UI
                    appController.loadInformation();
                    appController.createMap();
                }
                else { //It's a loaded game
                    appController.loadInformation();
                    appController.createMap();
                    GameEngine.gameManager.setEventListenerHandler(new EventHandler(){
                    //Wire the listener again since it's not saved
                        @Override
                        public void handle(EventObject eventObject) {
                            appController.getMapComponentController().unColorTerritory(eventObject.getIdentity());
                            appController.getHeaderComponentController().writeIntoTextArea("Territory " + eventObject.getIdentity() + " is fair play!" + "\n");
                        }
                    });
                    appController.getHeaderComponentController().getBtnSave().setDisable(false);
                    appController.getHeaderComponentController().getBtnUndo().setDisable(false);
                }
                //appController.startRound();
            } catch (IOException e) {
                e.printStackTrace();
            }
            primaryStage.show(); // show game
        }
        else {
            lbl_message.setText("No game or XML has been loaded");
            lbl_message.setStyle("-fx-opacity: 1;");
        }
    }*/
}
