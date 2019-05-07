package WelcomeScreen;

import GameEngine.GameEngine;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

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
        loadSucceed=gameEngine.loadXML(tbx_path.getText());
        if(!loadSucceed){

        }
    }
    @FXML
    public void setBtn_loadGameAction(){
        loadSucceed = gameEngine.loadGame(gameEngine.getLoadFilePath(tbx_path.getText()));
    }

}
