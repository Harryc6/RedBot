package com.nco.commands;

import com.nco.pojos.PlayerCharacter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.sql.Connection;
import java.sql.SQLException;

public class InfoCommand extends AbstractCommand {

    public InfoCommand(String[] messageArgs, GuildMessageReceivedEvent event) {
        super(messageArgs, event);
    }

    @Override
    protected void processUpdateAndRespond(Connection conn, PlayerCharacter pc, EmbedBuilder builder) throws SQLException {

    }

    @Override
    protected String getHelpTitle() {
        return "Redbot-Revolutions";
    }

    @Override
    protected String getHelpDescription() {
        return "This is based off of the previous iteration of RedBot by Blaze & Kookie.\n\n" +
                "**Version:** Pre-Alpha\n\n" +
                "**Creators:** Harry, Naabsault & Ronin";
    }
}
