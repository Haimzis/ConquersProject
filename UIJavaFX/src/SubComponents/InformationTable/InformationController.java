package SubComponents.InformationTable;

import GameEngine.GameEngine;
import GameObjects.Player;
import GameObjects.Territory;
import GameObjects.Unit;
import MainComponents.AppController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class InformationController {
    public AnchorPane InformationComponent;
    private AppController mainController;
    @FXML private Label currentRound;
    @FXML private Label totalRounds;
    @FXML private TabPane playersInformationTabPane;
    @FXML private TableView<Unit> unitsTableView;


    public void loadInformation() {
        List<Player> playersList  =mainController.getGameEngine().getDescriptor().getPlayersList();
        Map<Integer, Territory> territoryMap = mainController.getGameEngine().getDescriptor().getTerritoryMap();
        Map<String , Unit> unitMap  = mainController.getGameEngine().getDescriptor().getUnitMap();

        playersList.forEach(player ->{
            addTabToPlayers(player.getPlayer_name());
            //more complicated.
            //TODO: i should build another tabpane for territories,army etc...
        });
        loadUnitsToTableView();
    }

    private void loadUnitsToTableView() {
        final ObservableList<Unit> data =
                FXCollections.observableArrayList(mainController.getGameEngine().getDescriptor().getUnitMap().values());
        unitsTableView.setEditable(false);
        unitsTableView.setItems(data);
        //nameCol
        TableColumn<Unit,String> nameCol = new TableColumn<>("Name");
        nameCol.setMinWidth(70);
        //rankCol
        TableColumn<Unit,String> rankCol = new TableColumn<>("Rank");
        rankCol.setMinWidth(70);
        //priceCol
        TableColumn<Unit,Integer> priceCol = new TableColumn<>("Price");
        priceCol.setMinWidth(70);
        //firePowerCol
        TableColumn<Unit,Integer> firePowerCol = new TableColumn<>("Fire Power");
        firePowerCol.setMinWidth(100);
        //competenceReductionCol
        TableColumn<Unit,Integer> competenceReductionCol = new TableColumn<>("Competence Reduction");
        competenceReductionCol.setMinWidth(200);
        //worthCol
        TableColumn<Unit,Integer> worthCol = new TableColumn<>("Worth");
        worthCol.setMinWidth(70);
        //appearanceCol
        TableColumn<Unit,Integer> appearanceCol = new TableColumn<>();
        appearanceCol.setMinWidth(100);

        nameCol.setCellValueFactory(
                new PropertyValueFactory<>("Type")
        );
        rankCol.setCellValueFactory(
                new PropertyValueFactory<>("Rank")
        );
        priceCol.setCellValueFactory(
                new PropertyValueFactory<>("Purchase")
        );
        firePowerCol.setCellValueFactory(
                new PropertyValueFactory<>("MaxFirePower")
        );
        competenceReductionCol.setCellValueFactory(
                new PropertyValueFactory<>("CompetenceReduction")
        );
        worthCol.setCellValueFactory(
                new PropertyValueFactory<>("Worth")
        );
        appearanceCol.setCellValueFactory(
                new PropertyValueFactory<>("Appearance")
        );
        //TODO: need to update Appearance column, Problem: Units does not knows the gameManager
        unitsTableView.setItems(data);
        //noinspection unchecked
        unitsTableView.getColumns().addAll(nameCol, rankCol, priceCol,firePowerCol,competenceReductionCol,worthCol,appearanceCol);
    }

    private void addTabToPlayers(String playerName) {
        Tab tab = new Tab(playerName);
        playersInformationTabPane.getTabs().add(tab);
    }
    private void deleteTabFromPlayers(String playerName) {
        removeTab(playerName, playersInformationTabPane);
    }

    private void removeTab(String playerName, TabPane playersInformationTabPane) {
        for (int i = 0; i < playersInformationTabPane.getTabs().size(); i++) {
            String tabTitle = playersInformationTabPane.getTabs().get(i).getText();
            if (tabTitle.equals(playerName)) {
                playersInformationTabPane.getTabs().remove(i);
                break;
            }
        }
    }

    public void setMainController(AppController mainController) { this.mainController = mainController; }


}
