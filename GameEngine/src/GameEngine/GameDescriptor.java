package GameEngine;
import GameObjects.Player;
import GameObjects.Territory;
import GameObjects.Unit;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.nio.file.Path;
import java.util.*;

public class GameDescriptor implements Serializable {
    private int initialFunds , totalCycles , columns , rows;
    private int defaultThreshold , defaultProfit;
    private Map<Integer,Territory> territoryMap;
    private Map<String , Unit> unitMap;
    private List<Player> playersList;
    private String gameType; //relevant for the second project
    private String lastKnownGoodString;

    public GameDescriptor(Path xmlPath) {
        Generated.GameDescriptor descriptor = null;
        try {
            descriptor = deserializeFrom(xmlPath);
        } catch (JAXBException ignored) { }

        if(descriptor == null) // GD was not created
            throw new IllegalArgumentException();
        lastKnownGoodString = xmlPath.toString();
        getGameStats(descriptor);
        this.territoryMap = buildTerritoryMap(descriptor);
        if(checkRowsAndColumns() && validateTerritories(descriptor)){ //Checking the XML input (Board and territory ID's)
            this.unitMap = loadUnitsDescription(descriptor);
            //this.playersList =  //For dynamic players
            this.playersList = addPlayers();
        }
        else { //XML is not valid after further inspection
            throw new IllegalArgumentException();
        }
    }

    //*********************//
    /*  Getters & Setters  */
    //*********************//
    public String getLastKnownGoodString() {
        return lastKnownGoodString;
    }
    public int getTotalCycles() {
        return totalCycles;
    }
    public int getColumns() {
        return columns;
    }
    public int getRows() {
        return rows;
    }
    public Map<Integer, Territory> getTerritoryMap() { return territoryMap; }
    public Map<String,Unit> getUnitMap() {
        return unitMap;
    }
    public List<Player> getPlayersList() { return playersList; }
    public void setTerritoryMap(Map<Integer, Territory> territoryMap) {
        this.territoryMap = territoryMap;
    }
    public void setPlayersList(List<Player> playersList) {
        this.playersList = playersList;
    }


