package Server.Servlets;

import GameEngine.GameManager;
import GameObjects.GameStatus;
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
            case "neutralTerritory":
                break;
            case "gameStatus":
                returnStatus(request, response);
                break;
            case "singleGameDetails":
                sendSingleGameDetails(request , response);
                break;
        }
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

    private void returnStatus(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
