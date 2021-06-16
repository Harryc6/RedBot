package com.nco.utils;
import com.nco.pojos.PlayerCharacter;

import java.sql.*;

public class DBUtils {

    public static Connection getConnection() throws SQLException {
        Connection conn = null;
        try {
            String url = ConfigVar.getDBUrl();
            String user = ConfigVar.getDBUser();
            String password = ConfigVar.getDBPassword();

            Class.forName("com.mysql.cj.jdbc.Driver");
            Class.forName("org.postgresql.Driver");

            conn = DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public static boolean doesCharacterExist(String characterName) {
        String sql = "Select * From NCO_PC where character_name = ?";
        try(Connection conn = DBUtils.getConnection(); PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setString(1, characterName);
            try (ResultSet rs = stat.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

}
