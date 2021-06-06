package com.nco;

import com.nco.commands.*;
import com.nco.utils.StringUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;


public class MessageListener extends ListenerAdapter {
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        User author = event.getAuthor();
        Member member = event.getMember();
        Message message = event.getMessage();
        MessageChannel channel = event.getChannel();
        String messageEvent = message.getContentRaw().split(" ")[0];

        if (!author.isBot() && messageEvent.startsWith(RedBot.PREFIX)) {
            String[] messageArgs = StringUtils.parseArgsString(message.getContentRaw().substring(messageEvent.length()).trim());

            Commands eventType = StringUtils.getEnumFromString(Commands.class, messageEvent.substring(1));
            if (eventType == null) {
                eventType = Commands.UNKNOWN;
            }
            switch (eventType) {
                case ADDICTION:
                    break;
                case BANK:
                    new BankCommand(messageArgs, author, channel, member).process();
                    break;
                case BUYARMOR:
                    new BuyArmorCommand(messageArgs, author, channel, member).process();
                    break;
                case CHECK:
                     new CheckCommand(messageArgs, author, channel, member).process();
                    break;
                case COVERAGE:
                    break;
                case FAME:
                    new FameCommand(messageArgs, author, channel, member).process();
                    break;
                case FIXER:
                    break;
                case FIXERDEBT:
                    break;
                case HOSPITAL:
                    break;
                case HP:
                    new HPCommand(messageArgs, author, channel, member).process();
                    break;
                case HUM:
                    new HumCommand(messageArgs, author, channel, member).process();
                    break;
                case HUSTLE:
                    new HustleCommand(messageArgs, author, channel, member).process();
                    break;
                case IMPROVE:
                    new ImproveCommand(messageArgs, author, channel, member).process();
                    break;
                case INFO:
                    new InfoCommand(messageArgs, author, channel, member).process();
                    break;
                case INSTALL:
                    new InstallCommand(messageArgs, author, channel, member).process();
                    break;
                case LIFESTYLE:
                    break;
                case MAXHUM:
                    new MaxHumCommand(messageArgs, author, channel, member).process();
                    break;
                case MEDTECH:
                    break;
                case MONTHLY:
                    break;
                case MOTO:
                    break;
                case NANOHP:
                    new NanoHPCommand(messageArgs, author, channel, member).process();
                    break;
                case PROPERTY:
                    break;
                case RENT:
                    break;
                case SELECT:
                    new SelectCommand(messageArgs, author, channel, member).process();
                    break;
                case TEAM:
                    break;
                case TECHIE:
                    break;
                case TIMEZONE:
                    break;
                case TRADE:
                    new TradeCommand(messageArgs, author, channel, member).process();
                    break;
                case TRAUMADEBT:
                    break;
                case UPDATE:
                    new UpdateCommand(messageArgs, author, channel, member).process();
                    break;
                case UNKNOWN:
                    channel.sendMessage(messageEvent + " Is An Unrecognised Command").queue();
                    break;
                case UPDATEBODYHP:
                    new UpdateBodyHpCommand(messageArgs, author, channel, member).process();
                    break;
            }
        }
    }
}
