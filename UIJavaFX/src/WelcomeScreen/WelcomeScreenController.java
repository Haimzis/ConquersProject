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
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static Resources.ResourceConstants.APP_FXML_INCLUDE_RESOURCE;

public class WelcomeScreenController {
@FXML private FlowPane WelcomeScreenComponent;
@FXML private Button btn_loadXML;
@FXML private Button btn_loadGame;
@FXML private TextField tbx_path;
@FXML private Button btn_choosePath;
@FXML private Label lbl_message;
    private Stage primaryStage;
    private SimpleBooleanProperty isFileSelected;
    private SimpleStringProperty selectedFileProperty;
    private GameEngine gameEngine;
    private Boolean loadSucceed;
    private boolean gameLoaded;

    public WelcomeScreenController(){
        gameEngine = new GameEngine();
        isFileSelected = new SimpleBooleanProperty(false);
        selectedFileProperty = new SimpleStringProperty("");
        loadSucceed=false;
    }
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

        String absolutePath = selectedFile.getAbsolutePath();
        selectedFileProperty.set(absolutePath);
        //need to do task
        tbx_path.setText(selectedFile.getPath());
        //
        isFileSelected.set(true);
    }
    @FXML
    public void btn_loadXMLAction(){
        if(!isFileSelected.getValue()) {
            lbl_message.setText("Yoo Matha fucka, choose file!");
            lbl_message.setStyle("-fx-opacity: 1;");
        }
        else {
            lbl_message.setStyle("-fx-opacity: 0;");
            loadSucceed = gameEngine.loadXML(tbx_path.getText());
            if (!loadSucceed) {
                lbl_message.setText("could not load XML file!");
                lbl_message.setStyle("-fx-opacity: 1;");
            }
        }
    }
    @FXML
    public void setBtn_loadGameAction(){
        if(!isFileSelected.getValue()) {
            lbl_message.setText("Yoo Matha fucka, choose file!");
            lbl_message.setStyle("-fx-opacity: 1;");
        }
        else {
            lbl_message.setStyle("-fx-opacity: 0;");
            gameLoaded = gameEngine.loadGame(gameEngine.getLoadFilePath(tbx_path.getText()));
            if (!gameLoaded) {
                lbl_message.setText("Could not load saved game file!");
                lbl_message.setStyle("-fx-opacity: 1;");
            }
        }
    }
    @FXML
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
                //wire up game engine to appController
                AppController appController = fxmlLoader.getController();
                appController.setPrimaryStage(primaryStage);
                appController.setGameEngine(gameEngine);
                //start game
                if(!gameLoaded) { //Check if its a loaded game
                    appController.startGame();
                }
                //first load of xml into UI
                appController.createMap();
                appController.loadInformation();
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
    }
}
