package SubComponents.InformationTable.InnerTabPaneTable;

import GameEngine.GameEngine;
import GameObjects.Player;
import GameObjects.Territory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;

import java.util.ArrayList;
import java.util.List;


public class InnerTabPaneRootController {
    @FXML private AnchorPane rootComponent;
    @FXML private Label PlayerID;
    @FXML private Label PlayerName;
    @FXML private Label PlayerTurings;
    @FXML private HBox PlayerColor;
    @FXML private ListView<String> unitsListView;
    @FXML private TableView<Territory> territoriesTableView;
    private Player currentPlayer;


    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }
    public void createDataStructure(){
        territoriesTableView.setEditable(false);
        //nameCol
        TableColumn<Territory,Integer> IDCol = new TableColumn<>("ID");
        IDCol.setMinWidth(30);
        //rankCol
        TableColumn<Territory,Integer> profitCol = new TableColumn<>("Profit");
        profitCol.setMinWidth(60);
        //priceCol
        TableColumn<Territory,Integer> armyThresholdCol = new TableColumn<>("Army Threshold");
        armyThresholdCol.setMinWidth(100);

        territoriesTableView.setOnMouseClicked((MouseEvent event) -> {
            if (event.getButton().equals(MouseButton.PRIMARY)){
                loadConquerUnitsOnSelectedTerritory(territoriesTableView.getSelectionModel().getSelectedItem());
            }
        });

        IDCol.setCellValueFactory(
                new PropertyValueFactory<>("ID")
        );
        profitCol.setCellValueFactory(
                new PropertyValueFactory<>("Profit")
        );
        armyThresholdCol.setCellValueFactory(
                new PropertyValueFactory<>("ArmyThreshold")
        );
        //noinspection unchecked
        territoriesTableView.getColumns().addAll(IDCol, profitCol, armyThresholdCol);
    }
    void loadConquerUnitsOnSelectedTerritory(Territory territory){
        ObservableList<String> items =FXCollections.observableArrayList(createListOfUnitsStrings(territory));
        unitsListView.setItems(items);
    }
    public List<String> createListOfUnitsStrings(Territory territory){
        List<String> newList =new ArrayList<>(territory.getConquerArmyForce().getUnits().size());
        territory.getConquerArmyForce().getUnits().forEach(
                unit ->newList.add(unit.getType() +"/ Current Power: "+ unit.getCurrentFirePower()));
        return newList;
    }
    public void loadPlayerData(){
        this.PlayerID.setText(Integer.toString(currentPlayer.getID()));
        this.PlayerName.setText(currentPlayer.getPlayerName());
        this.PlayerTurings.setText(Integer.toString(currentPlayer.getFunds()));
        this.PlayerColor.setStyle("-fx-background-color: "+ currentPlayer.getColor());
        if(GameEngine.gameManager!=null){
            final ObservableList<Territory> data =
                    FXCollections.observableArrayList(GameEngine.gameManager.getTerritoryListByPlayer(currentPlayer));
            territoriesTableView.setItems(data);
        }
    }
}
