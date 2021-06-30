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
        if (array.length == 1 && array[0].isEmpty()) {
            return new String[] { prefix };
        }
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
            if (s != null && !s.isEmpty()) {
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

    public static String camelToFormal(String s) {
        return s.substring(0,1).toUpperCase() + s.substring(1).replaceAll("([A-Z])", " $1");
    }

    public static String capitalizeWords(String s) {
        char[] chars = s.toLowerCase().toCharArray();
        boolean found = false;
        for (int i = 0; i < chars.length; i++) {
            if (!found && Character.isLetter(chars[i])) {
                chars[i] = Character.toUpperCase(chars[i]);
                found = true;
            } else if (Character.isWhitespace(chars[i]) || chars[i]=='.' || chars[i]=='\'') {
                found = false;
            }
        }
        return String.valueOf(chars);
    }

    public static String superscript(String s) {
        s = s.replaceAll("0", "⁰");
        s = s.replaceAll("1", "¹");
        s = s.replaceAll("2", "²");
        s = s.replaceAll("3", "³");
        s = s.replaceAll("4", "⁴");
        s = s.replaceAll("5", "⁵");
        s = s.replaceAll("6", "⁶");
        s = s.replaceAll("7", "⁷");
        s = s.replaceAll("8", "⁸");
        s = s.replaceAll("9", "⁹");
        return s;
    }

    public static String subscript(String str) {
        str = str.replaceAll("0", "₀");
        str = str.replaceAll("1", "₁");
        str = str.replaceAll("2", "₂");
        str = str.replaceAll("3", "₃");
        str = str.replaceAll("4", "₄");
        str = str.replaceAll("5", "₅");
        str = str.replaceAll("6", "₆");
        str = str.replaceAll("7", "₇");
        str = str.replaceAll("8", "₈");
        str = str.replaceAll("9", "₉");
        return str;
    }

}
