package Server.Servlets;

import Server.Constants.Constants;
import Server.Users.UserManager;
import Server.Utils.ServletUtils;
import Server.Utils.SessionUtils;
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
    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletUtils.getGameEngine(getServletContext()); //Set the first game engine.
        ServletUtils.getRoomsContainer(getServletContext()); //set the rooms manager
        response.setContentType("text/html;charset=UTF-8");
        String userFromSession = SessionUtils.getUsername(request);
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        if (userFromSession == null) {
            //user is not logged in yet
            String userNameFromParameter = request.getParameter(Constants.USERNAME);
            if (userNameFromParameter == null) {
                response.sendRedirect(SIGN_UP_URL);
            } else {
                //normalize the username value
                userNameFromParameter = userNameFromParameter.trim();
                synchronized (this) {
                    if (userManager.isUserExists(userNameFromParameter)) {
                        String errorMessage = "Player name " + userNameFromParameter + " already exists. Please enter a different name.";
                        // username already exists, forward the request back to index.html
                        // with a parameter that indicates that an error should be displayed
                        // the request dispatcher obtained from the servlet context is one that MUST get an absolute path (starting with'/')
                        // and is relative to the web app root
                        // see this link for more details:
                        // http://timjansen.github.io/jarfiller/guide/servlet25/requestdispatcher.xhtml
                        request.setAttribute(Constants.USER_NAME_ERROR, errorMessage);
                        getServletContext().getRequestDispatcher(LOGIN_ERROR_URL).forward(request, response);
                    } else {
                        //add the new user to the users list
                        userManager.addUser(userNameFromParameter);
                        //set the username in a session so it will be available on each request
                        //the true parameter means that if a session object does not exists yet
                        //create a new one
                        request.getSession(true).setAttribute(Constants.USERNAME, userNameFromParameter);

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

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Login Servlet";
    }
}