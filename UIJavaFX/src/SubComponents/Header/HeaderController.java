package SubComponents.Header;

import Exceptions.invalidInputException;
import GameEngine.GameEngine;
import GameObjects.Player;
import MainComponents.AppController;
import SubComponents.MapTable.MapController;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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
    @FXML private MenuItem defaultTheme;
    @FXML private MenuItem themeOne;
    @FXML private MenuItem themeTwo;
    private StringProperty currentPlayerProperty;
    private static final PseudoClass THEME = PseudoClass.getPseudoClass("ocean");

    public ToggleButton getBtnAnimationToggle() {
        return btnAnimationToggle;
    }

    @FXML
    private void animationController() {
        if(btnAnimationToggle.isSelected()) {
            MapController.isAnimationOn = true;
            btnAnimationToggle.setText("Animations On");
        }
        else {
            MapController.isAnimationOn = false;
            btnAnimationToggle.setText("Animations Off");
        }
    }

    public void setBtnReplayAccessible(){
        btnReplay.setDisable(false);
    }
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
    private void changeToDefaultTheme(){
        errorLbl.getScene().getStylesheets().clear();
        errorLbl.getScene().getStylesheets().add("/MainComponents/Default.css");
        btnStyles.setText("Default");
    }
    @FXML
    private void changeToThemeOne(){
        errorLbl.getScene().getStylesheets().clear();
        errorLbl.getScene().getStylesheets().add("/MainComponents/Theme1.css");
        btnStyles.setText("Ocean");
        btnStyles.pseudoClassStateChanged(THEME , true);
    }

    public Button getBtnManageRound() {
        return btnManageRound;
    }

    @FXML
    private void changeToThemeTwo(){
        errorLbl.getScene().getStylesheets().clear();
        errorLbl.getScene().getStylesheets().add("/MainComponents/Avengers.css");
        btnStyles.setText("Avengers");
    }

    @FXML
    public void setReplayState(){
        mainController.getReplayComponentController().setReplayState();
    }

    @FXML
    public void roundManagerBtnListener() {
        if(!btnManageRound.getText().equals(NEW_GAME)) {
            if (!GameEngine.gameManager.roundStarted()) { //you're the first bitch
                writeIntoTextArea("Round " + GameEngine.gameManager.roundNumber + " has started" + "\n");
                btnManageRound.setText(END_TURN);
                mainController.startRound();
                mainController.getMapComponentController().disableMap(false);
                setButtonsDisabled(true);

            } else { // round started already
                if (GameEngine.gameManager.isCycleOver()) {//you're the last bitch, end your turn and call endOfRoundUpdate
                    newRound();
                } else { //normal - move next player..
                    nextTurn();
                }
            }
            MapController.actionBeenTaken = false;
            mainController.getInformationComponentController().setFocusOnCurrentPlayer();
        }
        else {// This bitch clicked on 'new game' button
            mainController.getMapComponentController().clearMap();
            btnReplay.setDisable(true);
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

    private void nextTurn() {
        errorLbl.setVisible(false);
        mainController.nextPlayer();
    }

    private void newRound() {
        writeIntoTextArea("Round " + GameEngine.gameManager.roundNumber + " has ended" + "\n");
        btnManageRound.setText(START_ROUND);
        mainController.endOfRoundUpdates();
        mainController.getMapComponentController().disableMap(true);
        setCurrentPlayerInTurnLbl("None");
        setButtonsDisabled(false);
        if (mainController.isGameOver())
            checkWinnerIfAny();
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

    @FXML
    private void retirePressListener() {
        GameEngine.gameManager.selectedPlayerRetirement();
        writeIntoTextArea(GameEngine.gameManager.getCurrentPlayerName() + " Has retired." + "\n");

        if(GameEngine.gameManager.checkIfOnlyOnePlayer()) {
            forceWinner();
            mainController.loadInformation();
            mainController.getInformationComponentController().setFocusOnCurrentPlayer();
            return;
        }
        if(!GameEngine.gameManager.isNextPlayerNull()) {
            mainController.nextPlayer();
        }
        else { //More than one player , but the last one retired so a new round begins
            newRound();
        }
        MapController.actionBeenTaken = false;
        mainController.getInformationComponentController().loadInformation();
        mainController.getInformationComponentController().setFocusOnCurrentPlayer();
    }

    private void forceWinner() {
        Player winner = GameEngine.gameManager.getForcedWinner();
        errorLbl.setText(winner.getPlayerName());
        writeIntoTextArea(winner.getPlayerName() + " has won by a technical.");
        setButtonsDisabled(true);
        btnRetire.setDisable(true);
        mainController.getMapComponentController().disableMap(true);
        btnManageRound.setText(NEW_GAME);
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
            writeIntoTextArea("Game ended");
            setButtonsDisabled(true);
            btnRetire.setDisable(true);
            btnReplay.setDisable(true);
            mainController.getMapComponentController().disableMap(true);
            btnManageRound.setText(NEW_GAME);
        }
    }

    private void setButtonsDisabled(Boolean set) {
        btnSave.setDisable(set);
        btnUndo.setDisable(set);
        btnRetire.setDisable(!set);
    }
    public Button getBtnSave() {
        return btnSave;
    }

    public Button getBtnUndo() {
        return btnUndo;
    }
}
