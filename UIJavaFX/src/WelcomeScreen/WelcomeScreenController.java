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
            loadSucceed = gameEngine.loadXML(tbx_path.getText());
            if (!loadSucceed) {
                lbl_message.setText("could not load XML file!");
                lbl_message.setStyle("-fx-opacity: 1;");
            } else {
                startGame();
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
            loadSucceed = gameEngine.loadGame(gameEngine.getLoadFilePath(tbx_path.getText()));
            if (!loadSucceed) {
                lbl_message.setText("could not load saved game file!");
                lbl_message.setStyle("-fx-opacity: 1;");
            } else {
                startGame();
            }
        }
    }
    private void startGame(){
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource(APP_FXML_INCLUDE_RESOURCE);
        fxmlLoader.setLocation(url);
        try {
            Parent root1 = fxmlLoader.load(url.openStream());
            Scene scene = new Scene(root1, 500, 550);
            //set new size of this stage
            primaryStage.setHeight(600);
            primaryStage.setWidth(900);
            primaryStage.setScene(scene);

            //wire up game engine to appController and create map
            AppController appController = fxmlLoader.getController();
            appController.setGameEngine(gameEngine);
            appController.createMap();
            appController.loadInformation();
            appController.startGame();
            appController.startRound();
            appController.nextPlayer();
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
