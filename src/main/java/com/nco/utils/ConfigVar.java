package com.nco.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigVar {

    public static String getDBUser() {
        return System.getenv().get("DBUser");
    }

    public static String getDBPassword() {
        return System.getenv().get("DBPassword");
    }

    public static String getDBUrl() {
        return System.getenv().get("DBUrl");
    }

    public static String getDiscordToken() {
        return System.getenv().get("DiscordToken");
    }

}
