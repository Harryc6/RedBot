package com.nco.commands;

import com.nco.pojos.PlayerCharacter;
import com.nco.utils.ConfigVar;
import com.nco.utils.DBUtils;
import com.nco.utils.JDAUtils;
import com.nco.utils.StringUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
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
    Member member;
    Message message;

    public AbstractCommand(String[] messageArgs, GuildMessageReceivedEvent event) {
        this.messageArgs = messageArgs;
        this.author = event.getAuthor();
        this.channel = event.getChannel();
        this.member = event.getMember();
        this.message = event.getMessage();
        process();
    }

    private void process() {
        channel.sendTyping().queue();
        logger.info("Starting " + getClass().getSimpleName() + " with args: " + StringUtils.parseArray(messageArgs));
        if (userHasPermission()) {
            if (findPlayerCharacter()) {
                if (canProcessByUser()) {
                    processByUser();
                } else if (canProcessByName()) {
                    processByName();
                } else {
                    returnHelp();
                }
            } else {
                if (canProcessWithoutPC()) {
                    processWithoutPC();
                } else {
                    returnHelp();
                }
            }
        } else {
            invalidPermissions();
        }
        logger.info("Finishing " + getClass().getSimpleName() + " with args: " + StringUtils.parseArray(messageArgs));
    }

    private boolean userHasPermission() {
        if (getRoleRequiredForCommand().isEmpty()) {
            return true;
        } else {
            return JDAUtils.hasRole(member, getRoleRequiredForCommand());
        }
    }

    protected boolean findPlayerCharacter() {
        return true;
    }

    protected String getRoleRequiredForCommand() {
        return "";
    }

    protected boolean canProcessByUser() {
        return false;
    }

    protected boolean canProcessByName() {
        return false;
    }

    protected boolean canProcessWithoutPC() {
        return false;
    }

    private void processByUser() {
        try(Connection conn = DBUtils.getConnection()) {
            PlayerCharacter pc = new PlayerCharacter(conn, author.getAsTag(), false);
            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.red);
            messageArgs = StringUtils.prefixArray(pc.getCharacterName(), messageArgs);
            processUpdateAndRespond(conn, pc, builder);
            channel.sendMessage(builder.build()).queue();
            builder.clear();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void processByName() {
        try (Connection conn = DBUtils.getConnection()) {
            PlayerCharacter pc = new PlayerCharacter(conn, messageArgs[0], true);
            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.red);
            processUpdateAndRespond(conn, pc, builder);
            channel.sendMessage(builder.build()).queue();
            builder.clear();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    private void processWithoutPC() {
        try (Connection conn = DBUtils.getConnection()) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.red);
            processUpdateAndRespond(conn, null, builder);
            channel.sendMessage(builder.build()).queue();
            builder.clear();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    protected abstract void processUpdateAndRespond(Connection conn, PlayerCharacter pc, EmbedBuilder builder) throws SQLException;

    private void returnHelp() {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.red);
        builder.setTitle(getHelpTitle());
        builder.setDescription(getHelpDescription());
        builder.addField("Need more help?", "[Documentation can be found here](" + ConfigVar.getDocumentationURL() + ")", true);
        channel.sendMessage(builder.build()).queue();
        builder.clear();
    }

    protected abstract String getHelpTitle();

    protected abstract String getHelpDescription();

    private void invalidPermissions() {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.red);
        builder.setTitle("Invalid Permissions");
        builder.setDescription("You do not have the required role to use this command");
        channel.sendMessage(builder.build()).queue();
        builder.clear();
    }

}
