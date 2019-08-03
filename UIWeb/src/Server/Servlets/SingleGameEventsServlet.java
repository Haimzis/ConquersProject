package Server.Servlets;

import Events.EventListener;
import Events.EventObject;
import Server.Constants.Constants;
import Server.Utils.RoomManager;
import Server.Utils.RoomsContainer;
import Server.Utils.ServletUtils;
import Server.Utils.SessionUtils;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name = "SingleGameEventsServlet")
public class SingleGameEventsServlet extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        RoomsContainer roomContainer = ServletUtils.getRoomsContainer(request.getServletContext());
        RoomManager roomManager = roomContainer.getRoomByUserName(SessionUtils.getUsername(request));
        String username = SessionUtils.getUsername(request);
        if (username == null) {
            response.sendRedirect(request.getContextPath() + "/index.html");
        }
        int gameVersion = ServletUtils.getVersionIntParameter(request, Constants.GAME_VERSION_PARAMETER);
        if (gameVersion == Constants.INT_PARAMETER_ERROR) {
            return;
        }
                /*
        Synchronizing as minimum as I can to fetch only the relevant information from the chat manager and then only processing and sending this information onward
        Note that the synchronization here is on the ServletContext, and the one that also synchronized on it is the chat servlet when adding new chat lines.
         */
        int gameManagerVersion = 0;
        List<EventObject> gameCurrentEvents;
        synchronized (getServletContext()) {
            EventListener gameEventsListener = roomManager.getGameManager().getEventListener();
            gameManagerVersion = gameEventsListener.getVersion();
            gameCurrentEvents = gameEventsListener.getEventObjectsList(gameVersion);
        }

        // log and create the response json string
        GameEventsAndVersion gameEventsAndVersion = new GameEventsAndVersion(gameCurrentEvents, gameManagerVersion);
        Gson gson = new Gson();
        String jsonResponse = gson.toJson(gameEventsAndVersion);
        logServerMessage("Server GameRoom version: " + gameManagerVersion + ", User '" + username + "' Game version: " + gameVersion);
        logServerMessage(jsonResponse);

        try (PrintWriter out = response.getWriter()) {
            out.print(jsonResponse);
            out.flush();
        }
    }
    private void logServerMessage(String message){
        System.out.println(message);
    }

    class GameEventsAndVersion {

        final private List<EventObject> gameEvents;
        final private int version;

        public GameEventsAndVersion(List<EventObject> gameEvents, int version) {
            this.gameEvents = gameEvents;
            this.version = version;
        }
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request,response);
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request,response);
    }

}
