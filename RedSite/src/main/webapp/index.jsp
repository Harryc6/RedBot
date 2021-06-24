<%@ page import="com.nco.utils.DBUtils" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="com.nco.pojos.PlayerCharacter" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="java.util.stream.Collectors" %>
<%@ page import="java.lang.reflect.Field" %>
<%@ page import="java.util.List" %>
<%@ page import="com.nco.utils.NCOUtils" %>
<%@ page import="com.nco.utils.StringUtils" %>
<%@ page import="java.lang.reflect.InvocationTargetException" %>
<%@ page import="java.lang.reflect.Method" %>
<%@ page import="com.nco.utils.NumberUtils" %>
<%@ page import="java.sql.Date" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%! PlayerCharacter pc = getUser(); %>
<!DOCTYPE html>
<html>
<%!
    public String getNameFromField(Field field) {
        try {
            String s = "";
            Method method = pc.getClass().getMethod("get" + StringUtils.capitalSnakeToCamelCase(field.getName()));
            if (method.getReturnType() == int.class) {
                return String.valueOf((int) method.invoke(pc));
            } else if (method.getReturnType() == Date.class) {
                return method.invoke(pc).toString();
            } else {
                return (String) method.invoke(pc);
            }
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return "it fell over";
    }

    public PlayerCharacter getUser() {
        try (Connection conn = DBUtils.getConnection()) {
            return new PlayerCharacter(conn, "Errol Clark", true);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public List<Field> getColumns() {
        return Arrays.stream(pc.getClass().getDeclaredFields()).collect(Collectors.toList());
    }
%>
<head>
    <title>JSP - Hello World</title>
</head>
<body>
<h1><%= "Hello World!" %>
</h1>
<br/>
<a href="hello-servlet">Hello Servlet</a>

<h1><%= pc.getCharacterName() %></h1>

<%
    int fontSize;
    for (fontSize = 1; fontSize <= 3; fontSize++){ %>
<font color = "green" size = "<%= fontSize %>">
    JSP Tutorial
</font><br />
<%}%>

<table>
    <tr>
        <th>Column</th>
        <th>Value</th>
    </tr>
    <% for (Field field : getColumns()) { %>
    <tr>
        <th><%= field.getName() %></th>
        <th><%= getNameFromField(field) %></th>
    </tr>
     <% } %>



</body>
</html>