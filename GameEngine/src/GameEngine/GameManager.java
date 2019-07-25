package GameEngine;
import Events.*;
import Events.EventListener;
import GameObjects.*;
import History.*;
import com.sun.istack.internal.Nullable;

import java.io.Serializable;
import java.util.*;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class GameManager implements Serializable {
    private String currentPlayerName = "None";
    private int ID;
    private int roundNumber=1;
    private String gameTitle;
    private static int gamesIDCounter = 0;
    private Stack<RoundHistory> roundsHistory;
    private GameDescriptor originalGameDescriptor;
    private GameDescriptor gameDescriptor;
    private Player currentPlayerTurn=null;
    private Army   selectedArmyForce=null;
    private String winnerName = "";
    private GameStatus status;
    private transient EventListener eventListener;
    private Queue<Player> playersTurns;
    private Territory selectedTerritoryByPlayer=null;
    private Stack<RoundHistory> replayStack = new Stack<>();  //Bonus #2

    public void resetManager() {
        this.playersTurns = new ArrayBlockingQueue<>(gameDescriptor.getMaxPlayers());
        this.roundsHistory = new Stack<>();
        this.roundsHistory.push(new RoundHistory(gameDescriptor,roundNumber));
        this.status = GameStatus.WaitingForPlayers;
        this.gameDescriptor = new GameDescriptor(originalGameDescriptor);
        this.roundNumber = 1;
        this.currentPlayerTurn=null;
    }

    public GameManager(GameDescriptor gameDes) {
        ID = ++gamesIDCounter;
        gameDescriptor = gameDes;
        playersTurns = new ArrayBlockingQueue<>(gameDescriptor.getMaxPlayers());
        if(!gameDes.getGameType().equals(GameDescriptor.DYNAMIC_MULTI_PLAYER)) {
            loadPlayersIntoQueueOfTurns();
        }
        roundsHistory = new Stack<>();
        roundsHistory.push(new RoundHistory(gameDescriptor,roundNumber));
        gameTitle = gameDescriptor.getGameTitle();
        status = GameStatus.WaitingForPlayers;
        originalGameDescriptor = new GameDescriptor(gameDes);
        eventListener = new EventListener();
    }

    public EventListener getEventListener() {
        return eventListener;
    }

    public void setEventListenerHandler(EventHandler eventHandler) {
        eventListener = new EventListener();
        this.eventListener.setEventsHandler(eventHandler);
    }



    //**************************//
    /*  Player Choices Control  */
    //**************************//

    //retrieve player object from UI, and put it as current player turn for all functions.
    public void setSelectedTerritoryForTurn(Territory selectedTerritory) {
        selectedTerritoryByPlayer = selectedTerritory;
    }
    public Territory getSelectedTerritoryByPlayer() { return selectedTerritoryByPlayer; }
    //retrieve amount of required units, create them in selectedArmyForce,and decrement the price from player
    public void buyUnits(Unit unit, int amount) {
        int unitPrice;
        if(selectedArmyForce == null) {
            selectedArmyForce = new Army();
        }
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
        eventListener.addEventObject(new PlayerEvent(currentPlayerTurn.getPlayerName(),EventNamesConstants.UnitsBuying));
    }
    //retrieve selectedArmyForce to the selectedTerritory
    public void transformSelectedArmyForceToSelectedTerritory() {
        selectedTerritoryByPlayer.getConquerArmyForce().uniteArmies(selectedArmyForce);
    }
    //retrieve selectedArmyForce Collection to the selectedTerritory
    public void buyUnitsCollection(Collection<Unit> unitList) {
        selectedArmyForce.getUnits().addAll(unitList);
        eventListener.addEventObject(new PlayerEvent(currentPlayerTurn.getPlayerName(),EventNamesConstants.UnitsBuying));
    }
    //Rehabilitation of selected territory army
    public void rehabilitateSelectedTerritoryArmy(){
        selectedTerritoryByPlayer.rehabilitateConquerArmy();
        int rehabilitationPrice =getRehabilitationArmyPriceInTerritory(selectedTerritoryByPlayer);
        currentPlayerTurn.decrementFunds(rehabilitationPrice);
        eventListener.addEventObject(new PlayerEvent(currentPlayerTurn.getPlayerName(),EventNamesConstants.ArmyRehabilitation));
    }
    //Rehabilitation of all army - not necessary
    public void rehabilitateAllArmy() {
        getCurrentPlayerTerritories().parallelStream()
            .forEach(Territory::rehabilitateConquerArmy);
    }

    //Bonus #2
    public void selectedPlayerRetirement(){
        //clear past
        roundsHistory.forEach(roundHistory -> {
            roundHistory.getPlayerStatsHistory().forEach(player -> {
                    if(player.getID() == currentPlayerTurn.getID()) {
                       removeTerritoriesOfPlayerFromSpecificTime(player , roundHistory);
                    }
            });
            roundHistory.getPlayerStatsHistory().remove(currentPlayerTurn);
        });
        //clear present
        removeTerritoriesOfPlayerFromCurrentTime();
        eventListener.addEventObject(new PlayerEvent(currentPlayerTurn.getPlayerName(), EventNamesConstants.Retirement));
        gameDescriptor.getPlayersList().remove(currentPlayerTurn);
        //activateEventsHandler();
    }

    //Bonus #2
    public boolean prevReplay(){
        if(!roundsHistory.isEmpty()) {
            replayStack.push(roundsHistory.pop());
            return true;
        }
        return false;
    }
    //Bonus #2
    public boolean nextReplay(){
        if(!replayStack.isEmpty()) {
            roundsHistory.push(replayStack.pop());
            return true;
        }
        return false;
    }
    //Bonus #2
    public void peekHistory(){
        if(!replayStack.isEmpty()) {
            roundNumber = replayStack.peek().getRoundNumber();
            gameDescriptor.setTerritoryMap(replayStack.peek().getCopyOfMap());
            gameDescriptor.setPlayersList(replayStack.peek().getCopyOfPlayersList());
        }
    }
    //Bonus #2
    public void generateReplayState(){
        replayStack.push(new RoundHistory(gameDescriptor,roundNumber)); //push "fake history"
    }
    public void exitReplayState(){
        while(!replayStack.isEmpty()){
            roundsHistory.push(replayStack.pop());
        }
        roundNumber = roundsHistory.peek().getRoundNumber();
        gameDescriptor.setTerritoryMap(roundsHistory.peek().getCopyOfMap());
        gameDescriptor.setPlayersList(roundsHistory.peek().getCopyOfPlayersList());
        roundsHistory.pop();//pop the "fake history"
    }
    //**************************//
    /*     Rounds Management    */
    //**************************//

    public void startGame() {
        loadPlayersIntoQueueOfTurns();
        nextPlayerInTurn();
        eventListener.addEventObject(new RoundEvent(EventNamesConstants.GameStarted));
    }
    public void startOfRoundUpdates() {
        updateAllPlayersProductionStartOfRound();
        updateAllPlayerTerritoriesHold();
        //i think i dont need it->
        //eventListener.addEventObject(new RoundEvent(EventNamesConstants.StartRoundUpdates));

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
        gameDescriptor.setTerritoryMap(roundsHistory.peek().getCopyOfMap()); //This Set the gameDescriptor Territories as copy
        gameDescriptor.setPlayersList(roundsHistory.peek().getCopyOfPlayersList()); //This Set the gameDescriptor players as copy
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
        if (currentPlayerTurn != null) {
            eventListener.addEventObject(new PlayerEvent(currentPlayerTurn.getPlayerName(),EventNamesConstants.PlayerTurnArrived));
            currentPlayerName = currentPlayerTurn.getPlayerName();
        }
        else {
            currentPlayerName = "None";
            eventListener.addEventObject(new RoundEvent(EventNamesConstants.RoundEnded));
        }
    }
    //returns potential production of the current player turn
    //gives current player his funds, for his territories profits
    private void harvestProduction(Player player) {
        player.incrementFunds(calculatePotentialProduction(player));
        eventListener.addEventObject(new PlayerEvent(player.getPlayerName(),EventNamesConstants.FundsIncrement));
    }
    private int calculatePotentialProduction(Player player) {
        return Optional.ofNullable(getTerritories(player))
                .orElse(Collections.emptyList())
                .stream()
                .mapToInt(Territory::getProfit).sum();
    }
    //reduce competence of current player units and clean off the dead units
    private void updateTerritoriesHold(Player player) {
        getTerritories(player)
                .forEach(Territory::reduceCompetence);

        getTerritories(player).stream()
                .filter(Territory::isArmyTotalPowerUnderThreshold)
                .forEach(this::releaseTerritory);
                        //Territory::eliminateThisWeakArmy);
        //activateEventsHandler();
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
        //activateEventsHandler();
    }
    public void activateEventsHandler(){
        eventListener.activateEventsHandler();
    }

    //**************************//
    /* War and Conquest Control*/
    //**************************//

    private void removeTerritoriesOfPlayerFromSpecificTime(Player player,RoundHistory roundHistory) {
        List<Integer> mapsToClear = player.getTerritoriesID();
        while(!mapsToClear.isEmpty()){
            Integer territoryID = mapsToClear.get(0);
            getTerritoryFromSpecificTime(roundHistory,territoryID).eliminateThisWeakArmy();
            mapsToClear.remove(0);
        }
    }

    private void removeTerritoriesOfPlayerFromCurrentTime() {
        List<Integer> mapsToClear = new ArrayList<>(currentPlayerTurn.getTerritoriesID());
        while(!mapsToClear.isEmpty()){
            Integer territoryID = mapsToClear.get(0);
            releaseTerritory(getTerritoryByID(territoryID));
            mapsToClear.remove(0);
        }
    }
    private void releaseTerritory(Territory territory) {
        Player playerSelected = getPlayerByID(territory.getConquerID());
        if (playerSelected != null) {
            playerSelected.removeTerritory(territory.getID());
        }
        territory.eliminateThisWeakArmy();
        eventListener.addEventObject(new TerritoryEvent(territory.getID(), EventNamesConstants.TerritoryRelease));
    }
    //Returns 0 = AttackerLoss : 1 = AttackerWins : 2 = DRAW
    public int attackConqueredTerritoryByWellTimedBattle(){
        return attackConqueredTerritory(new WellTimedBattle(
                selectedTerritoryByPlayer.getConquerArmyForce(),
                selectedArmyForce,
                selectedTerritoryByPlayer,
                getPlayerByID(selectedTerritoryByPlayer.getConquerID()),
                gameDescriptor.getUnitMap().size()));
    }
    //Returns 0 = AttackerLoss : 1 = AttackerWins : 2 = DRAW
    public int attackConqueredTerritoryByCalculatedRiskBattle(){
        return attackConqueredTerritory(new CalculatedRiskBattle(
                selectedTerritoryByPlayer.getConquerArmyForce(),
                selectedArmyForce,
                selectedTerritoryByPlayer,
                getPlayerByID(selectedTerritoryByPlayer.getConquerID())));
    }
    //Returns AttackerLoss - 0 : AttackerWins - 1 : DRAW - 2
    private int attackConqueredTerritory(Battle battle) {
        int result;
        battle.startBattle();
        result = battle.getResult();
        if(result == 1) { // AttackerWins
            selectedTerritoryByPlayer.setConquerID(currentPlayerTurn.getID());
            currentPlayerTurn.addTerritory(selectedTerritoryByPlayer);
            eventListener.addEventObject(new TerritoryEvent(selectedTerritoryByPlayer.getID(),EventNamesConstants.TerritoryConquered));
        }
        else if(result == 0){ // AttackerLoss
            selectedArmyForce = null;
        }
        else{ // DRAW
            eventListener.addEventObject(new TerritoryEvent(selectedTerritoryByPlayer.getID(), EventNamesConstants.TerritoryRelease));
            return result;
        }
        if(battle.isWinnerArmyNotStrongEnoughToHoldTerritory()){
            xChangeFundsForUnitsAndHold(selectedTerritoryByPlayer);
        }
        return result;
    }
    //Returns True: if attacking player conquered the territory, Else: False. anyway its update stats of GameObjects.Territory.
    public boolean conquerNeutralTerritory() {
        if (isSelectedArmyForceBigEnough()) {
            currentPlayerTurn.addTerritory(selectedTerritoryByPlayer);
            selectedTerritoryByPlayer.setConquerArmyForce(selectedArmyForce);
            selectedTerritoryByPlayer.setConquerID(currentPlayerTurn.getID());
        }
        eventListener.addEventObject(new TerritoryEvent(selectedTerritoryByPlayer.getID(),EventNamesConstants.TerritoryConquered));
        return isSelectedArmyForceBigEnough();
    }
    private void xChangeFundsForUnitsAndHold(Territory territory){
        int valueOfArmyForce = territory.getConquerArmyForce().getArmyValueInFunds();
        Player conquer = getPlayerByID(territory.getConquerID());
        conquer.incrementFunds(valueOfArmyForce);
        territory.eliminateThisWeakArmy();
        eventListener.addEventObject(new TerritoryEvent(territory.getID(), EventNamesConstants.TerritoryRelease));
    }
    //**************************//
    /*   Get InformationTable   */
    //**************************//
    public boolean isLastRound(){
        return gameDescriptor.getTotalCycles()==roundNumber;
    }
    public boolean roundStarted(){
        return !(playersTurns.size() == gameDescriptor.getPlayersList().size());
    }
    public List<Territory> getTerritoryListByPlayer(Player player){
        List<Territory> territories = new ArrayList<>(player.getTerritoriesID().size());
        player.getTerritoriesID().forEach(territoryID -> territories.add(getTerritoryByID(territoryID)));
        return territories;
    }
    public Territory getTerritoryByID(Integer territoryID){
        return gameDescriptor.getTerritoryMap().get(territoryID);
    }
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
        int j = 0;
        int winnerPlayerID=1,maxScore =0;
        int size =gameDescriptor.getPlayersList().size();
        int [] playerScores= new int [size+1];
        List<Integer> playersScoreList = new ArrayList<>();
        for (int i=0; i<= size;i++) playerScores[i] = 0;

        //make scores array
        for(Player player: gameDescriptor.getPlayersList()) {
            for(Integer territoryID:player.getTerritoriesID()) {
                playerScores[j]+= gameDescriptor.getTerritoryMap().get(territoryID).getProfit();
            }
            j++;
        }

        for(int i = 0 ; i < size ; i++) {
            playersScoreList.add(playerScores[i]);
        }

        //checks for max score and winner id
        for (int i=0; i< size ; i++) {
            if(playerScores[i] >= maxScore) {
                maxScore = playerScores[i];
                winnerPlayerID = i;
            }
        }

        if(maxScore == 0) {
            return null;
        }

        //checks for draw.
        List<Integer> duplicates =
                playersScoreList.stream().collect(Collectors.groupingBy(Function.identity()))
                        .entrySet()
                        .stream()
                        .filter(e -> e.getValue().size() > 1)
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList());
        duplicates.removeAll(Collections.singletonList(0));
        if(duplicates.size() != 0) {
            for(Integer score : duplicates) {
                if(score == maxScore) {
                    return null;
                }
            }
        }
        winnerName = gameDescriptor.getPlayersList().get(winnerPlayerID).getPlayerName();
        return gameDescriptor.getPlayersList().get(winnerPlayerID);
    }


    //get price of rehabilitation Army in territory
    public int getRehabilitationArmyPriceInTerritory(Territory territory){
        return territory.getRehabilitationArmyPriceInTerritory();
    }
    //checks if undo is valid
    public boolean isUndoPossible() {
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
    public boolean isFirstRound(){
        return roundNumber == 1;
    }
    //Returns True: if yes. Else False.
    private boolean isSelectedArmyForceBigEnough() {
         return selectedArmyForce.getTotalPower() >= selectedTerritoryByPlayer.getArmyThreshold();
    }
    //Checks if current player is the conquer of this GameObjects.Territory.
    public boolean isTerritoryBelongsCurrentPlayer() {
        if(selectedTerritoryByPlayer.getConquerID() == null)
            return false;
        return selectedTerritoryByPlayer.getConquerID().equals(currentPlayerTurn.getID());
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

    public void setSelectedArmyForce(Army selectedArmyForce) {
        this.selectedArmyForce = selectedArmyForce;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public String getGameTitle() { return gameTitle; }

    public void setRoundNumber(int roundNumber) {
        this.roundNumber = roundNumber;
    }

    public Player getPlayerByID(Integer playerID) {
        for(Player player: getGameDescriptor().getPlayersList()){
            if(player.getID()==(playerID)){
                return player;
            }
        }
        return null;
    }
    public String getCurrentPlayerName() { return currentPlayerName; }

    public Player getForcedWinner() {
        nextPlayerInTurn();
        if(currentPlayerTurn == null) {
            loadPlayersIntoQueueOfTurns();
            nextPlayerInTurn();
        }
        return currentPlayerTurn;
    }

    public int getAppearanceOfUnitWithSpecificType(String typeOfUnit) {
        int counterOfSpecificUnitType = 0;
        Territory territory=null;
        for (Player player : gameDescriptor.getPlayersList()) {
            for (Integer territoryID : player.getTerritoriesID()) {
                    territory = getTerritoryByID(territoryID);
                    counterOfSpecificUnitType = (int) (counterOfSpecificUnitType + territory.getConquerArmyForce().getUnits().stream().filter(unit -> typeOfUnit.equals(unit.getType())).count());

            }
        }
        return counterOfSpecificUnitType;
    }

    private Territory getTerritoryFromSpecificTime(RoundHistory roundHistory,Integer territoryID){
        return roundHistory.getMapHistory().get(territoryID);
    }
    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }
    public String getWinnerName() {
        return winnerName;
    }

    public Army getSelectedArmyForce() {
        return selectedArmyForce;
    }
    public boolean checkIfOnlyOnePlayer() {
        return gameDescriptor.getPlayersList().size() == 1;
    }

    public boolean isNextPlayerNull() {
        return playersTurns.peek() != null;
    }

    public GameDescriptor getGameDescriptor() {
        return gameDescriptor;
    }
}
