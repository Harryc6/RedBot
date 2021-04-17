package com.nco.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;

public class InfoCommand extends AbstractCommand {

    public InfoCommand(String[] messageArgs, User author, MessageChannel channel) {
        super(messageArgs, author, channel);
    }

    @Override
    protected void returnHelp() {
        EmbedBuilder info = new EmbedBuilder();
        info.setTitle("RedBot v2");
        info.setDescription("This is based off of the previous iteration RedBot by Kookie & Blaze.");
        info.addField("Version", "Pre-Alpha", true);
        info.addField("Creator", "Harry Carr", true);
        info.setColor(Color.red);

        channel.sendMessage(info.build()).queue();
        info.clear();
    }

}
