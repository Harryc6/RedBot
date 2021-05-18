package com.nco.entities;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Objects;

@NamedQueries({
        @NamedQuery(name = "CharacterByCharacterName", query = "from NcoPcEntity where characterName = :characterName and retiredYn = 'N'")


        })

@Entity
@Table(name = "NCO_PC", schema = "sql4404136")
public class NcoPcEntity {
    private String discordName;
    private String characterName;
    private String role;
    private Integer streetCred;
    private String rank;
    private Integer bodyScore;
    private Integer willScore;
    private Integer maxHp;
    private Integer currentHp;
    private Integer headSp;
    private Integer bodySp;
    private Integer maxHumanity;
    private Integer humanity;
    private Integer bank;
    private Integer influencePoints;
    private Integer reputation;
    private Integer fame;
    private Integer vault;
    private Integer downTime;
    private String lifestyle;
    private String rent;
    private String payDues;
    private String addicted;
    private Integer hospitalDebt;
    private String ttCoverage;
    private String ttcLevel;
    private Integer salvagedEuros;
    private Integer gamesOverall;
    private Integer weeklyGames;
    private Date jobBanUntil;
    private Timestamp createdOn;
    private String createdBy;
    private String retiredYn;

    @Basic
    @Column(name = "DiscordName")
    public String getDiscordName() {
        return discordName;
    }

    public void setDiscordName(String discordName) {
        this.discordName = discordName;
    }

