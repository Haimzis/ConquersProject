package Server.Servlets;

import Server.Constants.Constants;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "LoggedUserName")
public class LoggedUserName extends HttpServlet {

    private String processRequest(HttpServletRequest request){
       return request.getSession(false).getAttribute(Constants.USERNAME).toString();
    }
    @Override
    protected void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
        PrintWriter writer = response.getWriter();
        writer.println(processRequest(request));
    }
}
