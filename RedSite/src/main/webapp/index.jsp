<%@ page import="com.nco.utils.DBUtils" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%! ArrayList<String> players = getPlayerCharacters(); %>
<!DOCTYPE html>
<html>
<%!
    public ArrayList<String> getPlayerCharacters() {
        players = new ArrayList<>();
        try (Connection conn = DBUtils.getConnection(); PreparedStatement stat = conn.prepareStatement("select character_name from nco_pc");
             ResultSet rs = stat.executeQuery()) {
            while (rs.next()) {
                players.add(rs.getString(1));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return players;
    }
%>
<head>
    <title>NCO - RedSite</title>
</head>
<body>
<h1><%= "Search for a player character!" %> </h1>
<%--</h1>--%>
<%--<br/>--%>
<%--<a href="hello-servlet">Hello Servlet</a>--%>

<%--<form method="get" action="/player" id="form1">--%>
<%--    <select mame="Player Characters" id="pc-select">--%>
<%--        <% for (String player : players) { %>--%>
<%--        <option value="<%= player %>" ><%= player %></option>--%>
<%--        <% } %>--%>
<%--    </select>--%>
<%--    <button type="submit" value="search"> Search </button>--%>
<%--</form>--%>

<form action="${pageContext.request.contextPath}/player" method="get" >
    <select name="player-name" id="pc-select">
        <% for (String player : players) { %>
        <option value="<%= player %>" ><%= player %></option>
        <% } %>
    </select>
    <button type="submit" value="search"> Search </button>
</form>

</body>
</html>