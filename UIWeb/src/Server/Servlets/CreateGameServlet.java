package Server.Servlets;

import Exceptions.invalidInputException;
import GameEngine.GameDescriptor;
import GameEngine.GameEngine;
import GameObjects.Player;
import Server.Constants.Constants;
import Server.Utils.LoadGameStatus;
import com.google.gson.Gson;
import GameEngine.GameManager;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "CreateGameServlet")
public class CreateGameServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        GameEngine engine = (GameEngine) getServletContext().getAttribute("engine");
        response.setContentType("application/json");
        GameDescriptor descriptor;
        GameManager currentManager;
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        try {
            descriptor = engine.loadXML(request.getPart("xmlFile").getInputStream(), request.getPart("xmlFile").getSubmittedFileName());
            out.println(gson.toJson(new LoadGameStatus(true, "")));
            currentManager = engine.newGame(descriptor);
            Player host = engine.createPlayerFromUser(request.getParameter("creator"), 1 , descriptor.getInitialFunds());
            currentManager.getGameDescriptor().insertNewPlayer(host);
            System.out.println("Manager created , host inserted");

        } catch (invalidInputException e) {
            out.println(gson.toJson(new LoadGameStatus(false, e.getMessage())));
            System.out.println("Manager didnt created , host didnt inserted");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}
