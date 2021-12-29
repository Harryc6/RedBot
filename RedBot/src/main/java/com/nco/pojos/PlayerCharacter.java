package com.nco.pojos;

import com.nco.enums.PC;
import com.nco.enums.Skills;
import com.nco.enums.Stats;
import com.nco.utils.StringUtils;

import java.lang.reflect.Field;
import java.sql.*;

import java.util.HashMap;
import java.util.Map;

public class PlayerCharacter {

    // NCO_PC
    private String discordName;
    private String characterName;
    private String role;
    private int roleRank;
    private String creationRank;
    private String rank;
    private int streetCred;
    private int bank;
    private int headSp;
    private int bodySp;
    private int improvementPoints;
    private int reputation;
    private int fame;
    private String vault;
    private int downtime;
    private String lifestyle;
    private String rent;
    private int monthlyDebt;
    private int monthlyDue;
    private int gamesOverall;
    private int weeklyGames;
    private Date jobBanUntil;
    private int surgery;
    private int pharmaceuticals;
    private int cryosystemOperation;
    private int fieldExpertise;
    private int upgradeExpertise;
    private int fabricationExpertise;
    private int inventionExpertise;
    private String retiredYn;

    // NCO_PC_STATS
    private int intelligence;
    private int reflexes;
    private int dexterity;
    private int technique;
    private int cool;
    private int willpower;
    private int luck;
    private int usableLuck;
    private int movement;
    private int body;
    private int currentEmpathy;
    private int maxEmpathy;
    private int currentHp;
    private int maxHp;
    private int currentHumanity;
    private int maxHumanity;

    // NCO_PC_SKILLS
    private int aVTech;
    private int accounting;
    private int acting;
    private int animalHandling;
    private int archery;
    private int athletics;
    private int autofire;
    private int basicTech;
    private int brawling;
    private int bribery;
    private int bureaucracy;
    private int business;
    private int composition;
    private int concealReveal;
    private int concentration;
    private int contortionist;
    private int conversation;
    private int criminology;
    private int cryptography;
    private int cybertech;
    private int dance;
    private int deduction;
    private int demolitions;
    private int driveLand;
    private int education;
    private int electronicsSecurity;
    private int endurance;
    private int evasion;
    private int firstAid;
    private int forgery;
    private int gamble;
    private int handgun;
    private int heavyArms;
    private int humanPerception;
    private int instrumentA;
    private int instrumentB;
    private int interrogation;
    private int lVTech;
    private int languageA;
    private int languageB;
    private int librarySearch;
    private int lipReading;
    private int localExpertA;
    private int localExpertB;
    private int martial;
    private int melee;
    private int paintDrawSculpt;
    private int paramedic;
    private int perception;
    private int personalGrooming;
    private int persuasion;
    private int photographyFilm;
    private int pickLock;
    private int pickPocket;
    private int pilotAir;
    private int pilotSea;
    private int riding;
    private int sVTech;
    private int scienceA;
    private int scienceB;
    private int shoulderArms;
    private int stealth;
    private int streetslang;
    private int streetwise;
    private int tactics;
    private int tortureDrugs;
    private int tracking;
    private int trading;
    private int wardrobe;
    private int weaponstech;
    private int wilderness;
    private int yourHome;

    public PlayerCharacter(Connection conn, String name, boolean isPC) {
        Map<String, Field> map = new HashMap<>();
        for (Field field : getClass().getDeclaredFields()) {
            map.put(StringUtils.camelToSnakeCase(field.getName()), field);
        }
        setPcValues(conn, name, isPC, map);
        setStatsValues(conn, map);
        setSkillsValues(conn, map);
    }

    private void setPcValues(Connection conn, String name, boolean isPC, Map<String, Field> map) {
        try(PreparedStatement stat = conn.prepareStatement(getPCSqL(isPC))) {
            stat.setString(1, name);
            try (ResultSet rs = stat.executeQuery()) {
                if (rs.next()) {
                    for (PC enumValue : PC.values()) {
                        setFieldsByEnum(rs, enumValue, map.get(enumValue.toString()));
                    }
                }
            }
        } catch (SQLException | IllegalAccessException throwables) {
            throwables.printStackTrace();
        }
    }

    private String getPCSqL(boolean isPC) {
        if (isPC) {
            return "Select * From NCO_PC where character_name = ?";
        } else {
            return "Select * From NCO_PC where discord_name = ? AND retired_yn = 'n'";
        }
    }

