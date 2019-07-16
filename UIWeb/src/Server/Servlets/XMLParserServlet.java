package Server.Servlets;

import Exceptions.invalidInputException;
import GameEngine.GameEngine;
import GameEngine.GameDescriptor;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "XMLParserServlet")
public class XMLParserServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        GameEngine engine = new GameEngine();
        GameDescriptor descriptor = null;
        PrintWriter out = response.getWriter();
        request.setAttribute("isXMLLoaded" , true);
        try {
            descriptor = engine.loadXML(request.getPart("xmlFile").getInputStream(), request.getPart("xmlFile").getSubmittedFileName());
        } catch (invalidInputException e) {
            out.println(e.getMessage());
            request.setAttribute("isXMLLoaded" , false);
        }
        Gson gson = new Gson();
        out.println(gson.toJson(descriptor));
        engine.newGame(descriptor);
    }
}
