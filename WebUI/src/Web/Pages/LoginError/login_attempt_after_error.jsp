<%--
    Document   : index
    Created on : Jan 24, 2012, 6:01:31 AM
    Author     : blecherl
    This is the login JSP for the online chat application
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <%@page import="Utils.*" %>
    <%@ page import="Utils.Constants" %>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Online Chat</title>
<!--        Link the Bootstrap (from twitter) CSS framework in order to use its classes-->
        <link rel="stylesheet" href="../../Common/bootstrap.min.css"/>
        <script src="../../Common/jquery-3.4.1.min.js"></script>
<!--        Link jQuery JavaScript library in order to use the $ (jQuery) method-->
<!--        <script src="script/jquery-2.0.3.min.js"></script>-->
<!--        and\or any other scripts you might need to operate the JSP file behind the scene once it arrives to the client-->
    </head>
    <body>
        <div class="container">
            <% String usernameFromSession = SessionUtils.getPlayerName(request);%>
            <% String usernameFromParameter = request.getParameter(Constants.PLAYER_NAME) != null ? request.getParameter(Constants.PLAYER_NAME) : "";%>
            <% if (usernameFromSession == null) {%>
            <h1>Welcome to conquers , Please sign up to play</h1>
            <br/>
            <h2>Please enter a unique user name:</h2>
            <form method="GET" action="login">
                <input type="text" name="<%=Constants.PLAYER_NAME%>" value="<%=usernameFromParameter%>"/>
                <input type="submit" value="Login"/>
            </form>
            <% Object errorMessage = request.getAttribute(Constants.PLAYER_NAME_ERROR);%>
            <% if (errorMessage != null) {%>
            <span class="bg-danger" style="color:red;"><%=errorMessage%></span>
            <% } %>
            <% } else {%>
            <% }%>
        </div>
    </body>
</html>