package Server.Servlets;

import GameEngine.GameManager;
import GameObjects.GameStatus;
import GameObjects.Territory;
import Server.Utils.*;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "SingleGameServlet")
public class SingleGameServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        switch (action) {
            case "leaveGame": // This is NOT RETIRE
                leaveGame(request , response);
                break;
            case "retire":
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
            case "endTurnDetails":

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
        String userName = SessionUtils.getUsername(request);
        GameManager manager = ServletUtils.getRoomsManager(request.getServletContext()).getRoomByUserName(userName).getManager();
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        if(manager != null) {
            Territory selectedTerritory = manager.getSelectedTerritoryByPlayer();
            if(manager.isTerritoryBelongsCurrentPlayer()) {
                out.println(gson.toJson(new TerritoryMessage(false , false , true , true , "")));
            } else {
                if(manager.isTargetTerritoryValid()) {
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

    private void startGame(HttpServletRequest request , HttpServletResponse response) {
        String userName = SessionUtils.getUsername(request);
        GameManager manager = ServletUtils.getRoomsManager(request.getServletContext()).getRoomByUserName(userName).getManager();
        if(manager != null) {
            manager.nextPlayerInTurn();
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

    private void sendStatus(HttpServletRequest request, HttpServletResponse response) throws IOException {
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