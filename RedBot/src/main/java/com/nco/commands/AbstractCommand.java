package com.nco.commands;

import com.nco.pojos.PlayerCharacter;
import com.nco.utils.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;

public abstract class AbstractCommand {

    SlashCommandEvent event;
    Logger logger = LoggerFactory.getLogger(this.getClass());
    String[] messageArgs;
    User author;
    MessageChannel channel;
    Member member;
    Message message;

    public AbstractCommand(String[] messageArgs, Object event, boolean isSlash) {
        this.messageArgs = messageArgs;
        if (isSlash) {
            SlashCommandEvent slashEvent = (SlashCommandEvent) event;
            this.author = slashEvent.getUser();
            this.channel = slashEvent.getChannel();
            this.member = slashEvent.getMember();
            this.event = slashEvent;
        } else {
            GuildMessageReceivedEvent guildEvent = (GuildMessageReceivedEvent) event;
            this.author = guildEvent.getAuthor();
            this.channel = guildEvent.getChannel();
            this.member = guildEvent.getMember();
            this.message = guildEvent.getMessage();
        }
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
            messageArgs = StringUtils.prefixArray(pc.getCharacterName(), messageArgs);
            String errorDesc = "No active character was found tied to the user " + author.getAsTag();
            processCommand(conn, pc, builder, errorDesc);
        } catch (Exception e) {
            logErrorAndRespond(e);
        }
    }

    private void processByName() {
        try (Connection conn = DBUtils.getConnection()) {
            messageArgs[0] = messageArgs[0].toLowerCase();
            PlayerCharacter pc = new PlayerCharacter(conn, messageArgs[0], true);
            EmbedBuilder builder = new EmbedBuilder();
            String errorDesc = "No active character was found called " + messageArgs[0];
            processCommand(conn, pc, builder, errorDesc);
        } catch (Exception e) {
            logErrorAndRespond(e);
        }
    }

    private void processCommand(Connection conn, PlayerCharacter pc, EmbedBuilder builder, String errorDesc) throws SQLException {
        builder.setColor(Color.red);
        logCommandUse(conn);
        if (pc.getCharacterName() == null) {
            builder.setTitle("No Character Found");
            builder.setDescription(errorDesc);
        } else {
            processUpdateAndRespond(conn, pc, builder);
        }
        if (event == null) {
            channel.sendMessageEmbeds(builder.build()).queue();
        } else {
            event.replyEmbeds(builder.build()).queue();
        }
        builder.clear();
    }

    private void processWithoutPC() {
        try (Connection conn = DBUtils.getConnection()) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.red);
            logCommandUse(conn);
            processUpdateAndRespond(conn, null, builder);
            channel.sendMessageEmbeds(builder.build()).queue();
            builder.clear();
        } catch (Exception e) {
            logErrorAndRespond(e);
        }
    }

    private void logCommandUse(Connection conn) {
        String sql = getLoggingSQL();
        String command = (event == null ? message.getContentRaw().split(" ")[0].substring(1) : event.getName()).toLowerCase();
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setString(1, command);
            for (int i = 0; i < messageArgs.length; i++) {
                stat.setString(i+2, messageArgs[i]);
            }
            stat.setString(messageArgs.length + 2, author.getAsTag());
            stat.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @NotNull
    private String getLoggingSQL() {
        StringBuilder sql =  new StringBuilder("INSERT INTO NCO_LOGGING (command, ");
        for (int i = 0; i < messageArgs.length; i++) {
            sql.append("arg_").append(i).append(", ");
        }
        sql.append(" created_by) VALUES (?,");
        Arrays.stream(messageArgs).iterator().forEachRemaining(s -> sql.append("?,"));
        sql.append("?)");
        return sql.toString();
    }

    protected abstract void processUpdateAndRespond(Connection conn, PlayerCharacter pc, EmbedBuilder builder) throws SQLException;

    private void returnHelp() {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.red);
        builder.setTitle(getHelpTitle());
        builder.setDescription(getHelpDescription());
        builder.addField("Need more help?", "[Documentation can be found here](" + ConfigVar.getDocumentationURL() + ")", true);
        if (event == null) {
            channel.sendMessageEmbeds(builder.build()).queue();
        } else {
            event.replyEmbeds(builder.build()).queue();
        }
        builder.clear();
    }

    protected abstract String getHelpTitle();

    protected abstract String getHelpDescription();

    private void invalidPermissions() {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.red);
        builder.setTitle("Invalid Permissions");
        builder.setDescription("You do not have the required role to use this command");
        if (event == null) {
            channel.sendMessageEmbeds(builder.build()).queue();
        } else {
            event.replyEmbeds(builder.build()).queue();
        }
        builder.clear();
    }

    private void logErrorAndRespond(Exception e) {
        logger.error(e.getMessage(), e);
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("ERROR: " + e);
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        String trace = sw.toString().substring(e.toString().length(), 700);
        trace = "```Java\n" + trace.substring(0, trace.lastIndexOf("\n")) + "\n```";
        builder.setDescription(trace);
        channel.sendMessageEmbeds(builder.build()).queue();
    }

}