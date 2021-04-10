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

public class CheckEvent extends AbstractEvent {

    @Override
    protected boolean canProcessByUser(String[] messageArgs) {
        return messageArgs[0].isEmpty();
    }

    @Override
    protected boolean canProcessByName(String[] messageArgs) {
        return messageArgs.length == 1;
    }

    @Override
    protected void processUpdateAndRespond(Connection conn, ResultSet rs, String[] messageArgs, EmbedBuilder builder) throws SQLException {
        String characterName = rs.getString("CharacterName");
        int currentHum = rs.getInt("Humanity");

        builder.setTitle(characterName);
        builder.addField("Bank", rs.getString("Bank"), true);
        builder.addField("HP", rs.getString("CurrentHP") + "/" +
                rs.getString("MaxHP"), true);
        builder.addField("Humanity", currentHum + "/" +
                rs.getString("MaxHumanity"), true);
        builder.addField("Monthly", rs.getString("PayDues"), true);
        builder.addField("Down Time", rs.getString("DownTime"), true);
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

    @Override
    protected String getHelpTitle() {
        return "Incorrect Check Formatting";
    }

    @Override
    protected String getHelpDescription() {
        return "Please use the commands below to see information on characters \n" + RedBot.PREFIX +
                "check \"PC Name\" \nor \n" + RedBot.PREFIX + "check";
    }

}
