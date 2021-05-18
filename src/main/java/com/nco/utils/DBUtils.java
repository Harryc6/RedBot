package com.nco.utils;
import com.nco.pojos.PlayerCharacter;
import org.jetbrains.annotations.Nullable;

import java.sql.*;

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

    public static boolean doesCharacterExist(String characterName) {
        String sql = "Select * From NCO_PC where CharacterName = ?";
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

    public static PlayerCharacter getCharacter(String characterName) {
        String sql = "Select * From NCO_PC where CharacterName = ?";
        return getPlayerCharacter(characterName, sql);
    }

    public static PlayerCharacter getCharacterByUser(String user) {
        String sql = "Select * From NCO_PC where DiscordName = ? AND RetiredYN = 'N'";
        return getPlayerCharacter(user, sql);
    }

    private static PlayerCharacter getPlayerCharacter(String arg, String sql) {
        try(Connection conn = DBUtils.getConnection(); PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setString(1, arg);
            try (ResultSet rs = stat.executeQuery()) {
                return new PlayerCharacter(rs);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }
}
