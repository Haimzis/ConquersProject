package SubComponents.InformationTable.InnerTabPaneTable;

import GameEngine.GameEngine;
import GameObjects.Player;
import GameObjects.Territory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
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
    @FXML private SplitPane SplitPaneComponent;
    private Player currentPlayer;

    public String getCurrentPlayerColor() {
        return currentPlayer.getColor();
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }
    public void loadTerritoriesToTableView(){
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

        IDCol.setCellValueFactory(
                new PropertyValueFactory<>("ID")
        );
        profitCol.setCellValueFactory(
                new PropertyValueFactory<>("Profit")
        );
        armyThresholdCol.setCellValueFactory(
                new PropertyValueFactory<>("ArmyThreshold")
        );

        //CSS
        IDCol.setId("innerTabId_Id");
        profitCol.setId("innerTabProfit_Id");
        armyThresholdCol.setId("innerTabThreshold_Id");

        //noinspection unchecked
        territoriesTableView.getColumns().addAll(IDCol, profitCol, armyThresholdCol);
    }
    @FXML
    private void loadConquerUnitsOnSelectedTerritory(){
        Territory territory = territoriesTableView.getSelectionModel().getSelectedItem();
        ObservableList<String> items = null;
        if(territory == null) {
            items= FXCollections.observableArrayList(new ArrayList<>(0));
        }
        else { //Show only if its the current player territory.
            if(territory.getConquer().getID() == GameEngine.gameManager.getCurrentPlayerTurn().getID()) {
                items =FXCollections.observableArrayList(createListOfUnitsStrings(territory));
            }
        }
        unitsListView.setItems(items);
    }
    private void clearConquerUnitsListView(){
        unitsListView.setItems(FXCollections.observableArrayList(new ArrayList<>(0)));
    }
    @FXML
    private void releasedInnerTab(){
        clearConquerUnitsListView();
        dividerClose();
    }
    private void dividerClose(){
        if(territoriesTableView.getItems().size()==0)
            SplitPaneComponent.setDividerPosition(0, 0);
        else{
            SplitPaneComponent.setDividerPosition(0, 0.3);
        }
    }

    private List<String> createListOfUnitsStrings(Territory territory){
        if(territory.getConquer() == null)
            return new ArrayList<>(0);
        List<String> newList =new ArrayList<>(territory.getConquerArmyForce().getUnits().size());
        territory.getConquerArmyForce().getUnits().forEach(
                unit ->newList.add(unit.getType() +"/ Current Power: "+ unit.getCurrentFirePower()));
        return newList;
    }
    public void loadPlayerData(){
        this.PlayerID.setText(Integer.toString(currentPlayer.getID()));
        this.PlayerName.setText(currentPlayer.getPlayerName());
        this.PlayerTurings.setText(Integer.toString(currentPlayer.getFunds()));
        this.PlayerColor.setStyle("-fx-background-color: "+ getCurrentPlayerColor());
        if(GameEngine.gameManager!=null){
            final ObservableList<Territory> data =
                    FXCollections.observableArrayList(GameEngine.gameManager.getTerritoryListByPlayer(currentPlayer));
            territoriesTableView.setItems(data);
        }
    }
}
