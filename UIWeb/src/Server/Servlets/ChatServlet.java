package Server.Servlets;

import Server.Chat.ChatManager;
import Server.Chat.SingleChatEntry;
import Server.Constants.Constants;
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

@WebServlet(name = "ChatServlet")
public class ChatServlet extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        ChatManager chatManager = ServletUtils.getRoomsContainer(request.getServletContext()).getRoomByUserName(SessionUtils.getUsername(request)).getChatManager();
        String username = SessionUtils.getUsername(request);
        String action = request.getParameter("action");
        if(action != null) {
            chatManager.resetChat();
        } else {
            if (username == null) {
                response.sendRedirect(request.getContextPath() + "/index.html");
            }

            int chatVersion = ServletUtils.getVersionIntParameter(request, Constants.CHAT_VERSION_PARAMETER);
            if (chatVersion == Constants.INT_PARAMETER_ERROR) {
                return;
            }

            int chatManagerVersion;
            List<SingleChatEntry> chatEntries;
            synchronized (getServletContext()) {
                chatManagerVersion = chatManager.getVersion();
                chatEntries = chatManager.getChatEntries(chatVersion);
            }

            // log and create the response json string
            ChatAndVersion cav = new ChatAndVersion(chatEntries, chatManagerVersion);
            Gson gson = new Gson();
            String jsonResponse = gson.toJson(cav);
            logServerMessage("Server Chat version: " + chatManagerVersion + ", User '" + username + "' Chat version: " + chatVersion);
            logServerMessage(jsonResponse);

            try (PrintWriter out = response.getWriter()) {
                out.print(jsonResponse);
                out.flush();
            }
        }
    }

    private void logServerMessage(String message){
        System.out.println(message);
    }

    class ChatAndVersion {

        final private List<SingleChatEntry> entries;
        final private int version;

        public ChatAndVersion(List<SingleChatEntry> entries, int version) {
            this.entries = entries;
            this.version = version;
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">

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
        return "Short description";
    }// </editor-fold>
}
