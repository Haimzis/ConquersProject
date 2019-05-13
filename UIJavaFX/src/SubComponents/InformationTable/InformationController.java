package SubComponents.InformationTable;

import GameEngine.GameEngine;
import GameObjects.Player;
import GameObjects.Unit;
import MainComponents.AppController;
import Resources.ResourceConstants;
import SubComponents.InformationTable.InnerTabPaneTable.InnerTabPaneRootController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.*;


public class InformationController {
    public AnchorPane InformationComponent;
    private AppController mainController;
    @FXML private Label currentRound;
    @FXML private Label totalRounds;
    @FXML private TabPane playersInformationTabPane;
    @FXML private TableView<Unit> unitsTableView;
    private Map<Player,Tab> playersTabs= new HashMap<>();
    private Map<Player, InnerTabPaneRootController> playersInnerTabPaneRootControllers= new HashMap<>();
    private Stack<String> colors = new Stack<>();

    private void initializeColors(){
        colors.push("Green");colors.push("Red");
        colors.push("Blue");colors.push("Yellow");
    }
    public void loadInformation() {
        List<Player> playersList  = mainController.getGameEngine().getDescriptor().getPlayersList();
        initializeColors();
        initializeTotalCycles();

        for (Player player : playersList) { //load each Player Information
            Tab playerTab = addTabToPlayers(player.getPlayer_name());
            playersTabs.put(player, playerTab);
            //load inner TabPane Construct into tabs
            FXMLLoader innerTabPaneRootLoader = new FXMLLoader();
            URL url = getClass().getResource(ResourceConstants.INNER_PANETAB_FXML_INCLUDE_RESOURCE);
            innerTabPaneRootLoader.setLocation(url);
            Parent innerTabPaneRoot = null;
            try {
                innerTabPaneRoot = innerTabPaneRootLoader.load(url.openStream());
                InnerTabPaneRootController innerTabPaneRootController = innerTabPaneRootLoader.getController();
                player.setColor(colors.pop());
                innerTabPaneRootController.setCurrentPlayer(player);
                innerTabPaneRootController.createDataStructure();
                innerTabPaneRootController.loadPlayerData();
                playersInnerTabPaneRootControllers.put(player, innerTabPaneRootController);
            } catch (IOException e) {
                e.printStackTrace();
            }
            playerTab.setContent(innerTabPaneRoot);
        }
        loadUnitsToTableView();
    }

    private void initializeTotalCycles() {
        totalRounds.setText(Integer.toString(mainController.getGameEngine().getDescriptor().getTotalCycles()));
    }

    public void updatePlayersData(){
        currentRound.setText(Integer.toString(GameEngine.gameManager.roundNumber));
        playersInnerTabPaneRootControllers.forEach((player,innerTabPaneRootController) -> {
            innerTabPaneRootController.loadPlayerData();
        });
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

    private Tab addTabToPlayers(String playerName) {
        Tab tab = new Tab(playerName);
        tab.setOnSelectionChanged(event -> {
            //TODO: write if - that disable the option to see territories of another player.
        });
        playersInformationTabPane.getTabs().add(tab);
        return tab;
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
