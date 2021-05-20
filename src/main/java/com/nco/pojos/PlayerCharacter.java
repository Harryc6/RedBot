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
    private int salvagedEuros;
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
        salvagedEuros = rs.getInt("salvaged_euros");
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

    public void setDiscordName(String discordName) {
        this.discordName = discordName;
    }

    public String getCharacterName() {
        return characterName;
    }

    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getStreetCred() {
        return streetCred;
    }

    public void setStreetCred(int streetCred) {
        this.streetCred = streetCred;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public int getBodyScore() {
        return bodyScore;
    }

    public void setBodyScore(int bodyScore) {
        this.bodyScore = bodyScore;
    }

    public int getWillScore() {
        return willScore;
    }

    public void setWillScore(int willScore) {
        this.willScore = willScore;
    }

    public int getMaxHP() {
        return maxHP;
    }

    public void setMaxHP(int maxHP) {
        this.maxHP = maxHP;
    }

    public int getCurrentHP() {
        return currentHP;
    }

    public void setCurrentHP(int currentHP) {
        this.currentHP = currentHP;
    }

    public int getHeadSP() {
        return headSP;
    }

    public void setHeadSP(int headSP) {
        this.headSP = headSP;
    }

    public int getBodySP() {
        return bodySP;
    }

    public void setBodySP(int bodySP) {
        this.bodySP = bodySP;
    }

    public int getMaxHumanity() {
        return maxHumanity;
    }

    public void setMaxHumanity(int maxHumanity) {
        this.maxHumanity = maxHumanity;
    }

    public int getHumanity() {
        return humanity;
    }

    public void setHumanity(int humanity) {
        this.humanity = humanity;
    }

    public int getBank() {
        return bank;
    }

    public void setBank(int bank) {
        this.bank = bank;
    }

    public int getInfluencePoints() {
        return influencePoints;
    }

    public void setInfluencePoints(int influencePoints) {
        this.influencePoints = influencePoints;
    }

    public int getReputation() {
        return reputation;
    }

    public void setReputation(int reputation) {
        this.reputation = reputation;
    }

    public int getFame() {
        return fame;
    }

    public void setFame(int fame) {
        this.fame = fame;
    }

    public int getVault() {
        return vault;
    }

    public void setVault(int vault) {
        this.vault = vault;
    }

    public int getDowntime() {
        return downtime;
    }

    public void setDowntime(int downtime) {
        this.downtime = downtime;
    }

    public String getLifestyle() {
        return lifestyle;
    }

    public void setLifestyle(String lifestyle) {
        this.lifestyle = lifestyle;
    }

    public String getRent() {
        return rent;
    }

    public void setRent(String rent) {
        this.rent = rent;
    }

    public String getPayDues() {
        return payDues;
    }

    public void setPayDues(String payDues) {
        this.payDues = payDues;
    }

    public String getAddicted() {
        return addicted;
    }

    public void setAddicted(String addicted) {
        this.addicted = addicted;
    }

    public int getHospitalDebt() {
        return hospitalDebt;
    }

    public void setHospitalDebt(int hospitalDebt) {
        this.hospitalDebt = hospitalDebt;
    }

    public String getTtCoverage() {
        return ttCoverage;
    }

    public void setTtCoverage(String ttCoverage) {
        this.ttCoverage = ttCoverage;
    }

    public String getTtcLevel() {
        return ttcLevel;
    }

    public void setTtcLevel(String ttcLevel) {
        this.ttcLevel = ttcLevel;
    }

    public int getSalvagedEuros() {
        return salvagedEuros;
    }

    public void setSalvagedEuros(int salvagedEuros) {
        this.salvagedEuros = salvagedEuros;
    }

    public int getGamesOverall() {
        return gamesOverall;
    }

    public void setGamesOverall(int gamesOverall) {
        this.gamesOverall = gamesOverall;
    }

    public int getWeeklyGames() {
        return weeklyGames;
    }

    public void setWeeklyGames(int weeklyGames) {
        this.weeklyGames = weeklyGames;
    }

    public Date getJobBanUntil() {
        return jobBanUntil;
    }

    public void setJobBanUntil(Date jobBanUntil) {
        this.jobBanUntil = jobBanUntil;
    }

    public Timestamp getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Timestamp createdOn) {
        this.createdOn = createdOn;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getRetiredYN() {
        return retiredYN;
    }

    public void setRetiredYN(String retiredYN) {
        this.retiredYN = retiredYN;
    }

}
