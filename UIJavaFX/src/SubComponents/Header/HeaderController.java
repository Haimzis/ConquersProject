package SubComponents.Header;

import GameEngine.GameEngine;
import GameObjects.Player;
import Generated.Game;
import MainComponents.AppController;
import javafx.beans.binding.Bindings;
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
                //setCurrentPlayerInTurnLbl(GameEngine.gameManager.getCurrentPlayerTurn().getPlayer_name());
            }
            else {
                //Show message and animation of ending round.
                writeIntoTextArea("Round " + GameEngine.gameManager.roundNumber + " has ended");
                GameEngine.gameManager.endOfRoundUpdates();
                currentPlayerInTurnLabel.setText("None");
                checkWinnerIfAny();
                btnManageRound.setText(START_ROUND);
            }
        }
        else { //Start Round
            appController.startRound();
            btnManageRound.setText(END_TURN);
        }
    }

    @FXML
    private void onUndopressListener() {
        if(GameEngine.gameManager.isUndoPossible()) {
            writeIntoTextArea("Round " + GameEngine.gameManager.roundNumber + " has been undone");
            GameEngine.gameManager.roundUndo();
        }
    }

    @FXML
    public void onSaveBtnPressListener() {

        //GameEngine.saveGame(Paths.get(absolutePath), GameEngine.gameManager);
    }

    private void checkWinnerIfAny() {
        if(GameEngine.gameManager.isGameOver()) { //Need to disable the map and re enable other buttons.
            Player winner = GameEngine.gameManager.getWinnerPlayer();
            if(winner == null) { //Show draw message

            }
            else { //Need to show the winner.

            }
        }
    }

    public void bindLabel() {
        StringProperty playerName = new SimpleStringProperty();
        currentPlayerInTurnLabel.textProperty().bind(playerName);
    }
}
