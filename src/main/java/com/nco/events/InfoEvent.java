package com.nco.events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.awt.*;

public class InfoEvent {

    public static void info(MessageChannel channel) {
        channel.sendTyping().queue();

        EmbedBuilder info = new EmbedBuilder();
        info.setTitle("RedBot v2");
        info.setDescription("This is based off of the previous iteration RedBot by Blaze.");
        info.addField("Version", "Pre-Alpha", true);
        info.addField("Creator", "Harry Carr", true);
        info.setColor(Color.red);

        channel.sendMessage(info.build()).queue();
        info.clear();
    }

}
