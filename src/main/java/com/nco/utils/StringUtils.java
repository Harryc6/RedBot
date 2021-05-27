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
            if (c != '"' && (c != '~' || builtS.charAt(builtS.length() - 1) != '~')) {
                builtS += c;
            }
        }
        return builtS.split("~");
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

    public static String checkNull(String string) {
        return string == null ? "" : string;
    }
}
