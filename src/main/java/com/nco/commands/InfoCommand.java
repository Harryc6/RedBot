package com.nco.commands;

import com.nco.pojos.PlayerCharacter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;

public class InfoCommand extends AbstractCommand {

    public InfoCommand(String[] messageArgs, User author, MessageChannel channel, Member member) {
        super(messageArgs, author, channel, member);
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
        return "This is based off of the previous iteration RedBot by  Kookie & Blaze.\n\n" +
                "**Version:** Pre-Alpha\n\n" +
                "**Creators:** Harry, Naabsault & Ronin";
    }
}
