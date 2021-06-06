package com.nco.pojos;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class PlayerCharacter {
    private String discordName;
    private String characterName;
    private String role;
    private int streetCred;
    private String rank;
    private int bodyScore;
    private int willScore;
    private int maxHP;
    private int currentHP;
    private int headSP;
    private int bodySP;
    private int maxHumanity;
    private int humanity;
    private int bank;
    private int influencePoints;
    private int reputation;
    private int fame;
    private int vault;
    private int downtime;
    private String lifestyle;
    private String rent;
    private String payDues;
    private String addicted;
    private int hospitalDebt;
    private String ttCoverage;
    private String ttcLevel;
    private String timeZone;
    private int gamesOverall;
    private int weeklyGames;
    private Date jobBanUntil;
    private Timestamp createdOn;
    private String createdBy;
    private String retiredYN;

    public PlayerCharacter(ResultSet rs) throws SQLException {
        discordName = rs.getString("discord_name");
        characterName = rs.getString("character_name");
        role = rs.getString("role");
        streetCred = rs.getInt("street_cred");
        rank = rs.getString("rank");
        bodyScore = rs.getInt("body_score");
        willScore = rs.getInt("will_score");
        maxHP = rs.getInt("max_hp");
        currentHP = rs.getInt("current_hp");
        headSP = rs.getInt("head_sp");
        bodySP = rs.getInt("body_sp");
        maxHumanity = rs.getInt("max_humanity");
        humanity = rs.getInt("humanity");
        bank = rs.getInt("bank");
        influencePoints = rs.getInt("influence_points");
        reputation = rs.getInt("reputation");
        fame = rs.getInt("fame");
        vault = rs.getInt("vault");
        downtime = rs.getInt("downtime");
        lifestyle = rs.getString("lifestyle");
        rent = rs.getString("rent");
        payDues = rs.getString("pay_dues");
        addicted = rs.getString("addicted");
        hospitalDebt = rs.getInt("hospital_debt");
        ttCoverage = rs.getString("tt_coverage");
        ttcLevel = rs.getString("ttc_level");
        timeZone = rs.getString("time_zone");
        gamesOverall = rs.getInt("games_overall");
        weeklyGames = rs.getInt("weekly_games");
        jobBanUntil = rs.getDate("job_ban_until");
        createdOn = rs.getTimestamp("created_on");
        createdBy = rs.getString("created_by");
        retiredYN = rs.getString("retired_yn");
    }

    public String getDiscordName() {
        return discordName;
    }

    public String getCharacterName() {
        return characterName;
    }

    public String getRole() {
        return role;
    }

    public int getStreetCred() {
        return streetCred;
    }

    public String getRank() {
        return rank;
    }

    public int getBodyScore() {
        return bodyScore;
    }

    public int getWillScore() {
        return willScore;
    }

    public int getMaxHP() {
        return maxHP;
    }

    public int getCurrentHP() {
        return currentHP;
    }

    public int getHeadSP() {
        return headSP;
    }

    public int getBodySP() {
        return bodySP;
    }

    public int getMaxHumanity() {
        return maxHumanity;
    }

    public int getHumanity() {
        return humanity;
    }

    public int getBank() {
        return bank;
    }

    public int getInfluencePoints() {
        return influencePoints;
    }

    public int getReputation() {
        return reputation;
    }

    public int getFame() {
        return fame;
    }

    public int getVault() {
        return vault;
    }

    public int getDowntime() {
        return downtime;
    }

    public String getLifestyle() {
        return lifestyle;
    }

    public String getRent() {
        return rent;
    }

    public String getPayDues() {
        return payDues;
    }

    public String getAddicted() {
        return addicted;
    }

    public int getHospitalDebt() {
        return hospitalDebt;
    }

    public String getTtCoverage() {
        return ttCoverage;
    }

    public String getTtcLevel() {
        return ttcLevel;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public int getGamesOverall() {
        return gamesOverall;
    }

    public int getWeeklyGames() {
        return weeklyGames;
    }

    public Date getJobBanUntil() {
        return jobBanUntil;
    }

    public Timestamp getCreatedOn() {
        return createdOn;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getRetiredYN() {
        return retiredYN;
    }

}
