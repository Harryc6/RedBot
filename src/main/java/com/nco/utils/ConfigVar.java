package com.nco.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigVar {

    private static final String PROPERTIES_URL = "config.properties";

    public static String getDBUser() {
        return getProperties().getProperty("DBUser");
    }

    public static String getDBPassword() {
        return getProperties().getProperty("DBPassword");
    }

    public static String getDBUrl() {
        return getProperties().getProperty("DBUrl");
    }

    public static String getDiscordToken() {
        return getProperties().getProperty("DiscordToken");
    }

    private static Properties getProperties() {
        Properties prop = new Properties();
        try {
            FileInputStream ip = new FileInputStream(PROPERTIES_URL);
            prop.load(ip);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prop;
    }

}
