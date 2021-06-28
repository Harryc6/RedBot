package com.nco;

import com.nco.commands.*;
import com.nco.enums.Commands;
import com.nco.utils.StringUtils;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class MessageListener extends ListenerAdapter {
    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        List<CommandData> commandDataList = new ArrayList<>();

        commandDataList.add(new CommandData("bank", "Manage a characters bank")
                .addOption(OptionType.STRING, "reason", "Transaction Reason", true)
                .addOption(OptionType.INTEGER, "amount", "Change in bank balance", true)
                .addOption(OptionType.STRING, "pc-name", "Player characters name", false)
                .addOption(OptionType.INTEGER, "dt", "Downtime used", false));

        commandDataList.add(new CommandData("buyarmor", "Manage a characters armor")
                .addOption(OptionType.STRING, "armor-type", "What is the type of armor", true)
                .addOption(OptionType.STRING, "head-or-body", "Is armor for head or body", true)
                .addOption(OptionType.INTEGER, "sp", "Max sp of armor", true)
                .addOption(OptionType.INTEGER, "amount", "Amount spent on armor", true)
                .addOption(OptionType.STRING, "pc-name", "Player characters name", false));

        commandDataList.add(new CommandData("check", "See information on characters")
                .addOption(OptionType.STRING, "pc-name", "Player characters Name", false));

        commandDataList.add(new CommandData("fame", "Add fame to a character")
                .addOption(OptionType.STRING, "reason", "Reason for the fame increase", true)
                .addOption(OptionType.INTEGER, "amount", "Increase in fame", true)
                .addOption(OptionType.STRING, "pc-name", "Player characters name", false));

        commandDataList.add(new CommandData("hp", "Heal a character")
                .addOption(OptionType.INTEGER, "dt", "Downtime used", true)
                .addOption(OptionType.STRING, "pc-name", "Player characters name", false)
                .addOption(OptionType.STRING, "bonuses", "Bonuses to increase healing", false));

        commandDataList.add(new CommandData("hum", "Give a character therapy")
                .addOption(OptionType.STRING, "therapy-type", "Pro Standard or Pro Extreme", true)
                .addOption(OptionType.STRING, "pc-name", "Player characters name", false));

        commandDataList.add(new CommandData("hustle", "Hustle to earn eddies on with down time")
                .addOption(OptionType.INTEGER, "role-level", "Characters role level", true)
                .addOption(OptionType.INTEGER, "attempts", "Attempts at hustling", true)
                .addOption(OptionType.STRING, "pc-name", "Player characters name", false));

        commandDataList.add(new CommandData("improve", "Improve a characters skills")
                .addOption(OptionType.STRING, "skill-change", "Give Skill Name, Current Skill Level, New Skill Level", true)
                .addOption(OptionType.INTEGER, "ip-spent", "Number of IP spent", true)
                .addOption(OptionType.STRING, "pc-name", "Player characters name", false));

        commandDataList.add(new CommandData("info", "Show Redbot info"));

        commandDataList.add(new CommandData("install", "Install cyberware to a character")
                .addOption(OptionType.STRING, "product", "Product being installed", true)
                .addOption(OptionType.STRING, "dice", "Give the dice to roll in format Xd6/X+-X", true)
                .addOption(OptionType.STRING, "amount-or-paid", "Number of IP spent", true)
                .addOption(OptionType.STRING, "cyberware-or-borgware", "Is the product cyberware or borgware", true)
                .addOption(OptionType.STRING, "pc-name", "Player characters name", false));

        commandDataList.add(new CommandData("maxhum", "Restore max humanity when removing cyberware")
                .addOption(OptionType.STRING, "cyberware-or-borgware", "Are you removing cyberware or borgware", true)
                .addOption(OptionType.STRING, "pc-name", "Player characters name", false));

        commandDataList.add(new CommandData("nanohp", "Heal a character & their armor")
                .addOption(OptionType.INTEGER, "dt", "Downtime used", true)
                .addOption(OptionType.INTEGER, "max-sp", "Maximum SP of characters armour", true)
                .addOption(OptionType.STRING, "pc-name", "Player characters name", false)
                .addOption(OptionType.STRING, "bonuses", "Bonuses to increase healing", false));

        commandDataList.add(new CommandData("select", "See db information on characters")
                .addOption(OptionType.STRING, "pc-name", "Player characters Name", false));

        commandDataList.add(new CommandData("timezone", "Update players time zone")
                .addOption(OptionType.STRING, "timezone", "Enter UTC time zone", true)
                .addOption(OptionType.STRING, "pc-name", "Player characters Name", false));

        commandDataList.add(new CommandData("trade", "Trade eddies between characters")
                .addOption(OptionType.STRING, "sender", "Character sending eddies", true)
                .addOption(OptionType.STRING, "reason", "Reason for trade", true)
                .addOption(OptionType.INTEGER, "amount", "Amount of eddied traded", true)
                .addOption(OptionType.STRING, "recipient", "Character receiving eddies", true)
                .addOption(OptionType.INTEGER, "dt", "DT spent for trade", false));

        commandDataList.add(new CommandData("update", "See db information on characters")
                .addOption(OptionType.STRING, "pc-name", "Player characters Name", true)
                .addOption(OptionType.STRING, "column-name", "Column to update", true)
                .addOption(OptionType.STRING, "column-value", "Value to update column with", true));

        commandDataList.add(new CommandData("updatebodyhp", "See db information on characters")
                .addOption(OptionType.INTEGER, "body", "New body value", true)
                .addOption(OptionType.INTEGER, "hp", "New HP total", true)
                .addOption(OptionType.STRING, "reason", "Reason for update", true)
                .addOption(OptionType.STRING, "pc-name", "Player characters Name", false));

        for (Command command : event.getGuild().retrieveCommands().complete()) {
            event.getGuild().deleteCommandById(command.getId()).queue();
        }
        event.getGuild().updateCommands().addCommands(commandDataList).queue();


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
            case HP:
                addOptionIfFound(options, argsList, "pc-name");
                addOptionIfFound(options, argsList, "dt");
                addOptionIfFound(options, argsList, "bonuses");
                return listNull(argsList);
            case HUM:
                addOptionIfFound(options, argsList, "pc-name");
                addOptionIfFound(options, argsList, "therapy-type");
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
            case MAXHUM:
                addOptionIfFound(options, argsList, "pc-name");
                addOptionIfFound(options, argsList, "cyberware-or-borgware");
                return listNull(argsList);
            case MEDTECH:
                break;
            case MONTHLY:
                break;
            case MOTO:
                break;
            case NANOHP:
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
//            case STARTROLE:
//                new StartRoleCommand(messageArgs, event, isSlash);
//                break;
            case TEAM:
                break;
            case TECHIE:
                break;
            case TIMEZONE:
                addOptionIfFound(options, argsList, "pc-name");
                addOptionIfFound(options, argsList, "timezone");
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
