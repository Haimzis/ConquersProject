package SubComponents.MapTable;

import DataContainersTypes.Board;
import GameEngine.GameEngine;
import GameObjects.Territory;
import MainComponents.AppController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

import java.util.HashMap;
import java.util.Map;


public class MapController {

    @FXML private ScrollPane MapComponent;
    @FXML private GridPane GridComponent;
    private AppController mainController;
    private Map<Integer, Button> territoriesButtons= new HashMap<>();
    private Board map;
    private final int MIN_WIDTH_SIZE= 146;
    private final int MIN_HEIGHT_SIZE = 98;

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }
    public void setMap(Board newMap){
        this.map = newMap;
    }
    public void createMap(){

        int columns=map.getColumns(),rows=map.getRows();
        double heightSize = GridComponent.getPrefHeight() / rows,widthSize= GridComponent.getPrefWidth() / columns;
        if(heightSize < MIN_HEIGHT_SIZE)
            heightSize = MIN_HEIGHT_SIZE;
        if(widthSize < MIN_WIDTH_SIZE)
            widthSize = MIN_WIDTH_SIZE;
        for (int i = 0; i < rows; i++) {
            RowConstraints row = new RowConstraints(heightSize);
            row.setVgrow(Priority.valueOf("SOMETIMES"));
            GridComponent.getRowConstraints().add(row);
        }
        for (int i = 0; i < columns; i++) {
            ColumnConstraints column = new ColumnConstraints(widthSize);
            column.setHgrow(Priority.valueOf("SOMETIMES"));
            GridComponent.getColumnConstraints().add(column);
        }

        //initialize buttons list and constraints
        int counter= 1;
        for(int i=0; i<map.getRows();i++){
            for(int j=0; j<map.getColumns();j++){
                Territory territory =map.getTerritoryMap().get(counter) ;
                Button btnTerritory = new Button();
                btnTerritory.setId("btn_Territory_" + counter);
                btnTerritory.setText(
                        "ID: "+territory.getID()+
                        "\nThreshHold: "+ territory.getArmyThreshold()+
                        "\nProduction: "+ territory.getProfit());
                btnTerritory.getStyleClass().add("btn_Territory");
                btnTerritory.setPrefSize(widthSize,heightSize);
                btnTerritory.setMinSize(widthSize,heightSize);
                territoriesButtons.put(counter,btnTerritory);
                GridComponent.add(btnTerritory,j,i);
                btnTerritory.setOnAction(event -> onTerritoryPressListener(territory));
                counter++;
            }
        }
    }

    private void onTerritoryPressListener(Territory territory) {
       mainController.actOnTerritory(territory);
    }
}
