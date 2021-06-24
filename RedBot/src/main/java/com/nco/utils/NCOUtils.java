package com.nco.utils;

import jdk.nashorn.internal.runtime.regexp.joni.Regex;

import javax.annotation.RegEx;
import java.util.Arrays;
import java.util.Locale;

public class NCOUtils {

    private static final String[] fashion = {"Bag Lady Chic", "Gang Colors", "Generic Chic", "Bohemian Folksy", "Leisurewear Comfort", "Nomad Leathers", "Asia Pop", "Urban Flash", "Businesswear", "High Fashion"};
    private static final int[] famePerReputation = {10, 25, 45, 70, 100, 135, 175, 220, 270, 335};
    private static final String[] ranks = {"E-Sheep", "Poser", "Little Runner", "Gato", "Choomba", "Street Samurai", "Hardwired", "Silverhand", "Blackhand", "????????"};
    private static final int[] streetCredPerRank = {0, 12, 24, 36, 48, 60, 72, 84, 96, 108};
    private final static String[] UTCArray = { "UTC-12", "UTC-11", "UTC-10", "UTC-9:30", "UTC-9", "UTC-8", "UTC-7", "UTC-6", "UTC-5", "UTC-4", "UTC-3:30", "UTC-3", "UTC-2", "UTC-1", "UTC+0", "UTC+1", "UTC+2", "UTC+3", "UTC+3:30", "UTC+4", "UTC+4:30", "UTC+5", "UTC+5:30", "UTC+5:45", "UTC+6", "UTC+6:30", "UTC+7", "UTC+8", "UTC+8:45", "UTC+9", "UTC+9:30", "UTC+10", "UTC+10:30", "UTC+11", "UTC+12", "UTC+12:45", "UTC+13", "UTC+14" };
    private final static String[] roles = {"Rockerboy", "Solo", "Netrunner", "Tech", "Medtech", "Media", "Exec", "Lawman", "Fixer", "Nomad"};

    public static int getReputationFromFame(int fame) {
        for (int i = 0; i < famePerReputation.length; i++) {
            if (famePerReputation[i] > fame) {
                return i;
            }
        }
        return famePerReputation.length;
    }

    public static int getStartingStreetCredFromRank(String startingRank) {
        for (int i = 0; i < ranks.length; i++) {
            if (ranks[i].equalsIgnoreCase(startingRank)) {
                return streetCredPerRank[i];
            }
        }
        return 0;
    }

    public static boolean validRank(String suppliedRank) {
        return Arrays.stream(ranks).anyMatch(suppliedRank::equalsIgnoreCase);
    }

    public static String getCorrectRankCase(String suppliedRank) {
        for (String rank : ranks) {
            if (rank.equalsIgnoreCase(suppliedRank)) {
                return rank;
            }
        }
        return suppliedRank;
    }

    public static String[] getUTCArray() {
        return getUTCArray();
    }

    public static boolean validUTC(String suppliedUtc) {
        return Arrays.stream(UTCArray).anyMatch(suppliedUtc::equalsIgnoreCase);

    }

    public static String parseAndFormatRole(String suppliedRole) {
        String cleanedRole = suppliedRole.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        return Arrays.stream(roles).filter(role -> cleanedRole.contains(role.toLowerCase())).findFirst().orElse(suppliedRole);
    }

}
