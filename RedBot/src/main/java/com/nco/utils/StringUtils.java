package com.nco.utils;

import java.util.ArrayList;
import java.util.Arrays;

public class StringUtils {

    public static String[] parseArgsString(String s) {
        StringBuilder builtS = new StringBuilder();
        boolean isBetweenQuotationMarks = false;
        for (char c : s.toCharArray()) {
            if (c == '"') {
                isBetweenQuotationMarks = !isBetweenQuotationMarks;
            }
            if (c == ' ' && !isBetweenQuotationMarks) {
                c = '~';
            }
            if (c != '"' && (c != '~' || builtS.charAt(builtS.length() - 1) != '~')) {
                builtS.append(c);
            }
        }
        return builtS.toString().split("~");
    }

    public static String[] prefixArray(String prefix, String[] array) {
        String[] newArray = new String[array.length + 1];
        newArray[0] = prefix;
        System.arraycopy(array, 0, newArray, 1, newArray.length - 1);
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

    public static String parseArray(String[] array) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (String s : array) {
            if (!s.isEmpty()) {
                sb.append(s).append(", ");
            }
        }
        if (sb.length() > 1) {
            sb.replace(sb.lastIndexOf(", "), sb.lastIndexOf(", ") + 2, "");
        }
        return sb.append("]").toString();
    }

    public static String camelToSnakeCase(String s) {
        return s.replaceAll("([A-Z])", "_$1").toLowerCase();
    }

    public static String capitalSnakeToCamelCase(String s) {
        return snakeToCamelCase(s).substring(0, 1).toUpperCase() + snakeToCamelCase(s).substring(1);
    }

    public static String snakeToCamelCase(String s) {
        while (s.contains("_")) {
            s = s.replaceFirst("_[a-z]", String.valueOf(Character.toUpperCase(s.charAt(s.indexOf("_") + 1))));
        }
        return s;

    }
}