    private void setFieldsByEnum(ResultSet rs, Enum<?> enumValue, Field field) throws SQLException, IllegalAccessException {
        field.setAccessible(true);
        if (Integer.TYPE == field.getType()) {
            field.setInt(this, rs.getInt(enumValue.toString()));
        } else if (Date.class == field.getType()) {
            field.set(this, rs.getDate(enumValue.toString()));
        } else if (String.class == field.getType()) {
            field.set(this, rs.getString(enumValue.toString()));
        }
    }

    private void setStatsValues(Connection conn, Map<String, Field> map) {
        String sql = "Select * From nco_pc_stats where character_name = ?";
        try(PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setString(1, characterName);
            try (ResultSet rs = stat.executeQuery()) {
                if (rs.next()) {
                    for (Stats enumValue : Stats.values()) {
                        setFieldsByEnum(rs, enumValue, map.get(enumValue.toString()));
                    }
                }
            }
        } catch (SQLException | IllegalAccessException throwables) {
            throwables.printStackTrace();
        }
    }

    private void setSkillsValues(Connection conn, Map<String, Field> map) {
        String sql = "Select * From nco_pc_skills where character_name = ?";
        try(PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setString(1, characterName);
            try (ResultSet rs = stat.executeQuery()) {
                if (rs.next()) {
                    for (Skills enumValue : Skills.values()) {
                        setFieldsByEnum(rs, enumValue, map.get(enumValue.toString()));
                    }
                }
            }
        } catch (SQLException | IllegalAccessException throwables) {
            throwables.printStackTrace();
        }
    }

    public String getDiscordName() {
        return discordName;
    }

    public String getCharacterName() {
        return characterName;
    }

    public String getCharacterDisplayName() {
        return StringUtils.capitalizeWords(characterName);
    }

    public String getRole() {
        return role;
    }

    public int getRoleRank() {
        return roleRank;
    }

    public String getCreationRank() {
        return creationRank;
    }

    public String getRank() {
        return rank;
    }

    public int getStreetCred() {
        return streetCred;
    }

    public int getBank() {
        return bank;
    }

    public int getHeadSp() {
        return headSp;
    }

    public int getBodySp() {
        return bodySp;
    }

    public int getImprovementPoints() {
        return improvementPoints;
    }

    public int getReputation() {
        return reputation;
    }

    public int getFame() {
        return fame;
    }

    public String getVault() {
        return vault;
    }

    public int getDowntime() {
        return downtime / 12;
    }

    public int getDowntimeRemainder() {
        return downtime % 12;
    }

    public String getDowntimeToDisplay() {
        return downtime / 12 + ((downtime % 12 == 0) ? "" :
                " " + StringUtils.superscript(String.valueOf(downtime % 12)) + "/" + StringUtils.subscript("12"));
    }

    public String getLifestyle() {
        return lifestyle;
    }

    public String getRent() {
        return rent;
    }

    public int getMonthlyDebt() {
        return monthlyDebt;
    }

    public int getMonthlyDue() {
        return monthlyDue;
    }

