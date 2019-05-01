package GameEngine;
import GameObjects.*;
import History.*;
import com.sun.istack.internal.Nullable;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class GameManager implements Serializable {
    private int ID;
    public  int roundNumber=1;
    private static int gamesIDCounter = 0;
    private Stack<RoundHistory> roundsHistory;
    private GameDescriptor gameDescriptor;
    private Player currentPlayerTurn=null;
    private Army   selectedArmyForce=null;
    private Queue<Player> playersTurns;
    private Territory selectedTerritoryByPlayer=null;

    public GameManager(GameDescriptor gameDes) {
        ID = ++gamesIDCounter;
        gameDescriptor = gameDes;
        playersTurns = new ArrayBlockingQueue<>(gameDescriptor.getPlayersList().size());
        loadPlayersIntoQueueOfTurns();
        roundsHistory = new Stack<>();
        roundsHistory.push(new RoundHistory(gameDescriptor,roundNumber));
    }

    public GameDescriptor getGameDescriptor() {
        return gameDescriptor;
    }

    //**************************//
    /*  Player Choices Control  */
    //**************************//

    //retrieve player object from UI, and put it as current player turn for all functions.
    public void setSelectedTerritoryForTurn(Territory selectedTerritory) {
        selectedTerritoryByPlayer = selectedTerritory;
    }
    //retrieve amount of required units, create them in selectedArmyForce,and decrement the price from player
    public void buyUnits(Unit unit, int amount) {
        int unitPrice;
        selectedArmyForce = new Army();
        for(int i=0;i<amount;i++) {
            Unit unitToAdd = new Unit(unit.getType()
                    , unit.getRank()
                    , unit.getPurchase()
                    , unit.getMaxFirePower()
                    , unit.getCompetenceReduction());
            selectedArmyForce.addUnit(unitToAdd);
        }
        unitPrice= unit.getPurchase()*amount;
        currentPlayerTurn.decrementFunds(unitPrice);
    }
    //retrieve selectedArmyForce to the selectedTerritory
    public void transformSelectedArmyForceToSelectedTerritory() {
        selectedTerritoryByPlayer.getConquerArmyForce().uniteArmies(selectedArmyForce);
    }
    //retrieve selectedArmyForce Collection to the selectedTerritory
    public void buyUnitsCollection(Collection<Unit> unitList) {
        selectedArmyForce.getUnits().addAll(unitList);
    }
    //Rehabilitation of selected territory army
    public void rehabilitateSelectedTerritoryArmy(){
        selectedTerritoryByPlayer.rehabilitateConquerArmy();
    }
    //Rehabilitation of all army
    public void rehabilitateAllArmy() {
        getCurrentPlayerTerritories().parallelStream()
            .forEach(Territory::rehabilitateConquerArmy);
    }

    //**************************//
    /*     Rounds Management    */
    //**************************//
    public void startOfRoundUpdates() {
        updateAllPlayersProductionStartOfRound();
        updateAllPlayerTerritoriesHold();
    }
    private void updateAllPlayerTerritoriesHold() {
        gameDescriptor.getPlayersList().forEach(this::updateTerritoriesHold);
    }
    private void updateAllPlayersProductionStartOfRound(){
        gameDescriptor.getPlayersList().forEach(this::harvestProduction);
    }
    //Load players list into empty queue of turns
    private void updateTurnsObjectQueue() {
        while(!playersTurns.isEmpty())
            playersTurns.poll();
        loadPlayersIntoQueueOfTurns();
    }
    //load information of previous turn, after undo use
    private void updateGameDescriptorAfterUndo() {
        roundNumber = roundsHistory.peek().getRoundNumber();
        gameDescriptor.setTerritoryMap(roundsHistory.peek().getCopyOfMap());
        gameDescriptor.setPlayersList(roundsHistory.peek().getCopyOfPlayersList());
        updateTurnsObjectQueue();
    }
    //retrieve all maps from rounds history by there right order from begging
    public Stack<Map<Integer, Territory>> getMapsHistoryByOrder() {
        Stack<Map<Integer,Territory>> mapsHistoryByOrder = new Stack<>();
        roundsHistory.forEach(roundHistory -> mapsHistoryByOrder.push(roundHistory.getMapHistory()));
        return mapsHistoryByOrder;
    }
    //retrieve players list into queue of turns
    public void loadPlayersIntoQueueOfTurns() {
        if(gameDescriptor.getPlayersList() != null) {
            playersTurns.addAll(gameDescriptor.getPlayersList());
        }
        else
            System.out.println("NULL");
    }
    //poll player from the queue and save him as current player for one turn
    public void nextPlayerInTurn() {
        currentPlayerTurn= playersTurns.poll();
    }
    //returns potential production of the current player turn
    //gives current player his funds, for his territories profits
    private void harvestProduction(Player player) {
        player.incrementFunds(calculatePotentialProduction(player));
    }
    public int calculatePotentialProduction(Player player) {
        return Optional.ofNullable(getTerritories(player))
                .orElse(Collections.emptyList())
                .stream()
                .mapToInt(Territory::getProfit).sum();
    }
    //reduce competence of current player units and clean off the dead units
    public void updateTerritoriesHold(Player player) {
        getTerritories(player).stream()
                .forEach(Territory::reduceCompetence);

        getTerritories(player).stream()
                .filter(Territory::isArmyTotalPowerUnderThreshold)
                .forEach(Territory::eliminateThisWeakArmy);
    }
    //pop the last round history from the history stack, and call updates function
    public void roundUndo() {
        roundsHistory.pop();
        updateGameDescriptorAfterUndo();
    }
    //load history of round into the stack,update the queue of players turns
    public void endOfRoundUpdates() {
        roundsHistory.push(new RoundHistory(gameDescriptor,++roundNumber));
        loadPlayersIntoQueueOfTurns();
    }

    //**************************//
    /* War and Conquest Control*/
    //**************************//

    //Returns True: if attacking player wins, Else :False. anyway its update stats of the GameObjects.Territory after battle.
    public boolean attackConqueredTerritory() {
        boolean succeed;
        Battle.preparedToBattle(selectedTerritoryByPlayer.getConquerArmyForce(),selectedArmyForce,selectedTerritoryByPlayer);
        succeed = Battle.isAttackSucceed();
        if(succeed) {
            Battle.updateArmiesAfterAttackerVictory();
            selectedTerritoryByPlayer.setConquer(currentPlayerTurn);
            currentPlayerTurn.addTerritory(selectedTerritoryByPlayer);
        }
        else {//(!succeed)
            Battle.updateArmiesAfterAttackerDefeat();
            selectedArmyForce = null;
        }
        if(Battle.isWinnerArmyNotStrongEnoughToHoldTerritory())
            selectedTerritoryByPlayer.xChangeFundsForUnitsAndHold();
        return succeed;
    }
    //Returns True: if attacking player conquered the territory, Else: False. anyway its update stats of GameObjects.Territory.
    public boolean conquerNeutralTerritory() {
        if (isSelectedArmyForceBigEnough()) {
            currentPlayerTurn.addTerritory(selectedTerritoryByPlayer);
            selectedTerritoryByPlayer.setConquerArmyForce(selectedArmyForce);
            selectedTerritoryByPlayer.setConquer(currentPlayerTurn);
        }
        return isSelectedArmyForceBigEnough();
    }

    //**************************//
    /*      Get Information     */
    //**************************//

    public int getFundsBeforeProduction() {
        return roundsHistory.peek().getPlayerStatsHistory().get(currentPlayerTurn.getID()-1).getFunds();
    }
    //territories Getters
    public List<Territory> getCurrentPlayerTerritories() {
        return getTerritories(currentPlayerTurn);
    }
    private List<Territory> getTerritories(Player player) {
        List <Territory> playerTerritories=null;
        if(player.getTerritoriesID() != null) {
            playerTerritories= player.getTerritoriesID().stream()
                    .mapToInt(Integer::intValue)
                    .mapToObj(this::getTerritoryByID)
                    .collect(Collectors.toList());
        }
        return playerTerritories;
    }
    private Territory getTerritoryByID(int TerritoryID)
    {
        return gameDescriptor.getTerritoryMap().get(TerritoryID);
    }
    public int getCurrentPlayerTerritoriesAmount() {
        if(currentPlayerTurn.getTerritoriesID() == null)
            return 0;
        return currentPlayerTurn.getTerritoriesID().size();
    }

    @Nullable//returns the winner - player with most profits from territories
    public Player getWinnerPlayer() {
        int winnerPlayerID=1,maxScore =0;
        int size =gameDescriptor.getPlayersList().size();
        int [] playerScores= new int [size+1];

        for (int i=0; i<= size;i++) playerScores[i] = 0;

        //make scores array
        for(Player player: gameDescriptor.getPlayersList()) {
            for(Integer territoryID:player.getTerritoriesID()) {
                playerScores[player.getID()]+= gameDescriptor.getTerritoryMap().get(territoryID).getProfit();
            }
        }
        //checks for max score and winner id
        for (int i=1;i<= size ; i++) {

            if(playerScores[i] >= maxScore) {
                maxScore = playerScores[i];
                winnerPlayerID = i;
            }
        }
        //checks for draw.
        for(int i=1;i<= size;i++) {
            if(playerScores[i] == maxScore && winnerPlayerID != i)
                return null; //there are at least 2 winners - draw!
        }
        return gameDescriptor.getPlayersList().get(winnerPlayerID-1);
    }
    //get price of rehabilitation Army in territory
    public int getRehabilitationArmyPriceInTerritory(Territory territory){
        return territory.getRehabilitationArmyPriceInTerritory();
    }
    //checks if undo is valid
    public boolean isUndoPossible()
    {
        return (roundsHistory.size() > 1);
    }
    //Returns True: if target territory is 1 block away from his territories.Else false
    private boolean isTargetTerritoryOneBlockAway() {
        for(Territory territory:getCurrentPlayerTerritories()) {
            if(Math.abs(territory.getID()-selectedTerritoryByPlayer.getID()) == 1) {
                int minID = Math.min(territory.getID(),selectedTerritoryByPlayer.getID());
                int maxID = Math.max(territory.getID(),selectedTerritoryByPlayer.getID());
                if(maxID % gameDescriptor.getColumns() == 0)
                    return true;
                if((minID % gameDescriptor.getColumns() != 0 )
                        && (minID / gameDescriptor.getColumns() == maxID / gameDescriptor.getColumns())) {
                    return true;
                }
            }
            else if(Math.abs(territory.getID()-selectedTerritoryByPlayer.getID()) == gameDescriptor.getColumns())
                return true;
        }
        return false;
    }
    //Returns True: if target territory is valid
    public boolean isTargetTerritoryValid() {
        return isTargetTerritoryOneBlockAway();
    }
    //Returns True: if yes. Else False.
    private boolean isSelectedArmyForceBigEnough() {
         return selectedArmyForce.getTotalPower() >= selectedTerritoryByPlayer.getArmyThreshold();
    }
    //Checks if current player is the conquer of this GameObjects.Territory.
    public boolean isTerritoryBelongsCurrentPlayer() {
        if(selectedTerritoryByPlayer.getConquer() == null)
            return false;
        return selectedTerritoryByPlayer.getConquer().equals(currentPlayerTurn);
    }
    //Returns True if selected territory is conquered. Else False
    public boolean isConquered() {
        return selectedTerritoryByPlayer.isConquered();
    }
    //Returns True if next player exist in this round
    public boolean isCycleOver(){
        return playersTurns.isEmpty();
    }
    //Returns True if Game Over - final Round is over
    public boolean isGameOver() {
        return gameDescriptor.getTotalCycles() < roundNumber;
    }
    //Returns currentPlayer funds amount
    public int getCurrentPlayerFunds(){return currentPlayerTurn.getFunds();}
    //returns gameManager id - relevant for the second project
    public int getGameManagerID() {
        return ID;
    }
    //get supplier and returns if the current funds are enough
    public boolean isSelectedPlayerHasEnoughMoney(Supplier <Integer> amountOfMoney) {
        return amountOfMoney.get() <= getCurrentPlayerFunds();
    }
    //current player getter
    public Player getCurrentPlayerTurn() {
        return currentPlayerTurn;
    }

}
