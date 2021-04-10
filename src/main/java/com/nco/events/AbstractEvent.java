package com.nco.events;

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

public abstract class AbstractEvent {

    public void process(String[] messageArgs, User author, MessageChannel channel) {
        channel.sendTyping().queue();
        if (canProcessByUser(messageArgs)) {
            processByUser(messageArgs, channel, author);
        } else if (canProcessByName(messageArgs)) {
            processByName(messageArgs, channel);
        } else {
            returnHelp(channel);
        }
    }

    protected boolean canProcessByUser(String[] messageArgs) {
        return false;
    }

    protected boolean canProcessByName(String[] messageArgs) {
        return false;
    }

    private void processByUser(String[] messageArgs, MessageChannel channel, User author) {
        String sql = "Select * From NCO_PC where DiscordName = ? AND RetiredYN = 'N'";
        try(Connection conn = DBUtils.getConnection(); PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setString(1, author.getAsTag());
            try (ResultSet rs = stat.executeQuery()) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.red);
                if (rs.next()) {
                    String[] updatesArgs = StringUtils.prefixArray(rs.getString("CharacterName"), messageArgs);
                    processUpdateAndRespond(conn, rs, updatesArgs, builder);
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

    private void processByName(String[] messageArgs, MessageChannel channel) {
        String sql = "Select * From NCO_PC where CharacterName = ?";
        try(Connection conn = DBUtils.getConnection(); PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setString(1, messageArgs[0]);
            try (ResultSet rs = stat.executeQuery()) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.red);
                if (rs.next()) {
                    processUpdateAndRespond(conn, rs, messageArgs, builder);
                } else {
                    builder.setTitle("No Character Found");
                    builder.setDescription("No character information was found where the character is called  " + messageArgs[0]);
                }
                channel.sendMessage(builder.build()).queue();
                builder.clear();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    protected void processUpdateAndRespond(Connection conn, ResultSet rs, String[] messageArgs, EmbedBuilder builder) throws SQLException {

    }

    protected void returnHelp(MessageChannel channel) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.red);
        builder.setTitle(getHelpTitle());
        builder.setDescription(getHelpDescription());
        channel.sendMessage(builder.build()).queue();
        builder.clear();
    }


    protected String getHelpTitle() {
        return "";
    }

    protected String getHelpDescription() {
        return "";
    }

}
