package com.nco.utils;

public class NumberUtils {

//    private static String[] fameReputation = {}

    public static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static int asPositive(String str) {
        int i = Integer.parseInt(str);
        return (i < 0 ? i * -1 : i);
    }

    public static int asNegative(String str) {
        int i = Integer.parseInt(str);
        return (i < 0 ? i : i * -1);
    }

//    public  static int reputationFromFame() {
//
//    }
}
