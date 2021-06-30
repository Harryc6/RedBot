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
    INFLUENCE_POINTS,
    REPUTATION,
    FAME,
    VAULT,
    DOWNTIME,
    LIFESTYLE,
    RENT,
    GAMES_OVERALL,
    WEEKLY_GAMES,
    JOB_BAN_UNTIL,
    SURGERY,
    PHARMACEUTICALS,
    CRYOSYSTEM_OPERATION,
    FIELD_EXPERTISE,
    UPGRADE_EXPERTISE,
    FABRICATION_EXPERTISE,
    INVENTION_EXPERTISE,
    RETIRED_YN;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
