package GameEngine;
import Exceptions.invalidInputException;
import GameObjects.Player;
import GameObjects.Territory;
import GameObjects.Unit;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.*;

public class GameDescriptor implements Serializable {
    //TODO: We need to enter players into the player list when the game is full and has started.
    private static final String NO_DEFAULT_PROFIT = "No default profit detected";
    private static final String NO_DEFAULT_ARMY_THRESHOLD = "No default army threshold detected";
    private static final String RANKS_ARE_NOT_SEQUENTIAL = "Ranks in XML are not sequential";
    private static final String THE_SAME_RANK_IN_XML = "A unit exists with the same rank in XML";
    private static final String SAME_TYPE_EXISTS_IN_THE_XML = "A Unit with the same type exists in the XML";
    private static final String DYNAMIC_MULTI_PLAYER = "DynamicMultiPlayer";
    private String lastKnownGoodString;
    private int initialFunds , totalCycles , columns , rows;
    private int defaultThreshold , defaultProfit;
    private Map<Integer,Territory> territoryMap;
    private Map<String , Unit> unitMap;
    private List<Player> playersList;
    private int maxPlayers;


    public int getInitialFunds() {
        return initialFunds;
    }

    public void insertNewPlayer(Player newPlayer) {
        playersList.add(newPlayer);
    }

    private String error;
    private String gameType;
    private String gameTitle;

    public GameDescriptor(Path xmlPath) throws invalidInputException {
        Generated.GameDescriptor descriptor = null;
        try {
            descriptor = deserializeFrom(xmlPath);
        } catch (JAXBException ignored) { }
        if(descriptor == null) // GD was not created
            throw new Exceptions.invalidInputException("Could not deserialize XML");
        getGameStats(descriptor);
        this.territoryMap = buildTerritoryMap(descriptor);
        if(!gameType.equals(DYNAMIC_MULTI_PLAYER)) {
            this.playersList =  loadPlayers(descriptor);
        }
        this.unitMap = loadUnitsDescription(descriptor);
        if(!(checkRowsAndColumns(descriptor)
                && validateTerritories(descriptor)
                && validateDynamicMultiPlayer(descriptor)
                && validatePlayers(descriptor)
                && validateUnitsFromXml(descriptor))) //Checking the XML
            throw new Exceptions.invalidInputException(error);
        lastKnownGoodString = xmlPath.toString();
    }

