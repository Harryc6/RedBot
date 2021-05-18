package com.nco.commands;

import com.nco.pojos.PlayerCharacter;
import com.nco.utils.DBUtils;
import com.nco.utils.StringUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.sql.Connection;
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
        PlayerCharacter pc = DBUtils.getCharacterByUser(author.getAsTag());
        try(Connection conn = DBUtils.getConnection()) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.red);
            if (pc != null) {
                messageArgs = StringUtils.prefixArray(pc.getCharacterName(), messageArgs);
                processUpdateAndRespond(conn, pc, builder);
            } else {
                builder.setTitle("No Character Found");
                builder.setDescription("No active character was found tied to the user " + author.getAsTag());
            }
            channel.sendMessage(builder.build()).queue();
            builder.clear();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void processByName() {
        PlayerCharacter pc = DBUtils.getCharacter(messageArgs[0]);
        try (Connection conn = DBUtils.getConnection()) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.red);
            if (pc != null) {
                processUpdateAndRespond(conn, pc, builder);
            } else {
                builder.setTitle("No Character Found");
                builder.setDescription("No character information was found where the character is called  " + messageArgs[0]);
            }
            channel.sendMessage(builder.build()).queue();
            builder.clear();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    protected void processUpdateAndRespond(Connection conn, PlayerCharacter pc, EmbedBuilder builder) throws SQLException {

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
