package com.nco.utils;

import com.nco.enums.Skills;

import java.util.Arrays;
import java.util.HashMap;

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

    public static int getFameFromReputation(int reputation) {
        if (reputation == 0 || reputation > famePerReputation.length) {
            return 0;
        } else {
            return famePerReputation[reputation - 1];
        }
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
        return UTCArray;
    }

    public static boolean validUTC(String suppliedUtc) {
        return Arrays.stream(UTCArray).anyMatch(suppliedUtc::equalsIgnoreCase);

    }

    public static String parseAndFormatRole(String suppliedRole) {
        String cleanedRole = suppliedRole.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        return Arrays.stream(roles).filter(role -> cleanedRole.contains(role.toLowerCase())).findFirst().orElse(suppliedRole);
    }

    public static boolean validRole(String suppliedRole) {
        return Arrays.stream(roles).anyMatch(suppliedRole::equalsIgnoreCase);
    }

    public static HashMap<String, Integer> getHousing() {
        HashMap<String, Integer> housing = new HashMap<>();
        housing.put("Street Living", 0);
        housing.put("Street Vehicle", 0);
        housing.put("Cube Hotel", 500);
        housing.put("Cargo Container", 1000);
        housing.put("Studio Apt", 1500);
        housing.put("Two Bedroom Apt", 2500);
        housing.put("Corpo Conapt", 0);
        housing.put("Upscale Conapt", 7500);
        housing.put("Luxury Penthouse", 15000);
        housing.put("Corpo House", 0);
        housing.put("Corpo Mansion", 0);
        housing.put("Starter Rent", 0);
        housing.put("Motto Car", 0);
        housing.put("BL RM Hotel", 250);
        housing.put("BL RM Cargo", 500);
        housing.put("BL RM Studio Apt", 750);
        housing.put("RM Two Bed Apt", 1250);
        housing.put("RM Upscale Conapt", 3570);
        housing.put("RM Penthouse R 1/2", 7500);
        housing.put("RM Penthouse R 1/3", 5000);
        housing.put("RM Penthouse R 1/4", 3750);
        housing.put("RM Penthouse R 1/5", 3000);
        housing.put("NC RM Exec Conapt", 1);
        housing.put("NC RM Exec House ", 1);
        housing.put("NC RM Exec Mansion", 1);
        housing.put("NC RM Cargo Con", 1);
        housing.put("NC RM Studio Apt", 1);
        housing.put("NC RM Two Bed Apt", 1);
        housing.put("NC RM Conapt", 1);
        housing.put("NC RM Penthouse", 1);
        housing.put("NC RM House", 1);
        housing.put("NC RM Mansion", 1);
        housing.put("Owner Cargo Container", 0);
        housing.put("Owner Studio Apt", 0);
        housing.put("Owner Two Bed Apt", 0);
        housing.put("Owner Upscale Conapt", 0);
        housing.put("Owner Lux Pent", 0);
        housing.put("Owner Lux House", 0);
        housing.put("Owner Lux Mansion", 0);
        return housing;
    }

    public static HashMap<String, Integer> getLifestyle() {
        HashMap<String, Integer> lifestyle = new HashMap<>();
        lifestyle.put("Kibble", 100);
        lifestyle.put("Generic Prepak", 300);
        lifestyle.put("Good Prepak", 600);
        lifestyle.put("Fresh Food", 1500);
        lifestyle.put("Starter Lifestyle", 0);
        return lifestyle;
    }

    public static int getSkillLevelUpIP(Skills skill, int skillLevel) {
        return (skillLevel + 1) * (20 * skill.difficulty);
    }
}
