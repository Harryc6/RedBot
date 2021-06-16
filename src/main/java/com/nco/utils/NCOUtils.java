package com.nco.utils;

public class NCOUtils {

    static int[] famePerReputation = {10, 25, 45, 70, 100, 135, 175, 220, 270, 335};

    public static int getReputationFromFame(int fame) {
        for (int i = 0; i < famePerReputation.length; i++) {
            if (famePerReputation[i] > fame) {
                return i;
            }
        }
        return famePerReputation.length;
    }

    public static int getStartingStreetCredFromRank(String role) {
        return 0;
    }
}
