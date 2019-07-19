package Server.Servlets;

import Exceptions.invalidInputException;
import GameEngine.GameDescriptor;
import GameEngine.GameEngine;
import Server.Utils.*;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


@WebServlet(name = "CreateGameServlet") @MultipartConfig
public class CreateGameServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        GameEngine engine = ServletUtils.getGameEngine(getServletContext());
        RoomsManager roomsManager = ServletUtils.getRoomsManager(getServletContext());
        response.setContentType("application/json");
        GameDescriptor descriptor;
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        try {
            descriptor = engine.loadXML(request.getPart("xml").getInputStream(), request.getPart("xml").getSubmittedFileName());
            out.println(gson.toJson(new LoadGameStatus(true, "")));
            RoomDescriptor newRoom = new RoomDescriptor(engine.newGame(descriptor), SessionUtils.getUsername(request));
            roomsManager.addNewRoom(newRoom);
        } catch (invalidInputException e) {
            out.println(gson.toJson(new LoadGameStatus(false, e.getMessage())));
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("In GET!");
    }
}