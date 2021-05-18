package com.nco.utils;
import com.nco.pojos.PlayerCharacter;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import com.nco.entities.NcoPcEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.dialect.MySQL5Dialect;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.query.Query;

import java.sql.*;
import java.util.List;

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

    public static ResultSet getCharactersResultsSet(String characterName) {
        ResultSet rs = null;
        String sql = "Select * From NCO_PC where CharacterName = ?";
        try(Connection conn = DBUtils.getConnection(); PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setString(1, characterName);
            return stat.executeQuery();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return rs;
    }

    public static void msain(String[] args) {
//        try {
//            Class.forName("com.mysql.cj.jdbc.Driver");
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
        StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml").build();
        Metadata meta = new MetadataSources(ssr).getMetadataBuilder().build();

        SessionFactory factory = meta.getSessionFactoryBuilder().build();
        Session session = factory.openSession();
        Transaction t = session.beginTransaction();
//        Query query = session.getNamedQuery("CharacterByCharacterName");
//        query.setParameter("characterName", "Errol Clark");
//        List<NcoPcEntity> resultList = (List<NcoPcEntity>) query.getResultList();
        Query<NcoPcEntity> query = session.createQuery("from NcoPcEntity where characterName = :characterName and retiredYn = 'N'", NcoPcEntity.class);
        query.setParameter("characterName","Errol Clark");
        List<NcoPcEntity> resultList = query.getResultList();
        if (resultList.size() == 1) {
            System.out.println("Found an SQL Item!!!");
        }
        for (NcoPcEntity ncoPcEntity : resultList) {
            System.out.println("The Bank Balance of " + ncoPcEntity.getCharacterName() + " is " + ncoPcEntity.getBank());
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

//        NcoPcEntity e1 = new NcoPcEntity();
//        e1.setDiscordName("WhoCares?");
//        e1.setCharacterName("Errol Test");;
//        e1.setRank("Solo");
//        e1.setBodyScore(7);
//        e1.setWillScore(7);
//        e1.setHumanity(7);
//        e1.setBank(0);
//        e1.setReputation(0);
//        e1.setVault(1);
//        e1.setTtCoverage("No Coverage");
//        e1.setWeeklyGames(0);
//        session.createQuery()

//        session.save(e1);
//        t.commit();
        System.out.println("successfully saved");
        factory.close();
        session.close();
    }

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        StandardServiceRegistryBuilder configure = new StandardServiceRegistryBuilder();
        configure.applySetting("connection.url", ConfigVar.getDBUrl());
        configure.applySetting("connection.username", ConfigVar.getDBUser());
        configure.applySetting("connection.password", ConfigVar.getDBPassword());
        configure.applySetting("hibernate.dialect", MySQL5Dialect.class.getName());
        configure.configure("hibernate.cfg.xml");
        Metadata meta = new MetadataSources(configure.build()).getMetadataBuilder().build();
        SessionFactory factory = meta.getSessionFactoryBuilder().build();
        Session session = factory.openSession();

        Query<NcoPcEntity> query = session.createQuery("from NcoPcEntity where characterName = :characterName and retiredYn = 'N'", NcoPcEntity.class);
        query.setParameter("characterName","Errol Clark");
        List<NcoPcEntity> resultList = query.getResultList();
        if (resultList.size() == 1) {
            System.out.println("Found an SQL Item!!!");
        }
        for (NcoPcEntity ncoPcEntity : resultList) {
            System.out.println("The Bank Balance of " + ncoPcEntity.getCharacterName() + " is " + ncoPcEntity.getBank());
        }
        factory.close();
        session.close();
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
