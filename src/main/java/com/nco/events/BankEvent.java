package com.nco.events;

import com.nco.RedBot;
import com.nco.utils.DBUtils;
import com.nco.utils.StringUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class BankEvent {

    public static void bank (String[] messageArgs, User author, MessageChannel channel) {
        channel.sendTyping().queue();
        if (messageArgs.length >= 2 && StringUtils.isNumeric(messageArgs[1])) {
            bankByUser(messageArgs, channel, author);
        } else if (messageArgs.length >= 3 && StringUtils.isNumeric(messageArgs[2])) {
            bankByName(messageArgs, channel);
        } else {
            returnHelp(channel);
        }
    }

    private static void bankByUser(String[] messageArg, MessageChannel channel, User author) {
        String sql = "Select * From NCO_PC where DiscordName = ? AND RetiredYN = 'N'";
        try(Connection conn = DBUtils.getConnection(); PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setString(1, author.getAsTag());
            try (ResultSet rs = stat.executeQuery()) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.red);
                if (rs.next()) {
                    String[] updatesArg = StringUtils.prefixArray(rs.getString("CharacterName"), messageArg);
                    updateBankAndRespond(conn, rs, updatesArg, builder);
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

    private static void bankByName(String[] messageArg, MessageChannel channel) {
        String sql = "Select * From NCO_PC where CharacterName = ?";
        try(Connection conn = DBUtils.getConnection(); PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setString(1, messageArg[0]);
            try (ResultSet rs = stat.executeQuery()) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.red);
                if (rs.next()) {
                    updateBankAndRespond(conn, rs, messageArg, builder);
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

    private static void updateBankAndRespond(Connection conn, ResultSet rs, String[] messageArg, EmbedBuilder builder) throws SQLException {
        if (updateBank(messageArg, rs, conn) && insertBank(messageArg, conn)) {
            int oldBank = rs.getInt("Bank");
            int newBank = oldBank + Integer.parseInt(messageArg[2]);

            builder.setTitle(messageArg[0] + "'s Bank Balance Updated");
            builder.setDescription("For \"" + messageArg[1] + "\"");
            builder.addField("Old Balance", oldBank + "eb", true);
            builder.addBlankField(true);
            builder.addField("New Balance", newBank + "eb", true);

            if (messageArg.length == 4) {
                int oldDownTime = rs.getInt("DownTime");
                int DTChange = Integer.parseInt(messageArg[3]);
                int newDownTime = oldDownTime - (DTChange < 0 ? -DTChange : DTChange);

                builder.addField("Old DT", String.valueOf(oldDownTime), true);
                builder.addBlankField(true);
                builder.addField("New DT", String.valueOf(newDownTime), true);
            }

        } else {
            builder.setTitle("ERROR: Bank Update Or Insert Failure");
            builder.setDescription("Please contact an administrator to get this resolved");
        }
    }

    private static boolean updateBank(String[] messageArg, ResultSet rs, Connection conn) throws SQLException {
        int newBalance = rs.getInt("Bank") + Integer.parseInt(messageArg[2]);
        int newDownTime = rs.getInt("DownTime");
        if (messageArg.length == 4) {
            int DTChange = Integer.parseInt(messageArg[3]);
            newDownTime -= (DTChange < 0 ? -DTChange : DTChange);;
        }
        String sql = "UPDATE NCO_PC set Bank = ?, DownTime = ? Where CharacterName = ?";

        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setInt(1, newBalance);
            stat.setInt(2, newDownTime);
            stat.setString(3, messageArg[0]);
            return stat.executeUpdate() == 1;
        }
    }

    private static boolean insertBank(String[] messageArg, Connection conn) throws SQLException {
        String sql;
        if (messageArg.length == 4) {
            sql = "INSERT INTO NCO_BANK (CharacterName, Reason, Amount, DownTime) VALUES (?,?,?,?)";
        } else {
            sql = "INSERT INTO NCO_BANK (CharacterName, Reason, Amount) VALUES (?,?,?)";
        }
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setString(1, messageArg[0]);
            stat.setString(2, messageArg[1]);
            stat.setString(3, messageArg[2]);
            if (messageArg.length == 4) {
                int DTChange = Integer.parseInt(messageArg[3]);
                stat.setInt(4, (DTChange < 0 ? -DTChange : DTChange));
            }
            return stat.executeUpdate() == 1;
        }
    }

    private static void returnHelp(MessageChannel channel) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.red);
        builder.setTitle("Incorrect Bank Formatting");
        builder.setDescription("Please use the commands below to manage a characters bank\n" + RedBot.PREFIX +
                "bank \"Specific PC Name\" \"Reason\" \"Amount\" \"DT(Optional)\" \nor \n" + RedBot.PREFIX + "bank \"Reason\" \"Amount\" \"DT(Optional)\"");
        channel.sendMessage(builder.build()).queue();
        builder.clear();
    }

}
