package SubComponents.MapTable;

import DataContainersTypes.Board;
import GameEngine.GameEngine;
import GameObjects.Territory;
import MainComponents.AppController;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;


public class MapController {

    @FXML private ScrollPane MapComponent;
    @FXML private GridPane GridComponent;
    private AppController mainController;
    private Map<Integer, Button> territoriesButtons= new HashMap<>();
    private Board map;
    private Button currentlySelectedButton;
    public static boolean actionBeenTaken;
    public static boolean isAnimationOn = true;

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }
    public void setMap(Board newMap){
        this.map = newMap;
    }
    public void createMap(){
        int columns=map.getColumns(),rows=map.getRows();
        double heightSize = GridComponent.getPrefHeight() / rows,widthSize= GridComponent.getPrefWidth() / columns;
        int MIN_HEIGHT_SIZE = 98;
        if(heightSize < MIN_HEIGHT_SIZE)
            heightSize = MIN_HEIGHT_SIZE;
        int MIN_WIDTH_SIZE = 146;
        if(widthSize < MIN_WIDTH_SIZE)
            widthSize = MIN_WIDTH_SIZE;
        for (int i = 0; i < rows; i++) {
            RowConstraints row = new RowConstraints();
            row.setFillHeight(true);
            row.setVgrow(Priority.valueOf("ALWAYS"));
            GridComponent.getRowConstraints().add(row);
        }
        for (int i = 0; i < columns; i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setFillWidth(true);
            column.setHgrow(Priority.valueOf("ALWAYS"));
            GridComponent.getColumnConstraints().add(column);
        }

        //initialize buttons list and constraints
        int counter= 1;
        for(int i=0; i<map.getRows();i++){
            for(int j=0; j<map.getColumns();j++){
                Territory territory =map.getTerritoryMap().get(counter);
                Button btnTerritory = new Button();
                btnTerritory.setId("btn_Territory_" + counter);
                btnTerritory.setText(
                        "ID: "+territory.getID()+
                        "\nThreshHold: "+ territory.getArmyThreshold()+
                        "\nProduction: "+ territory.getProfit());
                btnTerritory.getStyleClass().add("btn_Territory");
                btnTerritory.setMinSize(widthSize,heightSize);
                btnTerritory.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                territoriesButtons.put(counter,btnTerritory);
                GridComponent.add(btnTerritory,j,i);
                btnTerritory.setOnAction(event -> {
                    if(!actionBeenTaken) {
                        Object node = event.getSource();
                        currentlySelectedButton = (Button)node;
                        onTerritoryPressListener(territory);
                    }
                    else {
                        mainController.getHeaderComponentController().writeIntoError("You have already played your turn");
                    }
                });

                if(territory.isConquered()) { //Color map if its conquered.(For load game functionality).
                    btnTerritory.setStyle("-fx-background-color: " + mainController.getColorByPlayerID(territory.getConquerID()));
                }
                counter++;
            }
        }
        disableMap(true);
    }

    private void onTerritoryPressListener(Territory territory) {
        GameEngine.gameManager.setSelectedTerritoryForTurn(territory);
        if(!GameEngine.gameManager.getCurrentPlayerTerritories().isEmpty()) {
            if (GameEngine.gameManager.isTerritoryBelongsCurrentPlayer()) {
                mainController.showOwnTerritoryPopup();
            }
            else {
                if(GameEngine.gameManager.isTargetTerritoryValid()) {
                    mainController.showAttackPopup();
                    mainController.getHeaderComponentController().hideErrorLabel();
                }
                else { //Not valid territory
                    mainController.getHeaderComponentController().setErrorOfNotValidTerritory();
                }
            }
        }
        else { //No territories.
            mainController.showAttackPopup();
            mainController.getHeaderComponentController().hideErrorLabel();
        }
    }

    public void colorTerritory() {
        Territory territory = GameEngine.gameManager.getSelectedTerritoryByPlayer();
        if(territory.isConquered()) {
            if(isAnimationOn) {
                animateColor(territory);
                popTerritoriesOfWinner(1.0 ,1.0 ,1.15, 1.15);
            }
            else {
                currentlySelectedButton.setStyle("-fx-background-color: " + mainController.getColorByPlayerID(territory.getConquerID()));
            }
        }
    }

    private void animateColor(Territory territory) {
        currentlySelectedButton.setStyle("-fx-background-color: " + mainController.getColorByPlayerID(territory.getConquerID()));
        fadeIn();

    }

    public void  disableMap(Boolean value) {
        GridComponent.setDisable(value);
        territoriesButtons.forEach((integer, button) -> {
            if(value) {
                button.setOpacity(0.5);
            }
            else {
                button.setOpacity(1.0);
            }
        });
    }

    public void clearMap() {
        territoriesButtons = new HashMap<>();
        GridComponent.getChildren().clear();
        GridComponent.getRowConstraints().clear();
        GridComponent.getColumnConstraints().clear();
    }

    public void unColorTerritory(Integer territoryID) {
        territoriesButtons.get(territoryID).setStyle("");
        if(isAnimationOn) {
            fadeOut(territoryID);
        }
    }

    private void fadeIn() {
        FadeTransition fd = new FadeTransition(Duration.seconds(2), currentlySelectedButton);
        fd.setCycleCount(2);
        fd.setFromValue(1);
        fd.setToValue(0.3);
        fd.setAutoReverse(true);
        fd.play();
    }

    private void fadeOut(Integer territoryId) {
        FadeTransition fd = new FadeTransition(Duration.seconds(2), territoriesButtons.get(territoryId));
        fd.setCycleCount(2);
        fd.setFromValue(1);
        fd.setToValue(0.3);
        fd.setAutoReverse(true);
        fd.play();
    }


    private void popTerritoriesOfWinner(double from_x , double from_y , double to_x , double to_y) {
        for(Territory territory: GameEngine.gameManager.getCurrentPlayerTerritories()) {
            ScaleTransition st = new ScaleTransition(Duration.millis(2000), territoriesButtons.get(territory.getID()));
            st.setCycleCount(2);
            st.setFromX(from_x);
            st.setFromY(from_y);
            st.setToX(to_x);
            st.setToY(to_y);
            st.setAutoReverse(true);
            st.play();
        }
    }
}
