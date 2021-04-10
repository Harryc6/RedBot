package com.nco.utils;

import java.util.ArrayList;

public class StringUtils {

    public static String[] parseArgsString(String s) {
        String builtS = "";
        boolean isBetweenQuotationMarks = false;
        for (char c : s.toCharArray()) {
            if (c == '"') {
                isBetweenQuotationMarks = !isBetweenQuotationMarks;
            }
            if (c == ' ' && !isBetweenQuotationMarks) {
                c = '~';
            }
            if (c != '"') {
                builtS += c;
            }
        }
        return builtS.split("~");
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

    public static String[] prefixArray(String prefix, String[] array) {
        String[] newArray = new String[array.length + 1];
        newArray[0] = prefix;
        for (int i = 1; i < newArray.length; i++) {
            newArray[i] = array[i-1];
        }
        return newArray;
    }

    public static <T extends Enum<T>> T getEnumFromString(Class<T> c, String string) {
        if( c != null && string != null ) {
            try {
                return Enum.valueOf(c, string.trim().toUpperCase());
            } catch(IllegalArgumentException ignored) {
            }
        }
        return null;
    }
}