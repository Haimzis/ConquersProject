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
    @FXML private Label lblPlayerID;
    @FXML private Label lblPlayerName;
    @FXML private Label lblPlayerTurings;
    @FXML private HBox hbPlayerColor;
    @FXML private ListView<String> lvUnits;
    @FXML private TableView<Territory> tvTerritories;
    @FXML private SplitPane spDividerComponent;
    private Player currentPlayer;

    public String getCurrentPlayerColor() {
        return currentPlayer.getColor();
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }
    public void loadTerritoriesToTableView(){
        tvTerritories.setEditable(false);
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

        //noinspection unchecked
        tvTerritories.getColumns().addAll(IDCol, profitCol, armyThresholdCol);
    }
    @FXML
    private void loadConquerUnitsOnSelectedTerritory(){
        Territory territory = tvTerritories.getSelectionModel().getSelectedItem();
        ObservableList<String> items = null;
        if(territory == null) {
            items= FXCollections.observableArrayList(new ArrayList<>(0));
        }
        else { //Show only if its the current player territory.
            if(territory.getConquer().getID() == GameEngine.gameManager.getCurrentPlayerTurn().getID()) {
                items =FXCollections.observableArrayList(createListOfUnitsStrings(territory));
            }
        }
        lvUnits.setItems(items);
    }
    private void clearConquerUnitsListView(){
        lvUnits.setItems(FXCollections.observableArrayList(new ArrayList<>(0)));
    }
    @FXML
    private void releasedInnerTab(){
        clearConquerUnitsListView();
        dividerClose();
    }
    private void dividerClose(){
        if(tvTerritories.getItems().size()==0)
            spDividerComponent.setDividerPosition(0, 0);
        else{
            spDividerComponent.setDividerPosition(0, 0.3);
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
        this.lblPlayerID.setText(Integer.toString(currentPlayer.getID()));
        this.lblPlayerName.setText(currentPlayer.getPlayerName());
        this.lblPlayerTurings.setText(Integer.toString(currentPlayer.getFunds()));
        this.hbPlayerColor.setStyle("-fx-background-color: "+ getCurrentPlayerColor());
        if(GameEngine.gameManager!=null){
            final ObservableList<Territory> data =
                    FXCollections.observableArrayList(GameEngine.gameManager.getTerritoryListByPlayer(currentPlayer));
            tvTerritories.setItems(data);
        }
    }
}
