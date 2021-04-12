package com.nco.utils;

public class NumberUtils {

    public static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public static int rollDice(int dice, int diceSize, boolean half) {
        double d = diceSize;
        if (half) {
            diceSize = (int) Math.ceil(d / 2);
        }
        return getRandomNumber(dice , dice * diceSize);
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

}
