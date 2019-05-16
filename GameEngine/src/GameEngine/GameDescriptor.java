package GameEngine;
import Exceptions.invalidInputException;
import GameObjects.Player;
import GameObjects.Territory;
import GameObjects.Unit;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GameDescriptor implements Serializable {
    public static final String NO_DEFAULT_PROFIT = "No default profit detected in territories while not all territories has been declared in xml , please try again";
    public static final String NO_DEFAULT_ARMY_THRESHOLD = "No default army threshold detected in territories while not all territories has been declared in xml , please try again";
    public static final String RANKS_ARE_NOT_SEQUENTIAL = "Ranks are not sequential";
    private String lastKnownGoodString;
    private int initialFunds , totalCycles , columns , rows;
    private int defaultThreshold , defaultProfit;
    private Map<Integer,Territory> territoryMap;
    private Map<String , Unit> unitMap;
    private List<Player> playersList;
    private String error;
    //private String gameType; //relevant for the third project

    public GameDescriptor(Path xmlPath) throws invalidInputException {
        Generated.GameDescriptor descriptor = null;
        try {
            descriptor = deserializeFrom(xmlPath);
        } catch (JAXBException ignored) { }
        if(descriptor == null) // GD was not created
            throw new Exceptions.invalidInputException("Could not deserialize XML");
        if(!(checkRowsAndColumns(descriptor) && validateTerritories(descriptor) && validatePlayers(descriptor) && validateUnitsFromXml(descriptor))) //Checking the XML
            throw new Exceptions.invalidInputException(error);
        lastKnownGoodString = xmlPath.toString();
        getGameStats(descriptor);
        this.playersList =  loadPlayers(descriptor);
        this.territoryMap = buildTerritoryMap(descriptor);
        this.unitMap = loadUnitsDescription(descriptor);
    }

    //*********************//
    /*  Getters & Setters  */
    //*********************//
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

    public Territory getTerritoryByID(Integer territoryID){
        return territoryMap.get(territoryID);
    }

    //*********************//
    /*     XML Loaders    */
    //*********************//
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

    private List<Player> loadPlayers(Generated.GameDescriptor descriptor) {
        List<Player> playersList = new ArrayList<>();
        if(descriptor.getPlayers() == null) {
            Player playerOne = new Player(1 , "Ran", initialFunds);
            Player playerTwo = new Player(2 , "Haim", initialFunds);
            playersList.add(playerOne);
            playersList.add(playerTwo);
        } else {
            List<Generated.Player> players = descriptor.getPlayers().getPlayer();
            for(Generated.Player player : players) {
                int id;
                String name;

                id = player.getId().intValue();
                name = player.getName();

                Player newPlayer = new Player(id , name, initialFunds);
                playersList.add(newPlayer);
            }
        }
        return playersList;
    }


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

    private void getGameStats(Generated.GameDescriptor descriptor) {
        this.initialFunds = descriptor.getGame().getInitialFunds().intValue();
        this.totalCycles = descriptor.getGame().getTotalCycles().intValue();
        this.columns = descriptor.getGame().getBoard().getColumns().intValue();
        this.rows = descriptor.getGame().getBoard().getRows().intValue();
        //this.gameType = descriptor.getGameType();
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


    //*********************//
    /*     Validators     */
    //*********************//
    //TODO: Not good , only checks if adjacent territories in list have the same ID.
    private boolean validateTerritories(Generated.GameDescriptor descriptor) {
        for(int i = 0; i < descriptor.getGame().getTerritories().getTeritory().size() - 1 ; i++) { //Checking double ID
            if(descriptor.getGame().getTerritories().getTeritory().get(i).getId().equals(descriptor.getGame().getTerritories().getTeritory().get(i + 1).getId()))  {
                error = "Double ID in xml detected , please try again.";
                return false; // an territory exists with the same ID
            }
        }
        return validateTerritoryDefaults(descriptor);
    }
    private boolean validateTerritoryDefaults(Generated.GameDescriptor descriptor) {
        if(descriptor.getGame().getTerritories().getDefaultProfit() == null && descriptor.getGame().getTerritories().getTeritory().size() != territoryMap.size()) {
            error = NO_DEFAULT_PROFIT;
            return  false;
        }
        if(descriptor.getGame().getTerritories().getDefaultArmyThreshold() == null && descriptor.getGame().getTerritories().getTeritory().size() != territoryMap.size()) {
            error = NO_DEFAULT_ARMY_THRESHOLD;
            return  false;
        }
        return true;
    }

    private boolean validateUnitsFromXml(Generated.GameDescriptor descriptor) {
        //Check for duplicate types or ranks , EXAMPLE: If the type of the set is 2 and the size of the unit list is 3 there are duplicate types.
        Set<String> unitsTypeSet = new HashSet<>();
        Set<Byte> unitsRankSet = new HashSet<>();
        for(int i = 0 ; i < descriptor.getGame().getArmy().getUnit().size() ; i++) {
            unitsTypeSet.add(descriptor.getGame().getArmy().getUnit().get(i).getType());
            unitsRankSet.add(descriptor.getGame().getArmy().getUnit().get(i).getRank());
        }

        //Checking if ranks are incremented well in XML.
        List<Byte> sortedRanks = new ArrayList<>(unitsRankSet);
        Collections.sort(sortedRanks);
        for(int i = 0 ; i < sortedRanks.size() - 1 ; i++) {
            if(sortedRanks.get(i).intValue()+1 != (sortedRanks.get(i+1).intValue())) {
                error = RANKS_ARE_NOT_SEQUENTIAL;
                return false;
            }
        }
       if(unitsTypeSet.size() == descriptor.getGame().getArmy().getUnit().size()) {
           if(unitsRankSet.size() == descriptor.getGame().getArmy().getUnit().size()) {
               return true;
           }
           else {
               error = "A unit exists with the same rank in XML";
           }
        }
       else {
           error = "A Unit with the same type exists in the XML";
       }
        return  false;
    }



    private boolean validatePlayers(Generated.GameDescriptor descriptor) {
        Set<Integer> playerIdsSet = new HashSet<>();
        if(descriptor.getPlayers() == null)
            return true;
        if(descriptor.getPlayers().getPlayer().size() < 2 || descriptor.getPlayers().getPlayer().size() > 4) {
            error = "Not a valid amount of players in XML";
            return false;
        }
        for(int i = 0; i < descriptor.getPlayers().getPlayer().size(); i++) {
            playerIdsSet.add(descriptor.getPlayers().getPlayer().get(i).getId().intValue());
        }
        if(playerIdsSet.size() == descriptor.getPlayers().getPlayer().size()) {
            return true;
        }
        else {
            error = "A player exists with the same ID in XML";
            return false;
        }
    }

    private boolean checkRowsAndColumns(Generated.GameDescriptor descriptor) {
        if (descriptor.getGame().getBoard().getColumns().intValue() >= 3 && descriptor.getGame().getBoard().getColumns().intValue() <= 30) {
            if((descriptor.getGame().getBoard().getRows().intValue() <= 30 && descriptor.getGame().getBoard().getRows().intValue() >= 2)) {
                return true;
            }
            else {
                error = "Rows in XML are invalid";
            }
        }
        else {
            error = "Columns in XML are invalid";
        }
        return false;
    }

    //TODO: Gets the last known good xml path , crashes if player moves the last know xml from it's former location.
    // Find a better way to implement.
    public String getLastKnownGoodString() {
        return lastKnownGoodString;
    }
}
