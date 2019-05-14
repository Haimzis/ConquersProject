package SubComponents.Header;

import GameEngine.GameEngine;
import GameObjects.Player;
import MainComponents.AppController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Paths;


public class HeaderController {
    private static final String END_TURN = "End Turn";
    private static final String START_ROUND = "Start Round";
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


    public void setErrorOfNotValidTerritory() {
        errorLbl.setText("This is not a valid territory!");
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

    public void setCurrentPlayerInTurnLbl(String currentPlayerName) {
        currentPlayerInTurnLabel.setText(currentPlayerName);
    }

    //TODO: This needs to move to an round manager.
    @FXML
    public void roundManagerBtnListener() {
        if(btnManageRound.getText().equals(END_TURN)) {
            if(!GameEngine.gameManager.isCycleOver()) {
                hideErrorLabel();
                mainController.nextPlayer();
                setCurrentPlayerInTurnLbl(GameEngine.gameManager.getCurrentPlayerTurn().getPlayerName());
            }
            else {
                //Show message and animation of ending round.
                writeIntoTextArea("Round " + GameEngine.gameManager.roundNumber + " has ended" + "\n");
                GameEngine.gameManager.endOfRoundUpdates();
                currentPlayerInTurnLabel.setText("None");
                checkWinnerIfAny();
                setButtonsDisabled(false);
                btnManageRound.setText(START_ROUND);
                mainController.getMapComponentController().disableMap(true);
            }
        }
        else { //Start Round
            mainController.startRound();
            mainController.getMapComponentController().disableMap(false);
            setButtonsDisabled(true);
            btnManageRound.setText(END_TURN);
        }
    }

    @FXML
    private void onUndoPressListener() {
        if(GameEngine.gameManager.isUndoPossible()) {
            writeIntoTextArea("Round " + (GameEngine.gameManager.roundNumber - 1) + " has been undone." + "\n");
            GameEngine.gameManager.roundUndo();
            mainController.getInformationComponentController().undoUpdate();

        }
        else {
            btnUndo.setDisable(true);
        }
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

    //TODO: Need to disable the game after the winner is shown.
    private void checkWinnerIfAny() {
        if(GameEngine.gameManager.isGameOver()) {
            Player winner = GameEngine.gameManager.getWinnerPlayer();
            if(winner == null) { //Show draw message

            }
            else { //Need to show the winner.

            }
            btnManageRound.setDisable(true);
        }
    }

    private void setButtonsDisabled(Boolean set) {
        btnSave.setDisable(set);
        btnUndo.setDisable(set);
    }
}
