package Server.Servlets;

import GameEngine.GameEngine;
import GameObjects.GameStatus;
import GameObjects.Player;
import Server.Utils.*;
import com.google.gson.Gson;
import GameEngine.GameManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "GamesServlet")
public class GamesServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        switch(action) {
            case "roomsList":
                showGamesList(response);
                break;
            case "joinRoom":
                addUserToRoom(request,response);
                break;
            case "roomDetails":
                int roomId= Integer.parseInt(request.getParameter("id"));
                sendRoomDetails(roomId,response);
                break;
            case "roomDetailsByUser":
                sendRoomDetails(request , response);
                break;
        }
    }

    private void addUserToRoom(HttpServletRequest request, HttpServletResponse response) throws IOException {
        GameEngine engine = ServletUtils.getGameEngine(getServletContext());
        RoomsManager roomsManager = ServletUtils.getRoomsManager(getServletContext());
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        String userName = request.getParameter("user");
        int gameId = Integer.parseInt(request.getParameter("roomId"));
        GameManager manager =  GameEngine.getGameManagers().get(gameId);
        Player newPlayer = engine.createPlayerFromUser(userName, ServletUtils.ID++, manager.getGameDescriptor().getInitialFunds());
        RoomDescriptor room = roomsManager.getRoom(gameId);

        if(room.status.equals(GameStatus.WaitingForPlayers)) {
            room.addPlayer(newPlayer);
            System.out.println(newPlayer.getPlayerName() + " Has joined room number " + room.id);
            out.println(gson.toJson(new LoadGameStatus(true, "")));
            room.checkStatus();
            if(room.status.equals(GameStatus.Running)) {
                System.out.println("Game " + gameId +" has started!");
                //Set the Game Manager players list and set it to running
                manager.getGameDescriptor().setPlayersList(new ArrayList<>(room.activePlayers));
                manager.setStatus(GameStatus.Running);
            }
        } else { //Game is running
            out.println(gson.toJson(new LoadGameStatus(false , "Game is running")));
        }
    }

    private void sendRoomDetails(int roomId , HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        out.println(gson.toJson(ServletUtils.getRoomsManager(getServletContext()).getRoom(roomId)));
    }
    private void sendRoomDetails(HttpServletRequest request ,HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        String userName = SessionUtils.getUsername(request);
        out.println(gson.toJson(ServletUtils.getRoomsManager(getServletContext()).getRoomByUserName(userName)));
    }

    private void showGamesList(HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        out.println(gson.toJson(ServletUtils.getRoomsManager(getServletContext()).getActiveRooms()));
    }
}
