package SubComponents.MapTable;

import DataContainersTypes.Board;
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


    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    public void setMap(Board newMap){
        this.map = newMap;
    }
    public void createMap(){
        //initialize scroll pane
        //this.MapComponent = new ScrollPane();
        //MapComponent.setMaxSize(500,400);
        //MapComponent.setPrefSize(500,400);
        //MapComponent.setId("MapComponent");

        //initialize grid pane
        //this.GridComponent = new GridPane();
        //GridComponent.setMaxSize(497,397);
        //GridComponent.setPrefSize(497,397);
        //MapComponent.setContent(GridComponent);

        for (int i = 0; i < map.getRows(); i++) {
            RowConstraints row = new RowConstraints(100);
            row.setVgrow(Priority.valueOf("SOMETIMES"));
            GridComponent.getRowConstraints().add(row);
        }
        for (int i = 0; i < map.getColumns(); i++) {
            ColumnConstraints column = new ColumnConstraints(30);
            column.setHgrow(Priority.valueOf("SOMETIMES"));
            GridComponent.getColumnConstraints().add(column);
        }

        //initialize buttons list and constraints
        int counter= 1;
        for(int i=0; i<map.getRows();i++){
            for(int j=0; j<map.getColumns();j++){
                Territory territory =map.getTerritoryMap().get(counter) ;
                Button btnTerritory = new Button("btn");
                btnTerritory.setId("btn_Territory_" + counter);
                btnTerritory.setText(
                        "ID:"+territory.getID()+
                        "&#10;ThreshHold: "+ territory.getArmyThreshold()+
                        "&#10;Production: "+ territory.getProfit());
                btnTerritory.getStyleClass().add("btn_Territory");
                btnTerritory.setPrefSize(146,98);
                territoriesButtons.put(counter,btnTerritory);
                GridComponent.add(btnTerritory,j,i);
                counter++;
            }
        }
    }
}
