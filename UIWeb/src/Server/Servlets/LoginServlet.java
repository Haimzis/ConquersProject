package Server.Servlets;

import GameEngine.PlayerManager;
import Utils.Constants;
import Utils.ServletUtils;
import Utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginServlet extends HttpServlet {
    private final String LOBBY_URL = "../Lobby/lobby.html";
    private final String SIGN_UP_URL = "../SignUp/signup.html";
    private final String LOGIN_ERROR_URL = "/Pages/LoginError/login_attempt_after_error.jsp";
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String playerFromSession = SessionUtils.getPlayerName(request);
        PlayerManager playerManager = ServletUtils.getPlayerManager(getServletContext());
        if (playerFromSession == null) {
            //user is not logged in yet
            String playerNameFromParameter = request.getParameter(Constants.PLAYER_NAME);
            if (playerNameFromParameter == null) {
                response.sendRedirect(SIGN_UP_URL);
            } else {
                //normalize the username value
                playerNameFromParameter = playerNameFromParameter.trim();
                synchronized (this) {
                    if (playerManager.isPlayerExists(playerNameFromParameter)) {
                        String errorMessage = "Player name " + playerNameFromParameter + " already exists. Please enter a different name.";
                        // username already exists, forward the request back to index.html
                        // with a parameter that indicates that an error should be displayed
                        // the request dispatcher obtained from the servlet context is one that MUST get an absolute path (starting with'/')
                        // and is relative to the web app root
                        // see this link for more details:
                        // http://timjansen.github.io/jarfiller/guide/servlet25/requestdispatcher.xhtml
                        request.setAttribute(Constants.PLAYER_NAME_ERROR, errorMessage);
                        getServletContext().getRequestDispatcher(LOGIN_ERROR_URL).forward(request, response);
                    } else {
                        //add the new user to the users list
                        playerManager.addPlayer(playerNameFromParameter);
                        //set the username in a session so it will be available on each request
                        //the true parameter means that if a session object does not exists yet
                        //create a new one
                        request.getSession(true).setAttribute(Constants.PLAYER_NAME, playerNameFromParameter);

                        //redirect the request to the chat room - in order to actually change the URL
                        System.out.println("On login, request URI is: " + request.getRequestURI());
                        response.sendRedirect(LOBBY_URL);
                    }
                }
            }
        } else {
            //user is already logged in
            response.sendRedirect(LOBBY_URL);
        }
    }
}
