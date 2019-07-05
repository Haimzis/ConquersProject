package SubComponents.Header;

import Exceptions.invalidInputException;
import GameEngine.*;
import GameObjects.Player;
import MainComponents.AppController;
import SubComponents.MapTable.MapController;
import javafx.animation.*;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.nio.file.Paths;


public class HeaderController {
    private static final String END_TURN = "End Turn";
    private static final String START_ROUND = "Start Round";
    private static final String NEW_GAME = "New Game";
    public AnchorPane HeaderComponent;
    private AppController mainController;
    @FXML private  TextFlow tfHeaderInfoArea;
    @FXML private Label lblCurrentPlayerInTurn;
    @FXML private Button btnManageRound;
    @FXML private Button btnSave;
    @FXML private Button btnUndo;
    @FXML private MenuButton btnStyles;
    @FXML private ToggleButton btnAnimationToggle;
    @FXML private ToggleButton btnReplay;
    @FXML private Label lblError;
    @FXML private Button btnRetire;
    @FXML private Label lblWinner;
    private StringProperty currentPlayerProperty;
    public String currentTheme = "Default";

    //*********************//
    /*  Getters & Setters  */
    //*********************//
    public ToggleButton getBtnAnimationToggle() {
        return btnAnimationToggle;
    }
    public void setMainController(AppController mainController) { this.mainController = mainController; }
    public void setCurrentPlayerInTurnLbl(String currentPlayerName) {
        currentPlayerProperty.setValue(currentPlayerName);
    }
    public Button getBtnManageRound() {
        return btnManageRound;
    }
    public Button getBtnRetire() {
        return btnRetire;
    }
    public Button getBtnSave() {
        return btnSave;
    }
    public Button getBtnUndo() {
        return btnUndo;
    }

