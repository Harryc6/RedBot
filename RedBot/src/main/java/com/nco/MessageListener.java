package com.nco;

import com.nco.commands.*;
import com.nco.enums.Commands;
import com.nco.utils.StringUtils;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


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
        Commands eventType = StringUtils.getEnumFromString(Commands.class, event.getName());
        String[] messageArgs = getSlashMessageArgs(event, eventType);
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
            case HEAL:
                new HealCommand(messageArgs, event, isSlash);
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
            case MEDTECH:
                break;
            case MONTHLY:
                break;
            case MOTO:
                break;
            case NANOHEAL:
                new NanoHealCommand(messageArgs, event, isSlash);
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
            case TEAM:
                break;
            case TECHIE:
                break;
            case THERAPY:
                new TherapyCommand(messageArgs, event, isSlash);
                break;
            case TRADE:
                new TradeCommand(messageArgs, event, isSlash);
                break;
            case TRAUMADEBT:
                break;
            case UPDATE:
                new UpdateCommand(messageArgs, event, isSlash);
                break;
            case UNINSTALL:
                new UninstallCommand(messageArgs, event, isSlash);
                break;
            case UNKNOWN:
//                event.getChannel().sendMessage(event.getName() + " Is An Unrecognised Command").queue();
                break;
            case UPDATEBODYHP:
                new UpdateBodyHpCommand(messageArgs, event, isSlash);
                break;
        }
    }

    private String[] getSlashMessageArgs(SlashCommandEvent event, Commands eventType) {
        List<OptionMapping> options = event.getOptions();
        List<String> argsList = new ArrayList<>();

        if (eventType == null) {
            eventType = Commands.UNKNOWN;
        }
        switch (eventType) {
            case ADDICTION:
                break;
            case BANK:
                addOptionIfFound(options, argsList, "pc-name");
                addOptionIfFound(options, argsList, "reason");
                addOptionIfFound(options, argsList, "amount");
                addOptionIfFound(options, argsList, "dt");
                return listNull(argsList);
            case BUYARMOR:
                addOptionIfFound(options, argsList, "pc-name");
                addOptionIfFound(options, argsList, "armor-type");
                addOptionIfFound(options, argsList, "head-or-body");
                addOptionIfFound(options, argsList, "sp");
                addOptionIfFound(options, argsList, "amount");
                return listNull(argsList);
            case CHECK:
                addOptionIfFound(options, argsList, "pc-name");
                return listNull(argsList);
            case COVERAGE:
                break;
            case FAME:
                addOptionIfFound(options, argsList, "pc-name");
                addOptionIfFound(options, argsList, "reason");
                addOptionIfFound(options, argsList, "amount");
                return listNull(argsList);
            case FIXER:
                break;
            case FIXERDEBT:
                break;
            case HOSPITAL:
                break;
            case HEAL:
                addOptionIfFound(options, argsList, "pc-name");
                addOptionIfFound(options, argsList, "dt");
                addOptionIfFound(options, argsList, "bonuses");
                return listNull(argsList);
            case HUSTLE:
                addOptionIfFound(options, argsList, "pc-name");
                addOptionIfFound(options, argsList, "role-level");
                addOptionIfFound(options, argsList, "attempts");
                return listNull(argsList);
            case IMPROVE:
                addOptionIfFound(options, argsList, "pc-name");
                addOptionIfFound(options, argsList, "skill-change");
                addOptionIfFound(options, argsList, "ip-spent");
                return listNull(argsList);
            case INFO:
                return listNull(argsList);
            case INSTALL:
                addOptionIfFound(options, argsList, "pc-name");
                addOptionIfFound(options, argsList, "product");
                addOptionIfFound(options, argsList, "dice");
                addOptionIfFound(options, argsList, "amount-or-paid");
                addOptionIfFound(options, argsList, "cyberware-or-borgware");
                return listNull(argsList);
            case LIFESTYLE:
                break;
            case MEDTECH:
                break;
            case MONTHLY:
                break;
            case MOTO:
                break;
            case NANOHEAL:
                addOptionIfFound(options, argsList, "pc-name");
                addOptionIfFound(options, argsList, "dt");
                addOptionIfFound(options, argsList, "max-sp");
                addOptionIfFound(options, argsList, "bonuses");
                return listNull(argsList);
            case PROPERTY:
                break;
            case RENT:
                break;
            case SELECT:
                addOptionIfFound(options, argsList, "pc-name");
                return listNull(argsList);
            case START:
                //Unable to do Start command due to no support for attachments on slash command
                break;
            case TEAM:
                break;
            case TECHIE:
                break;
            case THERAPY:
                addOptionIfFound(options, argsList, "pc-name");
                addOptionIfFound(options, argsList, "therapy-type");
                return listNull(argsList);
            case TRADE:
                addOptionIfFound(options, argsList, "sender");
                addOptionIfFound(options, argsList, "reason");
                addOptionIfFound(options, argsList, "amount");
                addOptionIfFound(options, argsList, "recipient");
                addOptionIfFound(options, argsList, "dt");
                return listNull(argsList);
            case TRAUMADEBT:
                break;
            case UPDATE:
                addOptionIfFound(options, argsList, "pc-name");
                addOptionIfFound(options, argsList, "column-name");
                addOptionIfFound(options, argsList, "column-value");
                return listNull(argsList);
            case UNINSTALL:
                addOptionIfFound(options, argsList, "pc-name");
                addOptionIfFound(options, argsList, "cyberware-or-borgware");
                return listNull(argsList);
            case UNKNOWN:
                return new String[] {""};
            case UPDATEBODYHP:
                addOptionIfFound(options, argsList, "pc-name");
                addOptionIfFound(options, argsList, "body");
                addOptionIfFound(options, argsList, "hp");
                addOptionIfFound(options, argsList, "reason");
                return listNull(argsList);
        }
        return new String[] {""};
    }

    private String[] listNull(List<String> argsList) {
        if (argsList.size() == 0) {
            return new String[]{""};
        } else {
            return argsList.toArray(new String[0]);
        }
    }

    private void addOptionIfFound(List<OptionMapping> options, List<String> argsList, String option) {
        if (options.stream().anyMatch(optionMapping -> optionMapping.getName().equals(option))) {
            argsList.add(options.stream().filter(optionMapping -> optionMapping.getName().equals(option)).findFirst().get().getAsString());
        }
    }
}
