package SubComponents.InformationTable;

import GameEngine.GameEngine;
import GameObjects.Player;
import GameObjects.Unit;
import MainComponents.AppController;
import Resources.ResourceConstants;
import SubComponents.InformationTable.InnerTabPaneTable.InnerTabPaneRootController;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
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
    private AppController mainController;
    @FXML public AnchorPane InformationComponent;
    @FXML private Label lblCurrentRound;
    @FXML private Label lblTotalRounds;
    @FXML private TabPane tpPlayersInformation;
    @FXML private TableView<Unit> tvUnits;
    private Map<Integer,Tab> playersTabs= new HashMap<>();
    private Map<Integer, InnerTabPaneRootController> playersInnerTabPaneRootControllers= new HashMap<>();
    private IntegerProperty currentRoundProperty;

    public String getColorByPlayerID(Integer playerID){
        return  playersInnerTabPaneRootControllers.get(playerID).getCurrentPlayerColor();
    }

    public void undoUpdate(){
        loadRoundHistory();
        decCurrentRoundProperty();
    }

    public void incCurrentRoundProperty(){
        currentRoundProperty.setValue(currentRoundProperty.getValue() + 1);
    }
    public void decCurrentRoundProperty(){
        currentRoundProperty.setValue(currentRoundProperty.getValue() - 1);
    }
    public void loadInformation() {
        resetPlayerTabs();
        loadTotalCycles();
        loadBinding();
        loadPlayersTabs();
        loadUnitsToTableView();
    }
    private void resetPlayerTabs(){
        tpPlayersInformation.getTabs().clear();
        playersTabs = new HashMap<>();
        playersInnerTabPaneRootControllers=new HashMap<>();
    }
    public void loadBinding() {
        currentRoundProperty = new SimpleIntegerProperty(mainController.getCurrentGameManager().roundNumber);
        StringExpression currentRoundSE = Bindings.concat(currentRoundProperty);
        lblCurrentRound.textProperty().bind(currentRoundSE);
    }

    private void loadPlayersTabs(){
        for (Player player : mainController.getCurrentGameManager().getGameDescriptor().getPlayersList()) { //load each Player Information
            Tab playerTab = addTabToPlayers(player.getPlayerName());
            playersTabs.put(player.getID(), playerTab);

            //load inner TabPane Construct into tabs
            FXMLLoader innerTabPaneRootLoader = new FXMLLoader();
            URL url = getClass().getResource(ResourceConstants.INNER_PANETAB_FXML_INCLUDE_RESOURCE);
            innerTabPaneRootLoader.setLocation(url);
            Parent innerTabPaneRoot = null;
            try {
                innerTabPaneRoot = innerTabPaneRootLoader.load(url.openStream());
                InnerTabPaneRootController innerTabPaneRootController = innerTabPaneRootLoader.getController();
                innerTabPaneRootController.setInformationController(this);
                innerTabPaneRootController.setCurrentPlayer(player);
                innerTabPaneRootController.loadTerritoriesToTableView();
                innerTabPaneRootController.loadPlayerData();
                playersInnerTabPaneRootControllers.put(player.getID(), innerTabPaneRootController);
            } catch (IOException e) {
                e.printStackTrace();
            }
            playerTab.setContent(innerTabPaneRoot);
        }
    }
    private void loadTotalCycles() {
        lblTotalRounds.setText(Integer.toString(mainController.getCurrentGameManager().getGameDescriptor().getTotalCycles()));
    }

    public void updatePlayersData(){
        playersInnerTabPaneRootControllers.forEach((player,innerTabPaneRootController) -> innerTabPaneRootController.loadPlayerData());
        tvUnits.refresh();
    }
    private void loadUnitsToTableView() {
        tvUnits.getColumns().clear();
        final ObservableList<Unit> data =
                FXCollections.observableArrayList(mainController.getCurrentGameManager().getGameDescriptor().getUnitMap().values());
        tvUnits.setEditable(false);
        tvUnits.setItems(data);
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
        TableColumn<Unit,Integer> appearanceCol = new TableColumn<>("Appearance");
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
        tvUnits.setItems(data);
        //noinspection unchecked
        tvUnits.getColumns().addAll(nameCol, rankCol, priceCol,firePowerCol,competenceReductionCol,worthCol,appearanceCol);
    }

    private Tab addTabToPlayers(String playerName) {
        Tab tab = new Tab(playerName);
        tpPlayersInformation.getTabs().add(tab);
        return tab;
    }

    public void setMainController(AppController mainController) { this.mainController = mainController; }
    public AppController getMainController() {
        return mainController;
    }
    public void setFocusOnCurrentPlayer() {
        for(int i = 0; i < tpPlayersInformation.getTabs().size(); i++) {
            if(tpPlayersInformation.getTabs().get(i).getText().equals(mainController.getCurrentGameManager().getCurrentPlayerName())) {
                tpPlayersInformation.getSelectionModel().select(tpPlayersInformation.getTabs().get(i));
            }
        }
    }

    public void loadRoundHistory() {
        playersInnerTabPaneRootControllers.forEach((player, playerInnerTabPaneRootControllers) -> {
            playerInnerTabPaneRootControllers.setCurrentPlayer(mainController.getCurrentGameManager().getPlayerByID(player));
            playerInnerTabPaneRootControllers.loadPlayerData();
        });
        tvUnits.refresh();
    }
}
