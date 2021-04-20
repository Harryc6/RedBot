package com.nco.commands;

import com.nco.utils.DBUtils;
import com.nco.utils.StringUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class AbstractCommand {

    Logger logger = LoggerFactory.getLogger(this.getClass());
    String[] messageArgs;
    User author;
    MessageChannel channel;

    public AbstractCommand(String[] messageArgs, User author, MessageChannel channel) {
        this.messageArgs = messageArgs;
        this.author = author;
        this.channel = channel;
    }

    public void process() {
        channel.sendTyping().queue();
        if (canProcessByUser()) {
            processByUser();
        } else if (canProcessByName()) {
            processByName();
        } else {
            returnHelp();
        }
    }

    protected boolean canProcessByUser() {
        return false;
    }

    protected boolean canProcessByName() {
        return false;
    }

    private void processByUser() {
        String sql = "Select * From NCO_PC where DiscordName = ? AND RetiredYN = 'N'";
        try(Connection conn = DBUtils.getConnection(); PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setString(1, author.getAsTag());
            try (ResultSet rs = stat.executeQuery()) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.red);
                if (rs.next()) {
                    messageArgs = StringUtils.prefixArray(rs.getString("CharacterName"), messageArgs);
                    processUpdateAndRespond(conn, rs, builder);
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

    private void processByName() {
        String sql = "Select * From NCO_PC where CharacterName = ?";
        try(Connection conn = DBUtils.getConnection(); PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setString(1, messageArgs[0]);
            try (ResultSet rs = stat.executeQuery()) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.red);
                if (rs.next()) {
                    processUpdateAndRespond(conn, rs, builder);
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

    protected void processUpdateAndRespond(Connection conn, ResultSet rs, EmbedBuilder builder) throws SQLException {

    }

    protected void returnHelp() {
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
