package com.nco.commands;

import com.nco.RedBot;
import com.nco.pojos.PlayerCharacter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GoatCommand extends AbstractCommand {

    public GoatCommand(String[] messageArgs, User author, MessageChannel channel) {
        super(messageArgs, author, channel);
    }

    @Override
    protected boolean canProcessByUser() {
        return messageArgs[0].isEmpty();
    }

    @Override
    protected boolean canProcessByName() {
        return messageArgs.length == 1;
    }

    @Override
    protected void processUpdateAndRespond(Connection conn, PlayerCharacter pc, EmbedBuilder builder) throws SQLException {
        String characterName = pc.getCharacterName();
        int currentBank = pc.getBank();

        builder.setTitle(characterName);
        builder.addField("Bank", String.valueOf(currentBank), true);
        if (currentBank<500){
            builder.addField("Bank Status", String.valueOf('Choom you need to get some eddies'), true);

        } else {
            builder.addField("Bank Status", String.valueOf('Choom you are LOADED'), true);
        }
    }


    @Override
    protected String getHelpTitle() {
        return "Incorrect Goat Formatting";
    }

    @Override
    protected String getHelpDescription() {
        return "Please use the commands below to see information on characters \n" + RedBot.PREFIX +
                "Goat \"PC Name(Optional)\"";
    }

}
