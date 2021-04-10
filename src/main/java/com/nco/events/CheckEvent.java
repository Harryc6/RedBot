package com.nco.events;

import com.nco.utils.DBUtils;
import com.nco.RedBot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CheckEvent {

    public static void check(String[] messageArgs, User author, MessageChannel channel) {
        channel.sendTyping().queue();
        if (messageArgs[0].isEmpty()) {
            checkByUser(author, channel);
        } else if (messageArgs.length == 1) {
            checkByName(messageArgs[0], channel);
        } else {
            returnHelp(channel);
        }
    }

    public static void checkByUser(User author, MessageChannel channel) {
        String sql = "Select * From NCO_PC where DiscordName = ? AND RetiredYN = 'N'";
        try(Connection conn = DBUtils.getConnection(); PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setString(1, author.getAsTag());

            try (ResultSet rs = stat.executeQuery()) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.red);
                if (rs.next()) {
                    buildCharacterEmbed(rs, builder, conn);
                } else {
                    builder.setTitle("No Character Found");
                    builder.setDescription("No active character was found tied to the user " + author.getAsTag());
                }
                channel.sendMessage(builder.build()).queue();
                builder.clear();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private static void buildCharacterEmbed(ResultSet rs, EmbedBuilder builder, Connection conn) throws SQLException {
        String characterName = rs.getString("CharacterName");
        int currentHum = rs.getInt("Humanity");

        builder.setTitle(characterName);
        builder.addField("Bank", rs.getString("Bank"), true);
        builder.addField("HP", rs.getString("CurrentHP") + "/" +
                rs.getString("MaxHP"), true);
        builder.addField("Humanity", currentHum + "/" +
                rs.getString("MaxHumanity"), true);
        builder.addField("Monthly", rs.getString("PayDues"), true);
        builder.addField("Down Time", rs.getString("Downtime"), true);
        builder.addField("IP", rs.getString("InfluencePoints"), true);
        builder.addField("Reputation", rs.getString("Reputation"), true);
        builder.addField("Weekly Games", rs.getString("WeeklyGames"), true);

        buildDescription(builder, currentHum, characterName, conn);
    }

    private static void buildDescription(EmbedBuilder builder, int currentHum, String characterName, Connection conn) throws SQLException {
        buildDescriptionForInjury(builder, characterName, conn);
        buildDescriptionForPsychosis(builder, currentHum);
    }

    private static void buildDescriptionForInjury(EmbedBuilder builder, String characterName, Connection conn) throws SQLException {
        String sql = "SELECT * FROM NCO_CRITICAL_INJURIES WHERE CharacterName = ?";
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setString(1, characterName);
            try (ResultSet rs = stat.executeQuery()) {
                boolean first = true;
                while (rs.next()) {
                    if (first) {
                        builder.setDescription(rs.getString("Injury"));
                        first = false;
                    } else {
                        builder.appendDescription("\n" + rs.getString("Injury"));
                    }
                }
            }
        }
    }

    private static void buildDescriptionForPsychosis(EmbedBuilder builder, int currentHum) {
        if (currentHum < 30) {
            if (!builder.getDescriptionBuilder().toString().isEmpty()) {
                builder.appendDescription("\n");
            }
            if (currentHum >= 20) {
                builder.appendDescription("Borderline dissociative disorder");
            } else if (currentHum >= 10) {
                builder.appendDescription("Dissociative disorder, borderline cyberpsychosis");
            } else if (currentHum >= 0) {
                builder.appendDescription("Cyberpsychosis");
            } else {
                builder.appendDescription("Extreme cyberpsychosis");
            }
        }
    }

    public static void checkByName(String characterName, MessageChannel channel) {
        String sql = "Select * From NCO_PC where CharacterName = ?";
        try(Connection conn = DBUtils.getConnection(); PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setString(1, characterName);
            try (ResultSet rs = stat.executeQuery()) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.red);
                if (rs.next()) {
                    buildCharacterEmbed(rs, builder, conn);
                } else {
                    builder.setTitle("No Character Found");
                    builder.setDescription("No character information was found where the character is called  " + characterName);
                }
                channel.sendMessage(builder.build()).queue();
                builder.clear();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void returnHelp(MessageChannel channel) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.red);
        builder.setTitle("Incorrect Check Formatting");
        builder.setDescription("Please use the commands below to see information on characters \n" + RedBot.PREFIX + "check \"Specific PC Name\" \nor \n" + RedBot.PREFIX + "check");
        channel.sendMessage(builder.build()).queue();
        builder.clear();
    }
}
