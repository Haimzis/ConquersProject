package Server.Servlets;

import Server.Constants.Constants;
import Server.Users.UserManager;
import Server.Utils.ServletUtils;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

@WebServlet(name = "LoggedUsersServlet")
public class LoggedUsersServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        int actionValue = -1;

        if (action.equals("getLoggedUsername")) {
            actionValue = 0;
        }
        if (action.equals("getLoggedUsers")) {
            actionValue = 1;
        }

        switch (actionValue) {
            case 0:
                this.getLoggedUsername(request, response);
                break;
            case 1:
                this.getUsersListAction(request, response);
                break;
        }
    }

    private void getUsersListAction(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        Set<String> usersList = userManager.getUsers();
        String json = gson.toJson(usersList);
        out.println(json);
        out.flush();
    }

    private void getLoggedUsername(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        String CurrentUsername= request.getSession(false).getAttribute(Constants.USERNAME).toString();
        out.println(gson.toJson(CurrentUsername));
        out.println();
    }
}


