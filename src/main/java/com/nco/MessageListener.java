package com.nco;

import com.nco.events.*;
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
            String[] messageArgs = parseArgsString(message.getContentRaw().substring(messageEvent.length()).trim());

            EventType eventType = getEnumFromString(EventType.class, messageEvent.substring(1));
            if (eventType == null) {
                eventType = EventType.UNKNOWN;
            }
            switch (eventType) {
                case ADDICTION:
                    break;
                case BANK:
                    BankEvent.bank();
                    break;
                case BUYARMOR:
                    break;
                case CHECK:
                    CheckEvent.check(messageArgs, author, channel);
                    break;
                case COVERAGE:
                    break;
                case FAME:
                    FameEvent.fame(messageArgs, author, channel);
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
                    break;
                case INFO:
                    InfoEvent.info(channel);
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

    private String[] parseArgsString(String s) {
        String builtS = "";
        boolean isBetweenQuotationMarks = false;
        for (char c : s.toCharArray()) {
            if (c == '"') {
                isBetweenQuotationMarks = !isBetweenQuotationMarks;
            }
            if (c == ' ' && !isBetweenQuotationMarks) {
                c = '~';
            }
            if (c != '"') {
                builtS += c;
            }
        }
        return builtS.split("~");
    }

    public static <T extends Enum<T>> T getEnumFromString(Class<T> c, String string) {
        if( c != null && string != null ) {
            try {
                return Enum.valueOf(c, string.trim().toUpperCase());
            } catch(IllegalArgumentException ignored) {
            }
        }
        return null;
    }

}
