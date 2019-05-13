package SubComponents.Header;

import GameEngine.GameEngine;
import GameObjects.Player;
import MainComponents.AppController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Paths;


public class HeaderController {
    private static final String END_TURN = "End Turn";
    private static final String START_ROUND = "Start Round";
    public AnchorPane HeaderComponent;
    private AppController appController;
    @FXML private  TextFlow headerInfoArea;
    @FXML private Label currentPlayerInTurnLabel;
    @FXML private Button btnManageRound;

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
            }
            else {
                //Show message and animation of ending round.
                writeIntoTextArea("Round " + GameEngine.gameManager.roundNumber + "has ended");
                GameEngine.gameManager.endOfRoundUpdates();
                checkWinnerIfAny();
                btnManageRound.setText(START_ROUND);
            }
            setCurrentPlayerInTurnLbl(GameEngine.gameManager.getCurrentPlayerTurn().getPlayer_name());
        }
        else { //Start Round
            appController.startRound();
            btnManageRound.setText(END_TURN);
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
}
