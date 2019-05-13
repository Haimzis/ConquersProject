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


public class HeaderController {
    private static final String END_TURN = "End Turn";
    private static final String START_ROUND = "Start Round";
    public AnchorPane HeaderComponent;
    private AppController appController;
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
    public void setMainController(AppController mainController) { this.appController = mainController; }

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
                appController.nextPlayer();
                setCurrentPlayerInTurnLbl(GameEngine.gameManager.getCurrentPlayerTurn().getPlayer_name());
            }
            else {
                //Show message and animation of ending round.
                writeIntoTextArea("Round " + GameEngine.gameManager.roundNumber + " has ended" + "\n");
                GameEngine.gameManager.endOfRoundUpdates();
                currentPlayerInTurnLabel.setText("None");
                checkWinnerIfAny();
                setButtonsDisabled(false);
                btnManageRound.setText(START_ROUND);
            }
        }
        else { //Start Round
            appController.startRound();
            setButtonsDisabled(true);
            btnManageRound.setText(END_TURN);
        }
    }

    @FXML
    private void onUndoPressListener() {
        if(GameEngine.gameManager.isUndoPossible()) {
            writeIntoTextArea("Round " + (GameEngine.gameManager.roundNumber - 1) + " has been undone." + "\n");
            GameEngine.gameManager.roundUndo();
            updateRoundInfo();
        }
        else {
            btnUndo.setDisable(true);
        }
    }

    private void updateRoundInfo() {

    }

    @FXML
    public void onSaveBtnPressListener() {

        //GameEngine.saveGame(Paths.get(absolutePath), GameEngine.gameManager);
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
