package com.nco.utils;

public class NCOUtils {

    static int[] famePerReputation = {10, 25, 45, 70, 100, 135, 175, 220, 270, 335};
    static String[] ranks = {"E-Sheep", "Poser", "Little Runner", "Gato", "Choomba", "Street Samurai", "Hardwired", "Silverhand", "Blackhand", "????????"};
    static int[] streetCredPerRank = {0, 12, 24, 36, 48, 60, 72, 84, 96, 108};

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
}