    private boolean checkRowsAndColumns() {
        return (columns >= 3 && columns <= 30) && (rows <= 30 && rows >= 2);
    }
    private List<Player> addPlayers() {
        List<Player> players = new ArrayList<>();
        Player playerOne = new Player(1 , "Ran", initialFunds);
        Player playerTwo = new Player(2 , "Haim", initialFunds);
        players.add(playerOne);
        players.add(playerTwo);
        return players;
    }
    // Here we create a list of all the units available to purchase in-game , when the game engine
    //loads the game it shall create the list , and here we can access all the information on each unit
    // by its type.
    private Map<String , Unit> loadUnitsDescription(Generated.GameDescriptor descriptor) {
        Map<String , Unit> unitsMap = new HashMap<>();
        List<Generated.Unit> units = descriptor.getGame().getArmy().getUnit();
        for(Generated.Unit unit : units) {
            String type;
            int purchaseCost , maxFire , compReduction , rank;

            type = unit.getType();
            purchaseCost = unit.getPurchase().intValue();
            maxFire = unit.getMaxFirePower().intValue();
            compReduction = unit.getCompetenceReduction().intValue();
            rank = unit.getRank();

            Unit newUnit = new Unit(type,rank , purchaseCost , maxFire, compReduction);
            unitsMap.put(type , newUnit);
        }
        return unitsMap;
    }
    //Will be used for further projects
    public List<Player> loadPlayers(Generated.GameDescriptor descriptor) {
        List<Player> playersList = new ArrayList<>();
        List<Generated.Player> players = descriptor.getPlayers().getPlayer();
        for(Generated.Player player : players) {
            int id;
            String name;

            id = player.getId().intValue();
            name = player.getName();

            Player newPlayer = new Player(id , name, initialFunds);
            playersList.add(newPlayer);
        }
        return playersList;
    }
    //Build the territory map from the defined XML document.
    private Map<Integer,Territory> buildTerritoryMap(Generated.GameDescriptor descriptor) {
        List<Generated.Teritory> territoryList = loadTerritories(descriptor);
        Map<Integer, Territory> territoriesMap = new HashMap<>();
        if(territoryList != null) {
            for(int i = 1; i <= columns * rows ; i++) {
               for(int j = 0 ; j < territoryList.size() ; j++) {
                   if(territoryList.get(j).getId().intValue() == i) {
                       createTerritoryToMap(j , territoriesMap , i , territoryList);
                       break;
                   }
                   else
                       createTerritoryToMapFromDefault(territoriesMap, i);
               }
            }
        }
        return territoriesMap;
    }
    //From default values(if exists)
    private void createTerritoryToMapFromDefault(Map<Integer,Territory> territoriesMap, int i) {
        Territory newTerritory = new Territory(i , defaultProfit , defaultThreshold);
        territoriesMap.put(newTerritory.getID() , newTerritory);
    }
    //From defined values(if exists)
    private void createTerritoryToMap(int j
            , Map<Integer, Territory> territoriesMap, int i
            , List<Generated.Teritory> territoryList) {
        int profit;
        int armyThreshold;
        profit = territoryList.get(j).getProfit().intValue();
        armyThreshold = territoryList.get(j).getArmyThreshold().intValue();
        Territory newTerritory = new Territory(i , profit , armyThreshold);
        territoriesMap.put(newTerritory.getID() , newTerritory);
    }
    private List<Generated.Teritory> loadTerritories(Generated.GameDescriptor descriptor) {
        return descriptor.getGame().getTerritories().getTeritory();
    }
    //Load game stats
    private void getGameStats(Generated.GameDescriptor descriptor) {
        this.initialFunds = descriptor.getGame().getInitialFunds().intValue();
        this.totalCycles = descriptor.getGame().getTotalCycles().intValue();
        this.columns = descriptor.getGame().getBoard().getColumns().intValue();
        this.rows = descriptor.getGame().getBoard().getRows().intValue();
        this.gameType = descriptor.getGameType();
        if(descriptor.getGame().getTerritories().getDefaultArmyThreshold() != null) {
            this.defaultProfit = descriptor.getGame().getTerritories().getDefaultProfit().intValue();
        }
        if(descriptor.getGame().getTerritories().getDefaultArmyThreshold() != null) {
            this.defaultThreshold = descriptor.getGame().getTerritories().getDefaultArmyThreshold().intValue();
        }
    }
    private static Generated.GameDescriptor deserializeFrom(Path path) throws JAXBException {
        File file = new File(path.toString());
        JAXBContext jc = JAXBContext.newInstance(Generated.GameDescriptor.class);
        Unmarshaller u = jc.createUnmarshaller();
        return (Generated.GameDescriptor) u.unmarshal(file);
    }
    //Valid territories from XML
    private boolean validateTerritories(Generated.GameDescriptor descriptor) {
        for(int i = 0; i < descriptor.getGame().getTerritories().getTeritory().size() - 1 ; i++) { //Checking double ID
            if(descriptor.getGame().getTerritories().getTeritory().get(i).getId().equals(descriptor.getGame().getTerritories().getTeritory().get(i + 1).getId()))  {
                System.out.println("Double ID in xml detected , please try again.");
                return false; // an territory exists with the same ID
            }
        }
        return validateTerritoryDefaults(descriptor);
    }
    private boolean validateTerritoryDefaults(Generated.GameDescriptor descriptor) {
        if(descriptor.getGame().getTerritories().getDefaultProfit() == null && descriptor.getGame().getTerritories().getTeritory().size() != territoryMap.size()) {
            System.out.println("No default profit detected in territories while not all territories has been declared in xml , please try again");
            return  false;
        }
        if(descriptor.getGame().getTerritories().getDefaultArmyThreshold() == null && descriptor.getGame().getTerritories().getTeritory().size() != territoryMap.size()) {
            System.out.println("No default army threshold detected in territories while not all territories has been declared in xml , please try again");
            return  false;
        }
        return true;
    }

}
