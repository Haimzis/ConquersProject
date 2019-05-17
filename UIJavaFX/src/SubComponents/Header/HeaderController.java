package SubComponents.Header;

import Exceptions.invalidInputException;
import GameEngine.GameEngine;
import GameObjects.Player;
import MainComponents.AppController;
import SubComponents.MapTable.MapController;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Paths;


public class HeaderController {
    private static final String END_TURN = "End Turn";
    private static final String START_ROUND = "Start Round";
    private static final String NEW_GAME = "New Game";
    public AnchorPane HeaderComponent;
    private AppController mainController;
    @FXML private  TextFlow headerInfoArea;
    @FXML private Label currentPlayerInTurnLabel;
    @FXML private Button btnManageRound;
    @FXML private Button btnSave;
    @FXML private Button btnUndo;
    @FXML private MenuButton btnStyles;
    @FXML private ToggleButton btnAnimationToggle;
    @FXML private ToggleButton btnReplay;
    @FXML private Button btnExit;
    @FXML private Label errorLbl;
    @FXML private Button btnRetire;
    private StringProperty currentPlayerProperty;


    public void setErrorOfNotValidTerritory() {
        errorLbl.setText("This is not a valid territory!");
        errorLbl.setVisible(true);
    }

    public void writeIntoError(String error) {
        errorLbl.setText(error);
        errorLbl.setVisible(true);
    }

    public void hideErrorLabel() {
        errorLbl.setVisible(false);
    }
    public void setMainController(AppController mainController) { this.mainController = mainController; }

    public  void writeIntoTextArea(String text) {
        Text textToAdd = new Text(text);
        headerInfoArea.getChildren().add(textToAdd);
    }

    public void loadBinding() {
        currentPlayerProperty = new SimpleStringProperty(GameEngine.gameManager.getCurrentPlayerName());
        StringExpression currentRoundSE = Bindings.concat(currentPlayerProperty);
        currentPlayerInTurnLabel.textProperty().bind(currentRoundSE);
    }

    public void setCurrentPlayerInTurnLbl(String currentPlayerName) {
        currentPlayerProperty.setValue(currentPlayerName);
    }

    @FXML
    public void roundManagerBtnListener() {
        if(!btnManageRound.getText().equals(NEW_GAME)) {
            if (!GameEngine.gameManager.roundStarted()) {//you're the first bitch
                writeIntoTextArea("Round " + GameEngine.gameManager.roundNumber + " has started" + "\n");
                btnManageRound.setText(END_TURN);
                mainController.startRound();
                mainController.getMapComponentController().disableMap(false);
                setButtonsDisabled(true);

            } else { // round started already
                if (GameEngine.gameManager.isCycleOver()) {//you're the last bitch, end your turn and call endOfRoundUpdate
                    writeIntoTextArea("Round " + GameEngine.gameManager.roundNumber + " has ended" + "\n");
                    btnManageRound.setText(START_ROUND);
                    mainController.endOfRoundUpdates();
                    mainController.getMapComponentController().disableMap(true);
                    setCurrentPlayerInTurnLbl("None");
                    setButtonsDisabled(false);
                    if (mainController.isGameOver())
                        checkWinnerIfAny();
                } else { //normal - move next player..
                    errorLbl.setVisible(false);
                    mainController.nextPlayer();
                    MapController.actionBeenTaken = false;
                }
            }
        }
        else {// This bitch clicked on 'new game' button
            mainController.getMapComponentController().clearMap();
            try {
                mainController.getGameEngine().loadXML(mainController.getGameEngine().getDescriptor().getLastKnownGoodString());
            } catch (invalidInputException ignore) {
            }
            mainController.startGame();
            mainController.loadInformation();
            mainController.createMap();
            headerInfoArea.getChildren().clear();
            btnManageRound.setText(START_ROUND);
        }
    }

    @FXML
    private void onUndoPressListener() {
        if(GameEngine.gameManager.isUndoPossible()) {
            writeIntoTextArea("Round " + (GameEngine.gameManager.roundNumber - 1) + " has been undone." + "\n");
            GameEngine.gameManager.roundUndo();
            mainController.getInformationComponentController().undoUpdate();
            mainController.getMapComponentController().clearMap();
            mainController.createMap();
            if(!GameEngine.gameManager.isUndoPossible()) {
                btnUndo.setDisable(true);
            }
        }
        else {
            btnUndo.setDisable(true);
        }
        MapController.actionBeenTaken = false;
    }

    @FXML
    public void onSaveBtnPressListener() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save Game");
        File selectedFile = null;
        while(selectedFile== null) {
            selectedFile = chooser.showSaveDialog(null);
        }

        if(GameEngine.saveGame(Paths.get(selectedFile.getAbsolutePath()), GameEngine.gameManager)) {
            errorLbl.setText("Game saved successfully");
        }
        else {
            errorLbl.setText("Failed to save game");
        }
    }

    @FXML
    private void exitToWelcomeScreen() {
        mainController.launchWelcomeScreen();
    }

    private void checkWinnerIfAny() {
        if(GameEngine.gameManager.isGameOver()) {
            Player winner = GameEngine.gameManager.getWinnerPlayer();
            if(winner == null) { //Show draw message
                setCurrentPlayerInTurnLbl("Draw!");
            }
            else { //Need to show the winner.
                setCurrentPlayerInTurnLbl(winner.getPlayerName());
            }
            setButtonsDisabled(true);
            btnRetire.setDisable(true);
            mainController.getMapComponentController().disableMap(true);
            btnManageRound.setText(NEW_GAME);
        }
    }

    private void setButtonsDisabled(Boolean set) {
        btnSave.setDisable(set);
        btnUndo.setDisable(set);
        btnRetire.setDisable(!set);
    }
}