    @Id
    @Column(name = "CharacterName")
    public String getCharacterName() {
        return characterName;
    }

    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }

    @Basic
    @Column(name = "Role")
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Basic
    @Column(name = "StreetCred")
    public Integer getStreetCred() {
        return streetCred;
    }

    public void setStreetCred(Integer streetCred) {
        this.streetCred = streetCred;
    }

    @Basic
    @Column(name = "Rank")
    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    @Basic
    @Column(name = "BodyScore")
    public Integer getBodyScore() {
        return bodyScore;
    }

    public void setBodyScore(Integer bodyScore) {
        this.bodyScore = bodyScore;
    }

    @Basic
    @Column(name = "WillScore")
    public Integer getWillScore() {
        return willScore;
    }

    public void setWillScore(Integer willScore) {
        this.willScore = willScore;
    }

    @Basic
    @Column(name = "MaxHP")
    public Integer getMaxHp() {
        return maxHp;
    }

    public void setMaxHp(Integer maxHp) {
        this.maxHp = maxHp;
    }

    @Basic
    @Column(name = "CurrentHP")
    public Integer getCurrentHp() {
        return currentHp;
    }

    public void setCurrentHp(Integer currentHp) {
        this.currentHp = currentHp;
    }

    @Basic
    @Column(name = "HeadSP")
    public Integer getHeadSp() {
        return headSp;
    }

    public void setHeadSp(Integer headSp) {
        this.headSp = headSp;
    }

    @Basic
    @Column(name = "BodySP")
    public Integer getBodySp() {
        return bodySp;
    }

    public void setBodySp(Integer bodySp) {
        this.bodySp = bodySp;
    }

    @Basic
    @Column(name = "MaxHumanity")
    public Integer getMaxHumanity() {
        return maxHumanity;
    }

    public void setMaxHumanity(Integer maxHumanity) {
        this.maxHumanity = maxHumanity;
    }

    @Basic
    @Column(name = "Humanity")
    public Integer getHumanity() {
        return humanity;
    }

    public void setHumanity(Integer humanity) {
        this.humanity = humanity;
    }

    @Basic
    @Column(name = "Bank")
    public Integer getBank() {
        return bank;
    }

    public void setBank(Integer bank) {
        this.bank = bank;
    }

    @Basic
    @Column(name = "InfluencePoints")
    public Integer getInfluencePoints() {
        return influencePoints;
    }

    public void setInfluencePoints(Integer influencePoints) {
        this.influencePoints = influencePoints;
    }

    @Basic
    @Column(name = "Reputation")
    public Integer getReputation() {
        return reputation;
    }

    public void setReputation(Integer reputation) {
        this.reputation = reputation;
    }

    @Basic
    @Column(name = "Fame")
    public Integer getFame() {
        return fame;
    }

    public void setFame(Integer fame) {
        this.fame = fame;
    }

    @Basic
    @Column(name = "Vault")
    public Integer getVault() {
        return vault;
    }

    public void setVault(Integer vault) {
        this.vault = vault;
    }

    @Basic
    @Column(name = "DownTime")
    public Integer getDownTime() {
        return downTime;
    }

    public void setDownTime(Integer downTime) {
        this.downTime = downTime;
    }

    @Basic
    @Column(name = "Lifestyle")
    public String getLifestyle() {
        return lifestyle;
    }

    public void setLifestyle(String lifestyle) {
        this.lifestyle = lifestyle;
    }

    @Basic
    @Column(name = "Rent")
    public String getRent() {
        return rent;
    }

    public void setRent(String rent) {
        this.rent = rent;
    }

    @Basic
    @Column(name = "PayDues")
    public String getPayDues() {
        return payDues;
    }

    public void setPayDues(String payDues) {
        this.payDues = payDues;
    }

    @Basic
    @Column(name = "Addicted")
    public String getAddicted() {
        return addicted;
    }

    public void setAddicted(String addicted) {
        this.addicted = addicted;
    }

    @Basic
    @Column(name = "HospitalDebt")
    public Integer getHospitalDebt() {
        return hospitalDebt;
    }

    public void setHospitalDebt(Integer hospitalDebt) {
        this.hospitalDebt = hospitalDebt;
    }

    @Basic
    @Column(name = "TTCoverage")
    public String getTtCoverage() {
        return ttCoverage;
    }

    public void setTtCoverage(String ttCoverage) {
        this.ttCoverage = ttCoverage;
    }

    @Basic
    @Column(name = "TTCLevel")
    public String getTtcLevel() {
        return ttcLevel;
    }

    public void setTtcLevel(String ttcLevel) {
        this.ttcLevel = ttcLevel;
    }

    @Basic
    @Column(name = "SalvagedEuros")
    public Integer getSalvagedEuros() {
        return salvagedEuros;
    }

    public void setSalvagedEuros(Integer salvagedEuros) {
        this.salvagedEuros = salvagedEuros;
    }

    @Basic
    @Column(name = "GamesOverall")
    public Integer getGamesOverall() {
        return gamesOverall;
    }

    public void setGamesOverall(Integer gamesOverall) {
        this.gamesOverall = gamesOverall;
    }

    @Basic
    @Column(name = "WeeklyGames")
    public Integer getWeeklyGames() {
        return weeklyGames;
    }

    public void setWeeklyGames(Integer weeklyGames) {
        this.weeklyGames = weeklyGames;
    }

    @Basic
    @Column(name = "JobBanUntil")
    public Date getJobBanUntil() {
        return jobBanUntil;
    }

    public void setJobBanUntil(Date jobBanUntil) {
        this.jobBanUntil = jobBanUntil;
    }

    @Basic
    @Column(name = "CreatedOn")
    public Timestamp getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Timestamp createdOn) {
        this.createdOn = createdOn;
    }

    @Basic
    @Column(name = "CreatedBy")
    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Basic
    @Column(name = "RetiredYN")
    public String getRetiredYn() {
        return retiredYn;
    }

    public void setRetiredYn(String retiredYn) {
        this.retiredYn = retiredYn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NcoPcEntity that = (NcoPcEntity) o;
        return Objects.equals(discordName, that.discordName) && Objects.equals(characterName, that.characterName) && Objects.equals(role, that.role) && Objects.equals(streetCred, that.streetCred) && Objects.equals(rank, that.rank) && Objects.equals(bodyScore, that.bodyScore) && Objects.equals(willScore, that.willScore) && Objects.equals(maxHp, that.maxHp) && Objects.equals(currentHp, that.currentHp) && Objects.equals(headSp, that.headSp) && Objects.equals(bodySp, that.bodySp) && Objects.equals(maxHumanity, that.maxHumanity) && Objects.equals(humanity, that.humanity) && Objects.equals(bank, that.bank) && Objects.equals(influencePoints, that.influencePoints) && Objects.equals(reputation, that.reputation) && Objects.equals(fame, that.fame) && Objects.equals(vault, that.vault) && Objects.equals(downTime, that.downTime) && Objects.equals(lifestyle, that.lifestyle) && Objects.equals(rent, that.rent) && Objects.equals(payDues, that.payDues) && Objects.equals(addicted, that.addicted) && Objects.equals(hospitalDebt, that.hospitalDebt) && Objects.equals(ttCoverage, that.ttCoverage) && Objects.equals(ttcLevel, that.ttcLevel) && Objects.equals(salvagedEuros, that.salvagedEuros) && Objects.equals(gamesOverall, that.gamesOverall) && Objects.equals(weeklyGames, that.weeklyGames) && Objects.equals(jobBanUntil, that.jobBanUntil) && Objects.equals(createdOn, that.createdOn) && Objects.equals(createdBy, that.createdBy) && Objects.equals(retiredYn, that.retiredYn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(discordName, characterName, role, streetCred, rank, bodyScore, willScore, maxHp, currentHp, headSp, bodySp, maxHumanity, humanity, bank, influencePoints, reputation, fame, vault, downTime, lifestyle, rent, payDues, addicted, hospitalDebt, ttCoverage, ttcLevel, salvagedEuros, gamesOverall, weeklyGames, jobBanUntil, createdOn, createdBy, retiredYn);
    }
}