    public int getGamesTillMonthlyDue() {
        return getGamesOverall() - getMonthlyDue();
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

    public int getSurgery() {
        return surgery;
    }

    public int getPharmaceuticals() {
        return pharmaceuticals;
    }

    public int getCryosystemOperation() {
        return cryosystemOperation;
    }

    public int getFieldExpertise() {
        return fieldExpertise;
    }

    public int getUpgradeExpertise() {
        return upgradeExpertise;
    }

    public int getFabricationExpertise() {
        return fabricationExpertise;
    }

    public int getInventionExpertise() {
        return inventionExpertise;
    }

    public String getRetiredYn() {
        return retiredYn;
    }

    public int getIntelligence() {
        return intelligence;
    }

    public int getReflexes() {
        return reflexes;
    }

    public int getDexterity() {
        return dexterity;
    }

    public int getTechnique() {
        return technique;
    }

    public int getCool() {
        return cool;
    }

    public int getWillpower() {
        return willpower;
    }

    public int getLuck() {
        return luck;
    }

    public int getUsableLuck() {
        return usableLuck;
    }

    public int getMovement() {
        return movement;
    }

    public int getBody() {
        return body;
    }

    public int getCurrentEmpathy() {
        return currentEmpathy;
    }

    public int getMaxEmpathy() {
        return maxEmpathy;
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getCurrentHumanity() {
        return currentHumanity;
    }

    public int getMaxHumanity() {
        return maxHumanity;
    }

    public int getAVTech() {
        return aVTech;
    }

    public int getAccounting() {
        return accounting;
    }

    public int getActing() {
        return acting;
    }

    public int getAnimalHandling() {
        return animalHandling;
    }

    public int getArchery() {
        return archery;
    }

    public int getAthletics() {
        return athletics;
    }

    public int getAutofire() {
        return autofire;
    }

    public int getBasicTech() {
        return basicTech;
    }

    public int getBrawling() {
        return brawling;
    }

    public int getBribery() {
        return bribery;
    }

    public int getBureaucracy() {
        return bureaucracy;
    }

    public int getBusiness() {
        return business;
    }

    public int getComposition() {
        return composition;
    }

    public int getConcealReveal() {
        return concealReveal;
    }

    public int getConcentration() {
        return concentration;
    }

    public int getContortionist() {
        return contortionist;
    }

    public int getConversation() {
        return conversation;
    }

    public int getCriminology() {
        return criminology;
    }

    public int getCryptography() {
        return cryptography;
    }

    public int getCybertech() {
        return cybertech;
    }

    public int getDance() {
        return dance;
    }

    public int getDeduction() {
        return deduction;
    }

    public int getDemolitions() {
        return demolitions;
    }

    public int getDriveLand() {
        return driveLand;
    }

    public int getEducation() {
        return education;
    }

    public int getElectronicsSecurity() {
        return electronicsSecurity;
    }

    public int getEndurance() {
        return endurance;
    }

    public int getEvasion() {
        return evasion;
    }

    public int getFirstAid() {
        return firstAid;
    }

    public int getForgery() {
        return forgery;
    }

    public int getGamble() {
        return gamble;
    }

    public int getHandgun() {
        return handgun;
    }

    public int getHeavyArms() {
        return heavyArms;
    }

    public int getHumanPerception() {
        return humanPerception;
    }

    public int getInstrumentA() {
        return instrumentA;
    }

    public int getInstrumentB() {
        return instrumentB;
    }

    public int getInterrogation() {
        return interrogation;
    }

    public int getlVTech() {
        return lVTech;
    }

    public int getLanguageA() {
        return languageA;
    }

    public int getLanguageB() {
        return languageB;
    }

    public int getLibrarySearch() {
        return librarySearch;
    }

    public int getLipReading() {
        return lipReading;
    }

    public int getLocalExpertA() {
        return localExpertA;
    }

    public int getLocalExpertB() {
        return localExpertB;
    }

    public int getMartial() {
        return martial;
    }

    public int getMelee() {
        return melee;
    }

    public int getPaintDrawSculpt() {
        return paintDrawSculpt;
    }

    public int getParamedic() {
        return paramedic;
    }

    public int getPerception() {
        return perception;
    }

    public int getPersonalGrooming() {
        return personalGrooming;
    }

    public int getPersuasion() {
        return persuasion;
    }

    public int getPhotographyFilm() {
        return photographyFilm;
    }

    public int getPickLock() {
        return pickLock;
    }

    public int getPickPocket() {
        return pickPocket;
    }

    public int getPilotAir() {
        return pilotAir;
    }

    public int getPilotSea() {
        return pilotSea;
    }

    public int getRiding() {
        return riding;
    }

    public int getsVTech() {
        return sVTech;
    }

    public int getScienceA() {
        return scienceA;
    }

    public int getScienceB() {
        return scienceB;
    }

    public int getShoulderArms() {
        return shoulderArms;
    }

    public int getStealth() {
        return stealth;
    }

    public int getStreetslang() {
        return streetslang;
    }

    public int getStreetwise() {
        return streetwise;
    }

    public int getTactics() {
        return tactics;
    }

    public int getTortureDrugs() {
        return tortureDrugs;
    }

    public int getTracking() {
        return tracking;
    }

    public int getTrading() {
        return trading;
    }

    public int getWardrobe() {
        return wardrobe;
    }

    public int getWeaponstech() {
        return weaponstech;
    }

    public int getWilderness() {
        return wilderness;
    }

    public int getYourHome() {
        return yourHome;
    }
}
