package Server.Servlets;

import GameEngine.GameManager;
import GameObjects.*;
import Server.Utils.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@WebServlet(name = "SingleGameServlet")
public class SingleGameServlet extends HttpServlet {
    private static final Object statusLock = new Object();
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        switch (action) {
            case "leaveGame": // This is NOT RETIRE
                leaveGame(request , response);
                break;
            case "retire":
                retirePlayer(request , response);
                break;
            case "gameStatus":
                sendStatus(request, response);
                break;
            case "singleGameDetails":
                sendSingleGameDetails(request , response);
                break;
            case "singleGameOnlinePlayers":
                sendHowManyPlayersAreOnline(request , response);
                break;
            case "endTurn":
                endTurn(request , response);
                break;
            case "startGame":
                startGame(request , response);
                break;
            case "checkTerritory":
                checkTerritory(request , response);
                break;
            case "selectTerritory":
                int territoryId = Integer.parseInt(request.getParameter("id"));
                setSelectedTerritory(territoryId , request);
                break;
            case "getOtherPlayersStats":
                getOtherPlayerStats(request , response);
                break;
            case "getOwnPlayerStats":
                getOwnPlayerStats(request , response);
                break;
            case "buyUnits":
                int howMany = Integer.parseInt(request.getParameter("amount"));
                String unitType = request.getParameter("unit");
                buyUnits(howMany, unitType ,  request, response);
                break;
            case "territoryAction":
                String actionType = request.getParameter("actionType");
                territoryAction(actionType , request , response);
                break;
            case "updateTerritories":
                returnUpdatedTerritories(request , response);
                break;
            case "currentRound":
                returnCurrentRound(request , response);
                break;
            case "resetGame":
                resetGame(request);
                break;


        }
    }

    private  synchronized void resetGame(HttpServletRequest request) {
        String userName = SessionUtils.getUsername(request);
        GameManager manager = ServletUtils.getRoomsManager(request.getServletContext()).getRoomByUserName(userName).getManager();
        RoomsManager roomManager = ServletUtils.getRoomsManager(request.getServletContext());
        RoomDescriptor room =  roomManager.getRoomByUserName(userName);
        room.removePlayerByUserName(userName);
        if(room.registeredPlayers == 0) {
            manager.resetManager();
            room.status = GameStatus.WaitingForPlayers;
        }
    }

    private void retirePlayer(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userName = SessionUtils.getUsername(request);
        GameManager manager = ServletUtils.getRoomsManager(request.getServletContext()).getRoomByUserName(userName).getManager();
        RoomsManager roomManager = ServletUtils.getRoomsManager(request.getServletContext());
        if(manager != null) {
            manager.selectedPlayerRetirement();
            roomManager.getRoomByUserName(userName).removePlayerByUserName(userName);
            if(manager.checkIfOnlyOnePlayer()) { //Game end
                manager.getForcedWinner();
                manager.setStatus(GameStatus.Finished);
            }
            if(!manager.isNextPlayerNull()) { // Not everyone retired and not last player in round
                manager.nextPlayerInTurn();
            } else { // Last player in round retired.
                manager.endOfRoundUpdates();
                if(manager.isGameOver()) {
                    checkWinnerIfAny(manager , response , request);
                    return;
                }
                manager.startOfRoundUpdates();
            }
        }
    }

    private void returnCurrentRound(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String userName = SessionUtils.getUsername(request);
        GameManager manager = ServletUtils.getRoomsManager(request.getServletContext()).getRoomByUserName(userName).getManager();
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        out.print(gson.toJson(new EndGameMessage(manager.getRoundNumber() , manager.getWinnerName())));
    }


    private void endTurn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userName = SessionUtils.getUsername(request);
        GameManager manager = ServletUtils.getRoomsManager(request.getServletContext()).getRoomByUserName(userName).getManager();
        if(manager != null) {
            if(manager.isCycleOver()) {
                manager.endOfRoundUpdates();
                if(manager.isGameOver()) {
                    checkWinnerIfAny(manager , response , request);
                    return;
                }
                // start round
                manager.startOfRoundUpdates();
                manager.nextPlayerInTurn();
            } else {
                manager.nextPlayerInTurn();
            }
        }
    }

    private void checkWinnerIfAny(GameManager manager, HttpServletResponse response, HttpServletRequest request) throws IOException {
        if(manager.isGameOver()) {
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            Gson gson = new Gson();
            Player winner = manager.getWinnerPlayer();
            manager.setStatus(GameStatus.Finished);
            ServletUtils.getRoomsManager(request.getServletContext()).getRoomByUserName(SessionUtils.getUsername(request)).status = GameStatus.Finished;
            if(winner == null) { //Show draw message
                out.println(gson.toJson(new GameOverMessage("", true, GameStatus.Finished)));
            }
            else { //Need to show the winner.
                out.println(new GameOverMessage(winner.getPlayerName(), false , GameStatus.Finished));
            }
        }
    }

    private void returnUpdatedTerritories(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String userName = SessionUtils.getUsername(request);
        PrintWriter out = response.getWriter();
        GsonBuilder gsonBuilder = new GsonBuilder().serializeNulls();
        Gson gson=gsonBuilder.create();
        GameManager manager = ServletUtils.getRoomsManager(request.getServletContext()).getRoomByUserName(userName).getManager();
        if(manager != null) {
            out.print(gson.toJson(manager.getGameDescriptor().getTerritoryMap()));
        }
    }

    private void territoryAction(String actionType , HttpServletRequest request , HttpServletResponse response) throws IOException {
        switch(actionType) {
            case "neutral":
                startNeutralAttack(request , response);
                break;
            case "wellTimed":
                startEnemyAttackWellTimed(request , response);
                break;
            case "calculatedRisk":
                startEnemyAttackCalculatedRisk(request , response);
                break;
            case "rehabilitate":
                rehabilitateTerritory(request , response);
                break;
            case "enforceTerritory":
                enforceTerritory(request, response);
                break;
        }
        String userName = SessionUtils.getUsername(request);
        GameManager manager = ServletUtils.getRoomsManager(request.getServletContext()).getRoomByUserName(userName).getManager();
        manager.setSelectedArmyForce(null);
    }

    private void enforceTerritory(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String userName = SessionUtils.getUsername(request);
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        GameManager manager = ServletUtils.getRoomsManager(request.getServletContext()).getRoomByUserName(userName).getManager();
        if(manager != null) {
            manager.transformSelectedArmyForceToSelectedTerritory();
            out.print(gson.toJson(new TerritoryActionMessage(true, manager.getSelectedTerritoryByPlayer().getID())));
        }
    }

    private void rehabilitateTerritory(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String userName = SessionUtils.getUsername(request);
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        GameManager manager = ServletUtils.getRoomsManager(request.getServletContext()).getRoomByUserName(userName).getManager();
        if(manager != null) {
            Supplier<Integer> enoughMoney = () -> manager.getRehabilitationArmyPriceInTerritory(manager.getSelectedTerritoryByPlayer());
            if(manager.isSelectedPlayerHasEnoughMoney(enoughMoney)) {
                manager.rehabilitateSelectedTerritoryArmy();
                out.print(gson.toJson(new TerritoryActionMessage(true , manager.getSelectedTerritoryByPlayer().getID())));
            } else {
                out.print(gson.toJson(new TerritoryActionMessage(false , manager.getSelectedTerritoryByPlayer().getID())));
            }
        }
    }

    private void startEnemyAttackCalculatedRisk(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String userName = SessionUtils.getUsername(request);
        PrintWriter out = response.getWriter();
        GsonBuilder gsonBuilder = new GsonBuilder().serializeNulls();
        Gson gson=gsonBuilder.create();
        GameManager manager = ServletUtils.getRoomsManager(request.getServletContext()).getRoomByUserName(userName).getManager();
        if(manager != null) {
            Army defendingArmy = new Army(manager.getSelectedTerritoryByPlayer().getConquerArmyForce());
            int selectedTerritoryId = manager.getSelectedTerritoryByPlayer().getID();
            Army attackingArmy = new Army(manager.getSelectedArmyForce());
            int attackerWon = manager.attackConqueredTerritoryByCalculatedRiskBattle();
            if(attackerWon == 1) { //Win
                out.println(gson.toJson(new TerritoryActionMessage(true , selectedTerritoryId,attackingArmy, defendingArmy , userName)));
            }
            else if(attackerWon == 0) { //Defeat
                out.println(gson.toJson(new TerritoryActionMessage(false , selectedTerritoryId,attackingArmy, defendingArmy , userName)));
            }
            else { // Draw
                out.print(gson.toJson(new TerritoryActionMessage(true , selectedTerritoryId , attackingArmy , defendingArmy)));
            }
        }
    }

    private void startEnemyAttackWellTimed(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String userName = SessionUtils.getUsername(request);
        PrintWriter out = response.getWriter();
        GsonBuilder gsonBuilder = new GsonBuilder().serializeNulls();
        Gson gson=gsonBuilder.create();
        GameManager manager = ServletUtils.getRoomsManager(request.getServletContext()).getRoomByUserName(userName).getManager();
        if(manager != null) {
            Army defendingArmy = new Army(manager.getSelectedTerritoryByPlayer().getConquerArmyForce());
            Army attackingArmy = new Army(manager.getSelectedArmyForce());
            int selectedTerritoryId = manager.getSelectedTerritoryByPlayer().getID();
            int attackerWon = manager.attackConqueredTerritoryByWellTimedBattle();
            if(attackerWon == 1) { //Win
                out.println(gson.toJson(new TerritoryActionMessage(true, selectedTerritoryId, attackingArmy, defendingArmy, userName)));
            }
            else if(attackerWon == 0) { //Defeat
                out.println(gson.toJson(new TerritoryActionMessage(false , selectedTerritoryId,attackingArmy, defendingArmy , userName)));
            }
            else { // Draw
                out.print(gson.toJson(new TerritoryActionMessage(true , selectedTerritoryId , attackingArmy , defendingArmy)));
            }
        }
    }

    private void startNeutralAttack(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String userName = SessionUtils.getUsername(request);
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        GameManager manager = ServletUtils.getRoomsManager(request.getServletContext()).getRoomByUserName(userName).getManager();
        if(manager != null) {
            Army attackingArmy = manager.getSelectedArmyForce();
            if(manager.conquerNeutralTerritory()) {
                out.println(gson.toJson(new TerritoryActionMessage(true , manager.getSelectedTerritoryByPlayer().getID() , attackingArmy , userName)));
            } else {
                out.println(gson.toJson(new TerritoryActionMessage(false , manager.getSelectedTerritoryByPlayer().getID()  , attackingArmy , userName)));
            }
        }
    }

    private void buyUnits(int howMany, String unitType, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String userName = SessionUtils.getUsername(request);
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        GameManager manager = ServletUtils.getRoomsManager(request.getServletContext()).getRoomByUserName(userName).getManager();
        if (manager != null) {
            Unit unitToBuy = manager.getGameDescriptor().getUnitMap().get(unitType);
            if(unitToBuy != null) {
                int unitCost = unitToBuy.getPurchase();
                int total = unitCost * howMany;
                if(manager.getCurrentPlayerFunds() < total) {
                    out.println(gson.toJson(new BuyUnitsMessage(false)));
                } else {
                    manager.buyUnits(unitToBuy, howMany);
                    List<Unit> unitsBought = manager.getSelectedArmyForce().getUnits();
                    int fundsAfterPurchase = manager.getCurrentPlayerFunds();
                    out.println(gson.toJson(new BuyUnitsMessage(unitsBought , fundsAfterPurchase, userName , true)));
                }
            }
        }
    }

    private void getOwnPlayerStats(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String userName = SessionUtils.getUsername(request);
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        String color = ServletUtils.getRoomsManager(request.getServletContext())
                .getRoomByUserName(userName)
                .getPlayerByUsername(userName)
                .getColor();
        String playerName = ServletUtils.getRoomsManager(request.getServletContext())
                .getRoomByUserName(userName)
                .getPlayerByUsername(userName)
                .getPlayerName();
        int funds = ServletUtils.getRoomsManager(request.getServletContext())
                .getRoomByUserName(userName)
                .getPlayerByUsername(userName)
                .getFunds();

        out.println(gson.toJson(new SimplifiedPlayer(color, playerName , funds)));
    }

    private void getOtherPlayerStats(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String userName = SessionUtils.getUsername(request);
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        GameManager manager = ServletUtils.getRoomsManager(request.getServletContext()).getRoomByUserName(userName).getManager();
        if(manager != null) {
            List<Player> otherPlayers = new ArrayList<>(manager.getGameDescriptor().getPlayersList());
            otherPlayers.remove(ServletUtils.getRoomsManager(request.getServletContext())
                    .getRoomByUserName(userName)
                    .getPlayerByUsername(userName));
            out.println(gson.toJson(otherPlayers));
        }
    }

    private void setSelectedTerritory(int territoryId, HttpServletRequest request) {
        String userName = SessionUtils.getUsername(request);
        GameManager manager = ServletUtils.getRoomsManager(request.getServletContext()).getRoomByUserName(userName).getManager();
        if(manager != null) {
            manager.setSelectedTerritoryForTurn(manager.getGameDescriptor().getTerritoryMap().get(territoryId));
        }
    }

    private void checkTerritory(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String userName = SessionUtils.getUsername(request);
        GameManager manager = ServletUtils.getRoomsManager(request.getServletContext()).getRoomByUserName(userName).getManager();
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        if(manager != null) {
            boolean doesPlayerHasTerritories = manager.getCurrentPlayerTerritories().isEmpty();
            Territory selectedTerritory = manager.getSelectedTerritoryByPlayer();
            if(manager.isTerritoryBelongsCurrentPlayer()) {
                out.println(gson.toJson(new TerritoryMessage(false , false , true , true , "")));
            } else {
                if(manager.isFirstRound() || manager.isTargetTerritoryValid()|| doesPlayerHasTerritories) {
                    if(!manager.isConquered()) {
                        out.println(gson.toJson(new TerritoryMessage(true , false , false , true , "")));
                    } else {
                        out.println(gson.toJson(new TerritoryMessage(false , true , false , true , "")));
                    }
                } else {
                    out.println(gson.toJson(new TerritoryMessage(false
                            , false
                            , false
                            , false
                            , "Territory " + selectedTerritory.getID() + " Is not valid")));
                }
            }
        }
    }

    private synchronized  void startGame(HttpServletRequest request , HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String userName = SessionUtils.getUsername(request);
        GameManager manager = ServletUtils.getRoomsManager(request.getServletContext()).getRoomByUserName(userName).getManager();
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        if(manager != null) {
            manager.loadPlayersIntoQueueOfTurns();
            manager.nextPlayerInTurn();
            out.println(gson.toJson(new LoadGameStatus(true , "")));
        }
    }



    private void sendHowManyPlayersAreOnline(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        out.println(gson.toJson(ServletUtils.getRoomsManager(request.getServletContext())
                .getRoomByUserName(SessionUtils.getUsername(request))
                .activePlayers));
    }

    private void sendSingleGameDetails(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        out.println(gson.toJson(new SingleSimplifiedGameManager(ServletUtils
                .getRoomsManager(request.getServletContext())
                .getRoomByUserName(SessionUtils.getUsername(request))
                .getManager())));
    }


    private synchronized  void  sendStatus(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        String userName = SessionUtils.getUsername(request);
        RoomsManager roomsManager = ServletUtils.getRoomsManager(request.getServletContext());
        GameStatus status;
        GameManager manager = roomsManager.getRoomByUserName(userName).getManager();
        if(manager != null) {
            status = manager.getStatus();
            String name = "";
            if(status.equals(GameStatus.Running)) {
                name = manager.getCurrentPlayerName(); //Set the player that in turn at the start
            }
            out.println(gson.toJson(new GameStatusMessage(status, name)));
        }
    }


    private void leaveGame(HttpServletRequest request, HttpServletResponse response) {
        RoomsManager roomsManager = ServletUtils.getRoomsManager(getServletContext());
        String userName = SessionUtils.getUsername(request);
        roomsManager.getRoomByUserName(userName).removePlayerByUserName(userName);
    }
}