    //*********************//
    /*    Functionality    */
    //*********************//
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
        lblError.setText("This is not a valid territory!");
        lblError.setVisible(true);
    }

    public void writeIntoError(String error) {
        lblError.setText(error);
        lblError.setVisible(true);
    }

    public void hideErrorLabel() {
        lblError.setVisible(false);
    }


    public  void writeIntoTextArea(String text) {
        Text textToAdd = new Text(text);
        tfHeaderInfoArea.getChildren().add(textToAdd);
    }

    public void loadBinding() {
        currentPlayerProperty = new SimpleStringProperty(mainController.getCurrentGameManager().getCurrentPlayerName());
        StringExpression currentRoundSE = Bindings.concat(currentPlayerProperty);
        lblCurrentPlayerInTurn.textProperty().bind(currentRoundSE);
    }



    @FXML
    private void changeToDefaultTheme(){
        mainController.getPrimaryStage().getScene().getStylesheets().clear();
        mainController.getPrimaryStage().getScene().getStylesheets().add("/Resources/Default.css");
        btnStyles.setText("Default");
        lblCurrentPlayerInTurn.setFont(Font.font("System", 54));
        lblCurrentPlayerInTurn.setStyle("-fx-text-fill: black");
        currentTheme = "Default";
    }
    @FXML
    private void changeToThemeOne(){
        mainController.getPrimaryStage().getScene().getStylesheets().clear();
        mainController.getPrimaryStage().getScene().getStylesheets().add("/Resources/Theme1.css");
        btnStyles.setText("Ocean");
        lblCurrentPlayerInTurn.setFont(Font.loadFont(getClass().getResourceAsStream("/Resources/ocean.ttf"), 54));
        lblCurrentPlayerInTurn.setStyle("-fx-text-fill: darkblue");
        currentTheme = "Ocean";
    }



    @FXML
    private void changeToThemeTwo(){
        mainController.getPrimaryStage().getScene().getStylesheets().clear();
        mainController.getPrimaryStage().getScene().getStylesheets().add("/Resources/BlackCore.css");
        btnStyles.setText("Black Core");
        lblCurrentPlayerInTurn.setFont(Font.loadFont(getClass().getResourceAsStream("/Resources/Avengers.ttf"), 54));
        lblCurrentPlayerInTurn.setStyle("-fx-text-fill: rgb(0, 51, 102)");
        currentTheme = "Black Core";

    }

    @FXML
    public void setReplayState(){
        mainController.getReplayComponentController().setReplayState();
    }

    @FXML
    public void roundManagerBtnListener() {
        if(!btnManageRound.getText().equals(NEW_GAME)) {
            if (!mainController.getCurrentGameManager().roundStarted()) {
                writeIntoTextArea("Round " + mainController.getCurrentGameManager().getRoundNumber() + " has started" + "\n");
                btnManageRound.setText(END_TURN);
                mainController.startRound();
                mainController.getMapComponentController().disableMap(false);
                setButtonsDisabled(true);

            } else { // round started already
                if (mainController.getCurrentGameManager().isCycleOver()) {//you're the last bitch, end your turn and call endOfRoundUpdate
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
            lblWinner.setVisible(false);
            try {
                GameDescriptor gameDescriptor = mainController.getGameEngine().loadXML(mainController.getCurrentGameManager().getGameDescriptor().getLastKnownGoodString());
                mainController.startGame(gameDescriptor);
            } catch (invalidInputException ignore) {
            }
            mainController.loadInformation();
            mainController.createMap();
            tfHeaderInfoArea.getChildren().clear();
            btnManageRound.setText(START_ROUND);
        }
    }

    private void nextTurn() {
        lblError.setVisible(false);
        mainController.nextPlayer();
    }

    private void newRound() {
        writeIntoTextArea("Round " + mainController.getCurrentGameManager().getRoundNumber() + " has ended" + "\n");
        btnManageRound.setText(START_ROUND);
        mainController.endOfRoundUpdates();
        mainController.getMapComponentController().disableMap(true);
        setCurrentPlayerInTurnLbl("None");
        setButtonsDisabled(false);
        if (mainController.getCurrentGameManager().isGameOver())
            checkWinnerIfAny();
    }

    @FXML
    private void onUndoPressListener() {
        if(mainController.getCurrentGameManager().isUndoPossible()) {
            writeIntoTextArea("Round " + (mainController.getCurrentGameManager().getRoundNumber() - 1) + " has been undone." + "\n");
            mainController.getCurrentGameManager().roundUndo();
            mainController.getInformationComponentController().undoUpdate();
            mainController.getMapComponentController().clearMap();
            mainController.createMap();
            if(!mainController.getCurrentGameManager().isUndoPossible()) {
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

        if(GameEngine.saveGame(Paths.get(selectedFile.getAbsolutePath()), mainController.getCurrentGameManager())) {
            lblError.setText("Game saved successfully");
        }
        else {
            lblError.setText("Failed to save game");
        }
        showErrorLabel();
    }



    private void showErrorLabel() {
        lblError.setVisible(true);
        PauseTransition visiblePause = new PauseTransition(
                Duration.seconds(3)
        );
        visiblePause.setOnFinished(
                event -> lblError.setVisible(false)
        );
        visiblePause.play();
    }

    public void writeAndShowError(String error) {
        lblError.setText(error);
        showErrorLabel();
    }

    @FXML
    private void exitToWelcomeScreen() {
        mainController.launchWelcomeScreen();
    }

    @FXML
    private void retirePressListener() {
        mainController.getCurrentGameManager().selectedPlayerRetirement();
        writeIntoTextArea(mainController.getCurrentGameManager().getCurrentPlayerName() + " Has retired." + "\n");

        if(mainController.getCurrentGameManager().checkIfOnlyOnePlayer()) {
            forceWinner();
            mainController.loadInformation();
            mainController.getInformationComponentController().setFocusOnCurrentPlayer();
            return;
        }
        if(!mainController.getCurrentGameManager().isNextPlayerNull()) {
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
        Player winner = mainController.getCurrentGameManager().getForcedWinner();
        writeIntoTextArea(winner.getPlayerName() + " has won by a technical.");
        lblWinner.setVisible(true);
        setButtonsDisabled(true);
        btnRetire.setDisable(true);
        mainController.getMapComponentController().disableMap(true);
        btnManageRound.setText(NEW_GAME);
        if(MapController.isAnimationOn) {
            animateWinner();
        }
    }

    private void checkWinnerIfAny() {
        if(mainController.getCurrentGameManager().isGameOver()) {
            Player winner = mainController.getCurrentGameManager().getWinnerPlayer();
            if(winner == null) { //Show draw message
                setCurrentPlayerInTurnLbl("Draw!");
            }
            else { //Need to show the winner.
                setCurrentPlayerInTurnLbl(winner.getPlayerName());
                lblWinner.setVisible(true);
                if(MapController.isAnimationOn) {
                    animateWinner();
                }
            }
            writeIntoTextArea("Game ended");
            setButtonsDisabled(true);
            btnRetire.setDisable(true);
            btnReplay.setDisable(true);
            mainController.getMapComponentController().disableMap(true);
            btnManageRound.setText(NEW_GAME);
        }
    }

    private void animateWinner() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(2), new KeyValue(lblCurrentPlayerInTurn.textFillProperty(), Color.GOLD)),
                new KeyFrame(Duration.seconds(2), new KeyValue(lblWinner.textFillProperty(), Color.GOLD))
        );
        timeline.setAutoReverse(true);
        timeline.setCycleCount(4);
        timeline.play();
    }

    private void setButtonsDisabled(Boolean set) {
        btnSave.setDisable(set);
        btnUndo.setDisable(set);
        btnRetire.setDisable(!set);
    }
}
