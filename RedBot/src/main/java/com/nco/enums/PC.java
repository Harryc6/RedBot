package com.nco.enums;

public enum PC {
    DISCORD_NAME,
    CHARACTER_NAME,
    ROLE,
    ROLE_RANK,
    CREATION_RANK,
    RANK,
    STREET_CRED,
    BANK,
    HEAD_SP,
    BODY_SP,
    IMPROVEMENT_POINTS,
    REPUTATION,
    FAME,
    VAULT,
    DOWNTIME,
    DOWNTIME_SPENT,
    LIFESTYLE,
    RENT,
    MONTHLY_DEBT,
    MONTHLY_DUE,
    GAMES_OVERALL,
    WEEKLY_GAMES,
    JOB_BAN_UNTIL,
    RETIRED_YN;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
