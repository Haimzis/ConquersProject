package SubComponents.Header;

import GameEngine.GameEngine;
import GameObjects.Player;
import MainComponents.AppController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;


public class HeaderController {
    public AnchorPane HeaderComponent;
    private AppController appController;
    @FXML private  TextFlow headerInfoArea;
    @FXML private Label currentPlayerInTurnLabel;

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
    public void endTurnOnPressListener() {
        if(!GameEngine.gameManager.isCycleOver()) {
            appController.nextPlayer();
        }
         else {
             //Show message and animation of ending round.
            writeIntoTextArea("Round " + GameEngine.gameManager.roundNumber + "has ended");
            GameEngine.gameManager.endOfRoundUpdates();
            checkWinnerIfAny();
            appController.startRound();
        }
        setCurrentPlayerInTurnLbl(GameEngine.gameManager.getCurrentPlayerTurn().getPlayer_name());
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
