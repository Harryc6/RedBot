package com.nco;

import java.io.*;
import java.util.Date;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet(name = "redServlet", value = "/saytime")
public class RedServlet extends HttpServlet {
    private String message;

    public void init() {
        message = "Hello World!";
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        response.setContentType("text/html");
//
//        // Hello
//        PrintWriter out = response.getWriter();
//        out.println("<html><body>");
//        out.println("<h1>" + message + "</h1>");
//        out.println("</body></html>");
        PrintWriter out = response.getWriter();
        out.print("<html><body><h1 align='center'>" + new Date().toString() + "</h1></body></html>");

    }

    public void destroy() {
    }
}