package com.nco.utils;

public class ConfigVar {

    public static String getDBUser() {
        return System.getenv().get("DB_USERNAME");
    }

    public static String getDBPassword() {
        return System.getenv().get("DB_PASSWORD");
    }

    public static String getDBUrl() {
        return "jdbc:" + System.getenv("DB_CONNECTION") + "://" + System.getenv().get("DB_HOST") + ":"
                + System.getenv().get("DB_PORT") + "/" + System.getenv().get("DB_DATABASE");
    }

    public static String getDiscordToken() {
        return System.getenv().get("DISCORD_TOKEN");
    }

}
