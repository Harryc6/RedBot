package com.nco.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtils {

    public static Connection getConnection() throws SQLException {
        Connection conn = null;
        try {
            String url = ConfigVar.getDBUrl();
            String user = ConfigVar.getDBUser();
            String password = ConfigVar.getDBPassword();

            Class.forName("com.mysql.cj.jdbc.Driver");

            conn = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return conn;


    }

}
