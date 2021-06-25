package com.nco;

import com.nco.commands.*;
import com.nco.enums.Commands;
import com.nco.utils.StringUtils;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class MessageListener extends ListenerAdapter {
    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        String messageEvent = event.getMessage().getContentRaw().split(" ")[0];
        if (!event.getAuthor().isBot() && messageEvent.startsWith(RedBot.PREFIX)) {
            String[] messageArgs = StringUtils.parseArgsString(event.getMessage().getContentRaw().substring(messageEvent.length()).trim());
            Commands eventType = StringUtils.getEnumFromString(Commands.class, messageEvent.substring(1));
            runCommand(event, messageArgs, eventType, false);
        }
    }

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        ArrayList<String> arguments = new ArrayList<>();
        event.getOptions().forEach(optionMapping -> arguments.add(optionMapping.getAsString()));
        String[] messageArgs = new String[arguments.size()];
        messageArgs = arguments.toArray(messageArgs);
        if (messageArgs.length == 0) {
            messageArgs = new String[] {""};
        }
        Commands eventType = StringUtils.getEnumFromString(Commands.class, event.getName());
        runCommand(event, messageArgs, eventType, true);
    }

    private void runCommand(Object event, String[] messageArgs, Commands eventType, boolean isSlash) {
        if (eventType == null) {
            eventType = Commands.UNKNOWN;
        }
        switch (eventType) {
            case ADDICTION:
                break;
            case BANK:
                new BankCommand(messageArgs, event, isSlash);
                break;
            case BUYARMOR:
                new BuyArmorCommand(messageArgs, event, isSlash);
                break;
            case CHECK:
                new CheckCommand(messageArgs, event, isSlash);
                break;
            case COVERAGE:
                break;
            case FAME:
                new FameCommand(messageArgs, event, isSlash);
                break;
            case FIXER:
                break;
            case FIXERDEBT:
                break;
            case HOSPITAL:
                break;
            case HP:
                new HPCommand(messageArgs, event, isSlash);
                break;
            case HUM:
                new HumCommand(messageArgs, event, isSlash);
                break;
            case HUSTLE:
                new HustleCommand(messageArgs, event, isSlash);
                break;
            case IMPROVE:
                new ImproveCommand(messageArgs, event, isSlash);
                break;
            case INFO:
                new InfoCommand(messageArgs, event, isSlash);
                break;
            case INSTALL:
                new InstallCommand(messageArgs, event, isSlash);
                break;
            case LIFESTYLE:
                break;
            case MAXHUM:
                new MaxHumCommand(messageArgs, event, isSlash);
                break;
            case MEDTECH:
                break;
            case MONTHLY:
                break;
            case MOTO:
                break;
            case NANOHP:
                new NanoHPCommand(messageArgs, event, isSlash);
                break;
            case PROPERTY:
                break;
            case RENT:
                break;
            case SELECT:
                new SelectCommand(messageArgs, event, isSlash);
                break;
            case START:
                new StartCommand(messageArgs, event, isSlash);
                break;
            case STARTROLE:
                new StartRoleCommand(messageArgs, event, isSlash);
                break;
            case TEAM:
                break;
            case TECHIE:
                break;
            case TIMEZONE:
                new TimeZoneCommand(messageArgs, event, isSlash);
                break;
            case TRADE:
                new TradeCommand(messageArgs, event, isSlash);
                break;
            case TRAUMADEBT:
                break;
            case UPDATE:
                new UpdateCommand(messageArgs, event, isSlash);
                break;
            case UNKNOWN:
//                event.getChannel().sendMessage(event.getName() + " Is An Unrecognised Command").queue();
                break;
            case UPDATEBODYHP:
                new UpdateBodyHpCommand(messageArgs, event, isSlash);
                break;
        }
    }


//        if (event.getName().equalsIgnoreCase("check")) {
//            event.getOptions().size();
//            new CheckCommand(messageArgs, event);
//        }
//        if (!event.getName().equals("ping")) return; // make sure we handle the right command
//        long time = System.currentTimeMillis();
//        event.reply("Pong!").setEphemeral(true) // reply or acknowledge
//                .flatMap(v ->
//                        event.getHook().editOriginalFormat("Pong: %d ms", System.currentTimeMillis() - time) // then edit original
//                ).queue(); // Queue both reply and edit
//        }
}