    public GameDescriptor(InputStream xmlPath) throws invalidInputException {
        Generated.GameDescriptor descriptor = null;
        try {
            descriptor = deserializeFrom(xmlPath);
        } catch (JAXBException ignored) { }
        if(descriptor == null) // GD was not created
            throw new Exceptions.invalidInputException("Could not deserialize XML");
        getGameStats(descriptor);
        this.territoryMap = buildTerritoryMap(descriptor);
        if(!gameType.equals(DYNAMIC_MULTI_PLAYER)) {
            this.playersList =  loadPlayers(descriptor);
        }
        this.unitMap = loadUnitsDescription(descriptor);
        if(!(checkRowsAndColumns(descriptor)
                && validateTerritories(descriptor)
                && validateDynamicMultiPlayer(descriptor)
                && validatePlayers(descriptor)
                && validateUnitsFromXml(descriptor))) //Checking the XML
            throw new Exceptions.invalidInputException(error);
        lastKnownGoodString = xmlPath.toString();
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
    public String getGameTitle() { return gameTitle; }

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
        Stack<String> colors = new Stack<>();
        colors.push("Green");colors.push("Red");
        colors.push("Blue");colors.push("Yellow");

        List<Player> playersList = new ArrayList<>();
        if(descriptor.getPlayers() == null) {
            Player playerOne = new Player(1 , "Ran", initialFunds,"Blue");
            Player playerTwo = new Player(2 , "Haim", initialFunds,"Red");
            playersList.add(playerOne);
            playersList.add(playerTwo);
        } else {
            List<Generated.Player> players = descriptor.getPlayers().getPlayer();
            for(Generated.Player player : players) {
                int id;
                String name;

                id = player.getId().intValue();
                name = player.getName();

                Player newPlayer = new Player(id , name, initialFunds,colors.pop());
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
        this.gameType = descriptor.getGameType();
        if(descriptor.getGame().getTerritories().getDefaultProfit() != null) {
            this.defaultProfit = descriptor.getGame().getTerritories().getDefaultProfit().intValue();
        }
        if(descriptor.getGame().getTerritories().getDefaultArmyThreshold() != null) {
            this.defaultThreshold = descriptor.getGame().getTerritories().getDefaultArmyThreshold().intValue();
        }
        if(gameType.equals(DYNAMIC_MULTI_PLAYER)) {
            gameTitle = descriptor.getDynamicPlayers().getGameTitle();
            this.playersList  = new ArrayList<>();
            this.maxPlayers = descriptor.getDynamicPlayers().getTotalPlayers().intValue();
        }
    }
    private static Generated.GameDescriptor deserializeFrom(Path path) throws JAXBException {
        File file = new File(path.toString());
        JAXBContext jc = JAXBContext.newInstance(Generated.GameDescriptor.class);
        Unmarshaller u = jc.createUnmarshaller();
        return (Generated.GameDescriptor) u.unmarshal(file);
    }

    private static Generated.GameDescriptor deserializeFrom(InputStream path) throws JAXBException { ;
        JAXBContext jc = JAXBContext.newInstance(Generated.GameDescriptor.class);
        Unmarshaller u = jc.createUnmarshaller();
        return (Generated.GameDescriptor) u.unmarshal(path);
    }


    //*********************//
    /*     Validators     */
    //*********************//
    private boolean validateTerritories(Generated.GameDescriptor descriptor) {
        Set<Integer> territoryIds = new HashSet<>();
        for(int i = 0; i < descriptor.getGame().getTerritories().getTeritory().size(); i++) { //Checking double ID
            territoryIds.add(descriptor.getGame().getTerritories().getTeritory().get(i).getId().intValue());
        }
        if(territoryIds.size() != descriptor.getGame().getTerritories().getTeritory().size()) {
            error = "Double territory ID in xml detected , please try again.";
            return false;
        }
        for(Generated.Teritory territory: descriptor.getGame().getTerritories().getTeritory()) {
            if(territory.getId().intValue() > columns * rows) {
                error = "Defined territory ID's exceed board limit in XML.";
                return false;
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

    private boolean validateDynamicMultiPlayer(Generated.GameDescriptor descriptor) {
        if(gameType.equals(DYNAMIC_MULTI_PLAYER)) {
            if(descriptor.getDynamicPlayers().getGameTitle() == null) {
                error = "Please enter a game title";
                return false;
            }
            if(GameEngine.getGameManagers().entrySet().stream().anyMatch(integerGameManagerEntry -> integerGameManagerEntry.getValue().getGameTitle().equals(gameTitle))) {
                error = gameTitle + " is already taken, please choose a different title";
                return false;
            }
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
               error = THE_SAME_RANK_IN_XML;
           }
        }
       else {
           error = SAME_TYPE_EXISTS_IN_THE_XML;
       }
        return  false;
    }



    private boolean validatePlayers(Generated.GameDescriptor descriptor) {
        if(!gameType.equals(DYNAMIC_MULTI_PLAYER)) {
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
        } else {
            if(descriptor.getDynamicPlayers().getTotalPlayers().intValue() < 2 || descriptor.getDynamicPlayers().getTotalPlayers().intValue() > 4) {
                error = "Invalid player amount";
                return false;
            }
        }
        return true;
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
    public String getLastKnownGoodString() {
        return lastKnownGoodString;
    }

    public int getCurrentPlayersInGame(){
        return this.playersList.size();
    }
    public int getMaxPlayers() {
        return maxPlayers;
    }
    public int getDefaultThreshold() {
        return defaultThreshold;
    }

    public int getDefaultProfit() {
        return defaultProfit;
    }
}
