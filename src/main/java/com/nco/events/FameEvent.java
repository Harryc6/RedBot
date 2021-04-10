package com.nco.events;

import com.nco.utils.DBUtils;
import com.nco.RedBot;
import com.nco.utils.StringUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FameEvent {

    public static void fame(String[] messageArgs, User author, MessageChannel channel) {
        channel.sendTyping().queue();
        if (messageArgs.length == 2) {
            fameByUser(messageArgs, channel, author);
        } else if (messageArgs.length == 3) {
            fameByName(messageArgs, channel);
        } else {
            returnHelp(channel);
        }
    }

    private static void fameByUser(String[] messageArg, MessageChannel channel, User author) {
        String sql = "Select * From NCO_PC where DiscordName = ? AND RetiredYN = 'N'";
        try(Connection conn = DBUtils.getConnection(); PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setString(1, author.getAsTag());
            try (ResultSet rs = stat.executeQuery()) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.red);
                if (rs.next()) {
                    String[] updatesArg = StringUtils.prefixArray(rs.getString("CharacterName"), messageArg);
                    updateFameAndRespond(conn, rs, updatesArg, builder);
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

    private static void updateFameAndRespond(Connection conn, ResultSet rs, String[] messageArg, EmbedBuilder builder) throws SQLException {
        if (updateFame(messageArg, rs.getInt("Fame"), conn) && insertFame(messageArg, conn)) {
            int oldFame = rs.getInt("Fame");
            int newFame = oldFame + Integer.parseInt(messageArg[2]);
            int oldReputation = rs.getInt("Reputation");
            int newReputation = newFame / 20;

            builder.setTitle(messageArg[0] + "'s Fame Updated");
            builder.setDescription("For \"" + messageArg[1] + "\"");
            builder.addField("Old Fame", String.valueOf(oldFame), true);
            builder.addBlankField(true);
            builder.addField("New Fame", String.valueOf(newFame), true);

            if (oldReputation != newReputation) {
                builder.addField("Old Reputation", rs.getString("Reputation"), true);
                builder.addField("New Reputation", String.valueOf(newReputation), true);
            }
        } else {
            builder.setTitle("ERROR: Fame Update Or Insert Failure");
            builder.setDescription("Please contact an administrator to get this resolved");
        }
    }

    private static boolean updateFame(String[] messageArg, int currentFame, Connection conn) throws SQLException {
        int newFameTotal = currentFame + Integer.parseInt(messageArg[2]);
        int newReputation = newFameTotal / 20;
        String sql = "UPDATE NCO_PC set Fame = ?, Reputation = ? Where CharacterName = ?";
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setInt(1, newFameTotal);
            stat.setInt(2, newReputation);
            stat.setString(3, messageArg[0]);
            return stat.executeUpdate() == 1;
        }
    }

    private static boolean insertFame(String[] messageArg, Connection conn) throws SQLException {
        String sql = "INSERT INTO NCO_FAME (CharacterName, Reason, Fame) VALUES (?,?,?)";
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setString(1, messageArg[0]);
            stat.setString(2, messageArg[1]);
            stat.setString(3, messageArg[2]);
            return stat.executeUpdate() == 1;
        }
    }

    private static void fameByName(String[] messageArg, MessageChannel channel) {
        String sql = "Select * From NCO_PC where CharacterName = ?";
        try(Connection conn = DBUtils.getConnection(); PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setString(1, messageArg[0]);
            try (ResultSet rs = stat.executeQuery()) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.red);
                if (rs.next()) {
                    updateFameAndRespond(conn, rs, messageArg, builder);
                } else {
                    builder.setTitle("No Character Found");
                    builder.setDescription("No character information was found where the character is called  " + messageArg[0]);
                }
                channel.sendMessage(builder.build()).queue();
                builder.clear();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private static void returnHelp(MessageChannel channel) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.red);
        builder.setTitle("Incorrect Fame Formatting");
        builder.setDescription("Please use the commands below to add fame onto a characters \n" + RedBot.PREFIX +
                "fame \"Specific PC Name\" \"Reason\" \"Amount\" \nor \n" + RedBot.PREFIX + "fame \"Reason\" \"Amount\"");
        channel.sendMessage(builder.build()).queue();
        builder.clear();
    }

}
