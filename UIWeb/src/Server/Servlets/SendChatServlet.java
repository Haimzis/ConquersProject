package Server.Servlets;

import Server.Chat.ChatManager;
import Server.Constants.Constants;
import Server.Utils.ServletUtils;
import Server.Utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "SendChatServlet" , urlPatterns = {"/Pages/GameRoom/sendChat"})
public class SendChatServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ChatManager chatManager = ServletUtils.getRoomsContainer(request.getServletContext()).getRoomByUserName(SessionUtils.getUsername(request)).getChatManager();
        String username = SessionUtils.getUsername(request);
        if (username == null) {
            response.sendRedirect(request.getContextPath() + "/index.html");
        }

        String userChatString = request.getParameter(Constants.CHAT_PARAMETER);
        if (userChatString != null && !userChatString.isEmpty()) {
            logServerMessage("Adding chat string from " + username + ": " + userChatString);
            synchronized (getServletContext()) {
                chatManager.addChatString(userChatString, username);
            }
        }
    }

    private void logServerMessage(String message) {
        System.out.println(message);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }


    @Override
    public String getServletInfo() {
        return "Short description";
    }

}
