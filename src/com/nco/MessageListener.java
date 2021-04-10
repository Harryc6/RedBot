package com.nco;

import com.nco.events.*;
import com.nco.utils.StringUtils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;


public class MessageListener extends ListenerAdapter {
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        User author = event.getAuthor();
        Message message = event.getMessage();
        MessageChannel channel = event.getChannel();
        String messageEvent = message.getContentRaw().split(" ")[0];

        if (!author.isBot() && messageEvent.startsWith(RedBot.PREFIX)) {
            String[] messageArgs = StringUtils.parseArgsString(message.getContentRaw().substring(messageEvent.length()).trim());

            EventType eventType = StringUtils.getEnumFromString(EventType.class, messageEvent.substring(1));
            if (eventType == null) {
                eventType = EventType.UNKNOWN;
            }
            switch (eventType) {
                case ADDICTION:
                    break;
                case BANK:
                    BankEvent bankEvent = new BankEvent();
                    bankEvent.process(messageArgs, author, channel);
                    break;
                case BUYARMOR:
                    break;
                case CHECK:
                    CheckEvent checkEvent = new CheckEvent();
                    checkEvent.process(messageArgs, author, channel);
                    break;
                case COVERAGE:
                    break;
                case FAME:
                    FameEvent fameEvent = new FameEvent();
                    fameEvent.process(messageArgs, author, channel);
                    break;
                case FIXER:
                    break;
                case FIXERDEBT:
                    break;
                case HOSPITAL:
                    break;
                case HP:
                    break;
                case HUM:
                    break;
                case HUSTLE:
                    break;
                case IMPROVE:
                    ImproveEvent improveEvent = new ImproveEvent();
                    improveEvent.process(messageArgs, author, channel);
                    break;
                case INFO:
                    InfoEvent infoEvent = new InfoEvent();
                    infoEvent.process(messageArgs, author, channel);
                    break;
                case INSTALL:
                    break;
                case LIFESTYLE:
                    break;
                case MAXHUM:
                    break;
                case MEDTECH:
                    break;
                case MONTHLY:
                    break;
                case MOTO:
                    break;
                case NANOHP:
                    break;
                case PROPERTY:
                    break;
                case RENT:
                    break;
                case SALVAGETRADE:
                    break;
                case TEAM:
                    break;
                case TECHIE:
                    break;
                case TIMEZONE:
                    break;
                case TRADE:
                    break;
                case TRAUMADEBT:
                    break;
                case UNKNOWN:
                    channel.sendMessage(messageEvent + " Is An Unrecognised Command").queue();
                    break;
                case UPDATEBODYHP:
                    break;
            }
        }
    }
}
