package com.nco.pojos;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class PlayerCharacter {
    private String DiscordName;
    private String CharacterName;
    private String Role;
    private int StreetCred;
    private String Rank;
    private int BodyScore;
    private int WillScore;
    private int MaxHP;
    private int CurrentHP;
    private int HeadSP;
    private int BodySP;
    private int MaxHumanity;
    private int Humanity;
    private int Bank;
    private int InfluencePoints;
    private int Reputation;
    private int Fame;
    private int Vault;
    private int DownTime;
    private String Lifestyle;
    private String Rent;
    private String PayDues;
    private String Addicted;
    private int HospitalDebt;
    private String TTCoverage;
    private String TTCLevel;
    private int SalvagedEuros;
    private int GamesOverall;
    private int WeeklyGames;
    private Date JobBanUntil;
    private Timestamp CreatedOn;
    private String CreatedBy;
    private String RetiredYN;

    public PlayerCharacter(ResultSet rs) throws SQLException {
        DiscordName = rs.getString("discordName");
        CharacterName = rs.getString("characterName");
        Role = rs.getString("role");
        StreetCred = rs.getInt("streetCred");
        Rank = rs.getString("rank");
        BodyScore = rs.getInt("bodyScore");
        WillScore = rs.getInt("willScore");
        MaxHP = rs.getInt("maxHP");
        CurrentHP = rs.getInt("currentHP");
        HeadSP = rs.getInt("headSP");
        BodySP = rs.getInt("bodySP");
        MaxHumanity = rs.getInt("maxHumanity");
        Humanity = rs.getInt("humanity");
        Bank = rs.getInt("bank");
        InfluencePoints = rs.getInt("influencePoints");
        Reputation = rs.getInt("reputation");
        Fame = rs.getInt("fame");
        Vault = rs.getInt("vault");
        DownTime = rs.getInt("downTime");
        Lifestyle = rs.getString("lifestyle");
        Rent = rs.getString("rent");
        PayDues = rs.getString("payDues");
        Addicted = rs.getString("addicted");
        HospitalDebt = rs.getInt("hospitalDebt");
        TTCoverage = rs.getString("TTCoverage");
        TTCLevel = rs.getString("TTCLevel");
        SalvagedEuros = rs.getInt("salvagedEuros");
        GamesOverall = rs.getInt("gamesOverall");
        WeeklyGames = rs.getInt("weeklyGames");
        JobBanUntil = rs.getDate("jobBanUntil");
        CreatedOn = rs.getTimestamp("createdOn");
        CreatedBy = rs.getString("createdBy");
        RetiredYN = rs.getString("retiredYN");
    }

    public String getDiscordName() {
        return DiscordName;
    }

    public void setDiscordName(String discordName) {
        DiscordName = discordName;
    }

    public String getCharacterName() {
        return CharacterName;
    }

    public void setCharacterName(String characterName) {
        CharacterName = characterName;
    }

    public String getRole() {
        return Role;
    }

    public void setRole(String role) {
        Role = role;
    }

    public int getStreetCred() {
        return StreetCred;
    }

    public void setStreetCred(int streetCred) {
        StreetCred = streetCred;
    }

    public String getRank() {
        return Rank;
    }

    public void setRank(String rank) {
        Rank = rank;
    }

    public int getBodyScore() {
        return BodyScore;
    }

    public void setBodyScore(int bodyScore) {
        BodyScore = bodyScore;
    }

    public int getWillScore() {
        return WillScore;
    }

    public void setWillScore(int willScore) {
        WillScore = willScore;
    }

    public int getMaxHP() {
        return MaxHP;
    }

    public void setMaxHP(int maxHP) {
        MaxHP = maxHP;
    }

    public int getCurrentHP() {
        return CurrentHP;
    }

    public void setCurrentHP(int currentHP) {
        CurrentHP = currentHP;
    }

    public int getHeadSP() {
        return HeadSP;
    }

    public void setHeadSP(int headSP) {
        HeadSP = headSP;
    }

    public int getBodySP() {
        return BodySP;
    }

    public void setBodySP(int bodySP) {
        BodySP = bodySP;
    }

    public int getMaxHumanity() {
        return MaxHumanity;
    }

    public void setMaxHumanity(int maxHumanity) {
        MaxHumanity = maxHumanity;
    }

    public int getHumanity() {
        return Humanity;
    }

    public void setHumanity(int humanity) {
        Humanity = humanity;
    }

    public int getBank() {
        return Bank;
    }

    public void setBank(int bank) {
        Bank = bank;
    }

    public int getInfluencePoints() {
        return InfluencePoints;
    }

    public void setInfluencePoints(int influencePoints) {
        InfluencePoints = influencePoints;
    }

    public int getReputation() {
        return Reputation;
    }

    public void setReputation(int reputation) {
        Reputation = reputation;
    }

    public int getFame() {
        return Fame;
    }

    public void setFame(int fame) {
        Fame = fame;
    }

    public int getVault() {
        return Vault;
    }

    public void setVault(int vault) {
        Vault = vault;
    }

    public int getDownTime() {
        return DownTime;
    }

    public void setDownTime(int downTime) {
        DownTime = downTime;
    }

    public String getLifestyle() {
        return Lifestyle;
    }

    public void setLifestyle(String lifestyle) {
        Lifestyle = lifestyle;
    }

    public String getRent() {
        return Rent;
    }

    public void setRent(String rent) {
        Rent = rent;
    }

    public String getPayDues() {
        return PayDues;
    }

    public void setPayDues(String payDues) {
        PayDues = payDues;
    }

    public String getAddicted() {
        return Addicted;
    }

    public void setAddicted(String addicted) {
        Addicted = addicted;
    }

    public int getHospitalDebt() {
        return HospitalDebt;
    }

    public void setHospitalDebt(int hospitalDebt) {
        HospitalDebt = hospitalDebt;
    }

    public String getTTCoverage() {
        return TTCoverage;
    }

    public void setTTCoverage(String TTCoverage) {
        this.TTCoverage = TTCoverage;
    }

    public String getTTCLevel() {
        return TTCLevel;
    }

    public void setTTCLevel(String TTCLevel) {
        this.TTCLevel = TTCLevel;
    }

    public int getSalvagedEuros() {
        return SalvagedEuros;
    }

    public void setSalvagedEuros(int salvagedEuros) {
        SalvagedEuros = salvagedEuros;
    }

    public int getGamesOverall() {
        return GamesOverall;
    }

    public void setGamesOverall(int gamesOverall) {
        GamesOverall = gamesOverall;
    }

    public int getWeeklyGames() {
        return WeeklyGames;
    }

    public void setWeeklyGames(int weeklyGames) {
        WeeklyGames = weeklyGames;
    }

    public Date getJobBanUntil() {
        return JobBanUntil;
    }

    public void setJobBanUntil(Date jobBanUntil) {
        JobBanUntil = jobBanUntil;
    }

    public Timestamp getCreatedOn() {
        return CreatedOn;
    }

    public void setCreatedOn(Timestamp createdOn) {
        CreatedOn = createdOn;
    }

    public String getCreatedBy() {
        return CreatedBy;
    }

    public void setCreatedBy(String createdBy) {
        CreatedBy = createdBy;
    }

    public String getRetiredYN() {
        return RetiredYN;
    }

    public void setRetiredYN(String retiredYN) {
        RetiredYN = retiredYN;
    }

}
