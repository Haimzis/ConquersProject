package UIConsole;

import Exceptions.invalidInputException;
import GameEngine.GameEngine;
import GameObjects.Player;
import GameObjects.Territory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class UIConsole {
    private String lastKnownXmlPath; //TODO : Ran - use it
    private boolean gameStarted , gameHasBeenPlayed;
    private static final String SOLDIER = "Soldier";
    private GameEngine engine;
    public UIConsole() {
        this.engine = new GameEngine();
    }

    //*******************//
    /*  Main MenuTable  */
    //*******************//
    public void showMainMenu() {
        gameStarted = false;
        int selection;
        String xmlPath, savePath , loadPath;
        System.out.println("Welcome to Conquers!");
        Scanner sc = new Scanner(System.in);
        while(true) {
            System.out.println("Please select one of the following:");
            System.out.println("1.Load XML File"
                    +"\n"
                    + "2.Start game "
                    + "\n"
                    + "3.Show game stats."
                    + "\n"
                    + "4.Start round"
                    +"\n"
                    + "5.Game history."
                    + "\n" 
                    + "6.Exit."
                    + "\n"
                    + "7.Save game."
                    + "\n"
                    + "8.Load game."
                    + "\n"
                    + "9.Undo round");
            try{
                selection = sc.nextInt();
                switch (selection) {
                    case 1: //Load XML
                        if(gameStarted)
                            System.out.println("Cannot load new XML , game has already started.");
                        else {
                            System.out.println("Enter XML path: ");
                            Scanner pathScanner  = new Scanner(System.in);
                            xmlPath = pathScanner.nextLine();
                            try {
                                engine.loadXML(xmlPath);
                            } catch (invalidInputException ignored) {
                            }
                            if(GameEngine.flag == 1) {
                                System.out.println("XML Loaded successfully");
                                lastKnownXmlPath = xmlPath;
                                gameHasBeenPlayed = false; // If player reads a new XML that means he ran over the previous played game.
                            }
                        }
                        break;
                    case 2: // Start game
                        if(GameEngine.flag != 1) { //Descriptor is null , no game has been loaded from xml
                            System.out.println("No loaded game has been found , please load a game first");
                            break;
                        }
                        else if(!gameStarted){ // Game has been loaded but not started yet
                            System.out.println("Game started!");
                            gameStarted = true;
                            if(gameHasBeenPlayed) // If a game has been played and they wish to start a new one with the same XML.
                                engine.loadXML(engine.getDescriptor().getLastKnownGoodString());
                            engine.newGame(); //Initialize manager from descriptor from xml
                            roundManager(); //Start the game , regardless of XML loaded manager or saved game manager
                        }
                        else //game has started AND is running
                            System.out.println("A game is currently running.");
                        break;
                    case 3: // Show game stats
                        if(!gameStarted)
                            System.out.println("Please start a game first");
                        else
                            showGameStats();
                        break;
                    case 4: // 4.Start round
                        if(!gameStarted)
                            System.out.println("Please start a game first!");
                        else {
                            System.out.println("Starting round...");
                            sleepAbit(1);
                            roundManager();
                        }
                        break;
                    case 5: // Game history
                        if(gameStarted)
                            printHistory();
                        break;
                    case 6: // Exit
                        System.exit(1);
                    case 7: // Save game
                        if (gameStarted) {
                            System.out.println("Enter the full path of which the game will be saved:");
                            Scanner pathScanner = new Scanner(System.in);
                            savePath = pathScanner.nextLine();
                            GameEngine.saveGame(Paths.get(savePath), GameEngine.gameManager);
                        } else
                            System.out.println("No game is found to save.");
                        break;
                    case 8: //Load game
                        if(!gameStarted) {
                            System.out.println("Enter path for saved game:");
                            Scanner loadScanner = new Scanner(System.in);
                            loadPath = loadScanner.nextLine();
                            Path load = engine.getLoadFilePath(loadPath);
                            if (engine.loadGame(load)) {
                                gameStarted = true;
                                engine.setDescriptor(GameEngine.gameManager.getGameDescriptor());
                            }
                        }
                        break;
                    case 9: //Undo round
                        if(gameStarted)
                            undoRound();
                        else
                            System.out.println("No game started to undo round.");
                        break;
                }
            }
            catch(InputMismatchException e){
                System.out.println("Enter one of the options listed");
                sc.next();
            } catch (invalidInputException | IOException | ClassNotFoundException ignored) {
            }
        }
    }
    //*******************//
    /*  Territory Func  */
    //******************//
    //Players select a territory if they have none(only have to select if it's the first round).
    private void chooseTerritoryIfNone() {
        System.out.println(GameEngine.gameManager.getCurrentPlayerTurn().getPlayerName()
                + ", You have no territories!"
                + "\n");
        drawMap();
        System.out.println("Please select a territory: ");
        Scanner sc = new Scanner(System.in);
        int territoryID = sc.nextInt();
        while(!engine.getDescriptor().getTerritoryMap().containsKey(territoryID)) {
            System.out.println("Enter a valid territory ID as shown.");
            territoryID = sc.nextInt();
        }
        Territory targetTerritory = engine.getDescriptor().getTerritoryMap().get(territoryID);
        GameEngine.gameManager.setSelectedTerritoryForTurn(targetTerritory);
        if(targetTerritory.isConquered()) {
            attackConqueredTerritoryResult(targetTerritory);
        }else {
            getNeutralTerritory(targetTerritory);
        }
    }
    //The main function who works on the current selected territory.
    private void actOnTerritory() {
        if(!GameEngine.gameManager.getCurrentPlayerTerritories().isEmpty()) {
            int territoryID;
            Scanner scanner = new Scanner(System.in);
            System.out.println("Select a territory to act on: ");
            territoryID = scanner.nextInt();
            while(!checkTerritoryIdValid(territoryID)) {
                System.out.println("Enter a valid territory ID.");
                territoryID = scanner.nextInt();
            }
            Territory targetTerritory = engine.getDescriptor().getTerritoryMap().get(territoryID);
            GameEngine.gameManager.setSelectedTerritoryForTurn(targetTerritory);
            if(GameEngine.gameManager.isTerritoryBelongsCurrentPlayer()) { //Player working on his territory
                actOnSelfTerritory(territoryID, scanner, targetTerritory);
            }
            else if(GameEngine.gameManager.isConquered()) { //Belongs to a different player
                if(GameEngine.gameManager.isTargetTerritoryValid()){ // Is it valid too attack?
                    attackConqueredTerritoryResult(targetTerritory);
                } else { //Not valid
                    System.out.println("Invalid target territory");
                }
            } else { // Neutral(When he has atleast one territory)
                if(GameEngine.gameManager.isTargetTerritoryValid()){
                    getNeutralTerritory(targetTerritory);
                }
                else { //Not valid
                    System.out.println("Invalid target territory");
                }
            }
        } else {
            chooseTerritoryIfNone();
        }
    }
    private void actOnSelfTerritory(int territoryID, Scanner scanner, Territory targetTerritory) {
        int choice;
        System.out.println("You have selected territory number "
                + targetTerritory.getID()
                + "\n"
                + "What do you wish to do?"
                + "\n"
                + "1.Rehabilitate your army."
                + "\n"
                + "2.Enforce army");
        choice = scanner.nextInt();
        switch (choice)
        {
            case 1: // Rehabilitate
                Supplier<Integer> enoughMoney = () -> GameEngine.gameManager.getRehabilitationArmyPriceInTerritory(targetTerritory);
                if(GameEngine.gameManager.isSelectedPlayerHasEnoughMoney(enoughMoney)) {
                    System.out.println("Rehabilitating army on territory number: " + territoryID + "...");
                    sleepAbit(1);
                    GameEngine.gameManager.rehabilitateSelectedTerritoryArmy();
                }
                else
                    System.out.println("Not enough Turings.");
                break;
            case 2: //Buy a UNIT(Soldier for now)
                printAndBuySelectedUnit();
                GameEngine.gameManager.transformSelectedArmyForceToSelectedTerritory();
                break;
        }
    }
    //Act on neutral territory.
    private void getNeutralTerritory(Territory targetTerritory) {
        System.out.println("Territory threshold: "
                + targetTerritory.getArmyThreshold()
                +  " You have "
                + GameEngine.gameManager.getCurrentPlayerFunds()
                + " Turings");
        printAndBuySelectedUnit();
        if(GameEngine.gameManager.conquerNeutralTerritory())
            System.out.println("Territory : "
                    + targetTerritory.getID()
                    + " Has been conquered!"
                    + "\n");
        else
            System.out.println("Conquering failed , army is not above threshold.");
    }
    //Choose Attack Type and call the right battle function
    private int attackConqueredTerritory(){
        int selection;
        Scanner sc = new Scanner(System.in);
        while(true) {
            System.out.println("Please select one of the following:");
            System.out.println("1.CalculatedRisk Attack"
                    + "\n"
                    + "2.WellTimed Attack");
            try {
                selection = sc.nextInt();
                switch (selection) {
                    case 1:
                        return GameEngine.gameManager.attackConqueredTerritoryByCalculatedRiskBattle();
                    case 2:
                        return GameEngine.gameManager.attackConqueredTerritoryByWellTimedBattle();
                }
            }
            catch(InputMismatchException e){
                    System.out.println("Enter one of the options listed");
                    sc.next();
            }
        }
    }
    //manage the battle and update its results
    private void attackConqueredTerritoryResult(Territory targetTerritory) {
        System.out.println("You have selected to attack territory number " + targetTerritory.getID());
        printAndBuySelectedUnit();
        int defendingArmyAmountOfUnits = targetTerritory.getConquerArmyForce().getUnits().size();

        int attackerWon = attackConqueredTerritory();
        if(attackerWon == 1) {
            System.out.println("VICTORY!"
                    + "\n"
                    + "You have conquered territory number: " + targetTerritory.getID());
            if(targetTerritory.getConquerArmyForce()== null) {
                System.out.println("Army is not above threshold , exchanging Turings instead.");
            }
            else printArmyOnTerritory(targetTerritory);
        }
        else if(attackerWon == 0) {
            System.out.println("DEFEAT!");
        }
        else {
            System.out.println("DRAW!");
        }
        System.out.println("The defending territory had "
                + defendingArmyAmountOfUnits
                + " Units!"
                + "\n");
    }
    //checks if a territory is in 1 block away(rows and columns)
    private boolean checkTerritoryIdValid(int territoryID) {
        return engine.getDescriptor().getTerritoryMap().containsKey(territoryID);
    }
    //Prints the army stats on the territory.
    private void printArmyOnTerritory(Territory targetTerritory) {
        System.out.println("This are the territory stats: ");
        targetTerritory.getConquerArmyForce().getUnits().forEach(unit -> System.out.println("Type: "
                + unit.getType()
                + " Firepower: "
                + unit.getCurrentFirePower()
                + " Competence reduction: "
                + unit.getCompetenceReduction()));
    }


    //*******************//
    /*     Unit Func     */
    //******************//
    private void buyUnits(int amount,String unitType) {
       GameEngine.gameManager.buyUnits(engine.getDescriptor().getUnitMap().get(unitType) , amount);
    }
    private void printAndBuySelectedUnit(){
        Scanner scanner = new Scanner(System.in);
        int howManyToAdd, selection;
        String unitType = null;
        printUnitTypesForChoice();
        selection = scanner.nextInt();
        while(selection != 1) { //Force correct selection
            System.out.println("Please enter the correct values shown!");
            selection = scanner.nextInt();
        }
        switch (selection) {//All other cases for next projects
            case 1:
                unitType = SOLDIER;
                break;
        }
        System.out.println("Select how many " + unitType + " to buy");
        howManyToAdd = scanner.nextInt();
        while(howManyToAdd*engine.getDescriptor().getUnitMap().get(unitType).getPurchase() > GameEngine.gameManager.getCurrentPlayerFunds()){ //must enter a valid amount to purchase
            System.out.println("not enough funds , please write down a lower amount to buy");
            howManyToAdd = scanner.nextInt();
            if(howManyToAdd == 0) //Exit loop if the player does not wish to buy at all.
                break;
        }
        buyUnits(howManyToAdd, unitType);
    }
    private void printUnitTypesForChoice() {
        System.out.println("The following units are available: ");
        engine.getDescriptor().getUnitMap()
                .forEach((k,v)-> System.out.println(k));
        System.out.println("Enter 1 for soldier");
    }

    //*******************//
    /*     Stats Func    */
    //******************//
    //Prints the winner of the game.
    private void getGameWinner() {
        Player winner = GameEngine.gameManager.getWinnerPlayer();
        if(winner == null){
            System.out.println("DRAW!");
        } else {
            System.out.println("The game has finished , concluding winners...");
            sleepAbit(1);
            System.out.println("The winning player is: " + winner.getPlayerName());
            System.out.println("Ending game..." + "\n");
            sleepAbit(1);
        }
    }
    private void printFundsBeforeProduction() {
        System.out.println("Turings before potential production added: "
                + GameEngine.gameManager.getFundsBeforeProduction());

    }
    private void printFunds() {
        System.out.println("Turings after production: "+ GameEngine.gameManager.getCurrentPlayerFunds());
    }
    private void printHistory() {
        printMapLegend();
        GameEngine.gameManager.getMapsHistoryByOrder()
                .forEach(this::drawMap);
    }
    //Show the stats of current round.
    private void showRoundStats() {
        System.out.println("Current round is: "
                + (GameEngine.gameManager.roundNumber)
                + "\n"
                + "The total rounds for this game are: "
                + engine.getDescriptor().getTotalCycles()
                + "\n");
    }
    private void showCurrentPlayerStats(){ // We print this after a new turn has started
        System.out.println("Territory information:"
                + "\n"
                + "--------------------------------------");
        showCurrentPlayerTerritoriesStats();
    }
    private void showAllPlayersStats() {
        System.out.println("The players who are currently playing are: " + "\n");
        engine.getDescriptor().getPlayersList().parallelStream()
                .forEach(player -> System.out.println("Player name: "
                        + player.getPlayerName()
                        + "\n" + "Player ID: "
                        + player.getID() + "\n"
                        + "Current amount of territories: "
                        + player.getTerritoriesID().size()
                        + "\n"
                        + "The current turing amount is: "
                        + player.getFunds()));
        System.out.println("\n");
    }
    //Prints the game stats so far
    private void showGameStats(){
        System.out.println("Printing game stats...");
        sleepAbit(1);
        drawMap();
        showRoundStats();
        showAllPlayersStats();
    }
    //Print the stats of current player territories.
    private void showCurrentPlayerTerritoriesStats() {
        System.out.println(GameEngine.gameManager.getCurrentPlayerTurn().getPlayerName() + " holds: " + GameEngine.gameManager.getCurrentPlayerTerritoriesAmount() + " territories.");
        GameEngine.gameManager.getCurrentPlayerTerritories().forEach(territory ->
                System.out.println("Territory ID: " + territory.getID()
                        + " Total power: "
                        + territory.getConquerArmyForce().getTotalPower()
                        + "\n"
                        + "Army threshold: "
                        + territory.getArmyThreshold()
                        + "\n"
                        + "Current units: "
                        + "\n"
                        + "Soldier"
                        + ", Amount: "
                        + territory.getConquerArmyForce().getUnits().size()
                        + " Power: "
                        + territory.getConquerArmyForce().getTotalPower()
                        + "\n"
                        + "The cost to regain full power for all units: "
                        + GameEngine.gameManager.getRehabilitationArmyPriceInTerritory(territory)
                        + "\n"
                        + "--------------------------------------"
                        + "\n"));
    }

    //*******************************//
    /*     Round Management Func    */
    //******************************//
    //Start turn of player in turn.
    private void startTurn() {
        System.out.println("Round number: " + GameEngine.gameManager.roundNumber);
        System.out.println("Current player: " +GameEngine.gameManager.getCurrentPlayerTurn().getPlayerName());
        printFundsBeforeProduction();
        printFunds();
        showCurrentPlayerStats();
    }
    //This function manages each round , including turns between players.
    private void roundManager(){
        GameEngine.gameManager.startOfRoundUpdates();
        while(!GameEngine.gameManager.isCycleOver()) {
            GameEngine.gameManager.nextPlayerInTurn();
            if(GameEngine.gameManager.roundNumber == 1 && GameEngine.gameManager.getCurrentPlayerTerritories().isEmpty()) {
                firstRound();
            } else {
                startTurn();
                showPlayerTurnOptions();
                printFunds();
                showCurrentPlayerTerritoriesStats();
                System.out.println("Switching players... " + "\n");
                sleepAbit(1);
            }
        }
        System.out.println("Ending round..." + "\n");
        GameEngine.gameManager.endOfRoundUpdates();
        sleepAbit(1);
        if(GameEngine.gameManager.isGameOver()) {
            getGameWinner();
            gameStarted = false;
            gameHasBeenPlayed = true;
            GameEngine.gameManager = null;
        }
    }
    private void undoRound() {
        if(GameEngine.gameManager.isUndoPossible()) {
            System.out.println("Undoing round...");
            sleepAbit(1);
            GameEngine.gameManager.roundUndo();
        }
        else
            System.out.println("Undo is not possible at first round.");
    }
    //show options of the current player in turn.
    private void showPlayerTurnOptions() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Choose what to do: "
                + "\n"
                + "1.End turn."
                + "\n"
                + "2.Act on a territory.");
        int choice = scanner.nextInt();
        switch (choice) {
            case 1: //Laying on the eggs - Do nothing
                break;
            case 2: //Players wishes to act on a territory
                if(!GameEngine.gameManager.getCurrentPlayerTerritories().isEmpty()) {
                    drawMap();
                }
                actOnTerritory();
                break;
        }
    }
    //Firsts round for players too choose territories.
    private void firstRound() {
        showCurrentPlayerStats();
        chooseTerritoryIfNone();
    }
    //******************//
    /*     MapTable Func    */
    //*****************//
    private void drawMap() {
        printMapLegend();
        Table map = new Table(engine.getDescriptor().getTerritoryMap()
                ,engine.getDescriptor().getRows()
                ,engine.getDescriptor().getColumns()
                ,engine.getDescriptor().getPlayersList().get(0)
                ,engine.getDescriptor().getPlayersList().get(1));
        map.setTableDefaultStyle(1);
        map.print();
    }
    private void drawMap(Map<Integer,Territory> historyMap) {
        Table map = new Table(historyMap
                ,engine.getDescriptor().getRows()
                ,engine.getDescriptor().getColumns()
                ,engine.getDescriptor().getPlayersList().get(0)
                ,engine.getDescriptor().getPlayersList().get(1));
        map.setTableDefaultStyle(1);
        map.print();
    }
    private void printMapLegend() {
        System.out.println("MapTable legend");
        System.out.println(engine.getDescriptor().getPlayersList().get(0).getPlayerName()
                + ": "
                + "X"
                + ", "
                + engine.getDescriptor().getPlayersList().get(1).getPlayerName()
                + ": "
                + "O");
        System.out.println("----------------------------");
        System.out.println("ID: "
                + "\n"
                + "Profit: "
                + "\n" + "Status: "
                + "\n"
                + "Threshold: ");
        System.out.println("----------------------------");
    }

    //*********************//
    /*     others Func    */
    //*******************//
    private void sleepAbit(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
