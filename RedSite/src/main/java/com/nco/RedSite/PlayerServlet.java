package com.nco.RedSite;

import com.nco.pojos.PlayerCharacter;
import com.nco.utils.DBUtils;
import com.nco.utils.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(name = "playerServlet", value = "/player")
public class PlayerServlet extends HttpServlet {

    public PlayerServlet() {
        super();
    }

    public void init() {
        String message = "Hello World!";
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");

        String player = req.getParameter("player-name");
        PrintWriter out = resp.getWriter();
        PlayerCharacter pc = getUser(player);
        out.println("<h1> " + player + " </h1>");

        out.println("<table>");
        out.println("<tr style=\"background: radial-gradient(#461c1c, #ff7e7e);\">");
        for (Field field : getColumns(pc)) {
            out.println("<th>" + StringUtils.camelToFormal(field.getName()) + "</th>");
        }
        out.println("</tr>");
        out.println("<tr style=\"background: #f15858;\">");
        for (Field field : getColumns(pc)) {
            out.println("<th>" + getValueFromField(pc, field) + "</th>");
        }
        out.println("</tr></tbody>");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    public String getValueFromField(PlayerCharacter pc, Field field) {
        try {
            Method method = pc.getClass().getMethod("get" + StringUtils.capitalSnakeToCamelCase(field.getName()));
            if (method.getReturnType() == int.class) {
                return String.valueOf((int) method.invoke(pc));
            } else {
                return (String) method.invoke(pc);
            }
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
//            e.printStackTrace();
        }
        return null;
    }


    public List<Field> getColumns(PlayerCharacter pc) {
        return Arrays.stream(pc.getClass().getDeclaredFields()).collect(Collectors.toList());
    }

    public PlayerCharacter getUser(String player) {
        try (Connection conn = DBUtils.getConnection()) {
            return new PlayerCharacter(conn, player, true);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

//    <table style="border-width: thin">
//    <tr>
//        <th>Column</th>
//        <th>Value</th>
//    </tr>
//    <% for (Field field : getColumns()) { if (getNameFromField(field) != null) {%>
//    <tr>
//        <th><%= field.getName() %></th>
//        <th><%= getNameFromField(field) %></th>
//    </tr>
//     <% }} %>
//</table>


}
