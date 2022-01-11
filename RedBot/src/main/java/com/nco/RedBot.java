package com.nco;

import com.nco.enums.Skills;
import com.nco.jobs.IncrementDownTimeJob;
import com.nco.jobs.ResetWeeklyGamesJob;
import com.nco.utils.ConfigVar;
import com.nco.utils.StringUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.quartz.impl.StdSchedulerFactory;

import static org.quartz.CronScheduleBuilder.*;
import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;


import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RedBot {


    public static String PREFIX = "#";

    public static void main(String[] args) {
        try {
            JDA jda = JDABuilder.createDefault(ConfigVar.getDiscordToken())
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .setActivity(Activity.playing("Cyberpunk Red"))
                    .setStatus(OnlineStatus.ONLINE)
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .addEventListeners(new MessageListener())
                    .enableIntents(GatewayIntent.GUILD_MEMBERS)
                    .build();
            insertSlashCommands(jda);
            jda.awaitReady();
            Logger logger = LoggerFactory.getLogger(RedBot.class);
            logger.info("Finished Setting Up JDA For RedBot");
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }

        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            // and start it off
            scheduler.start();
            // define the job and tie it to our HelloJob class
            JobDetail incrementDownTime = newJob(IncrementDownTimeJob.class)
                    .withIdentity("incrementDownTime")
                    .build();

            JobDetail resetWeeklyGames = newJob(ResetWeeklyGamesJob.class)
                    .withIdentity("resetWeeklyGames")
                    .build();

            // Trigger the job to run now, and then repeat every 40 seconds
            Trigger dailyTrigger = newTrigger()
                    .withIdentity("daily")
                    .startNow().withSchedule(dailyAtHourAndMinute(0, 0))
                    .build();
            Trigger weeklyTrigger = newTrigger()
                    .withIdentity("weekly")
                    .startNow().withSchedule(weeklyOnDayAndHourAndMinute(2,0,0))
                    .build();
            Trigger quarterHourTrigger = newTrigger()
                    .withIdentity("quarterHour")
                    .startNow().withSchedule(cronSchedule("0 0/15 * * * ?"))
                    .build();

            // Tell quartz to schedule the job using our trigger
            scheduler.scheduleJob(incrementDownTime, dailyTrigger);
            scheduler.scheduleJob(resetWeeklyGames, weeklyTrigger);

//            Thread.sleep(60000);
//            scheduler.shutdown();

        } catch (SchedulerException se) {
            se.printStackTrace();
        }
    }

    private static void insertSlashCommands(JDA jda) {
        jda.updateCommands().addCommands(getCommandData()).queue();
    }

    private static List<CommandData> getCommandData() {
        List<CommandData> commandDataList = new ArrayList<>();

        commandDataList.add(new CommandData("bank", "Manage a characters bank")
                .addOption(OptionType.STRING, "reason", "Transaction Reason", true)
                .addOption(OptionType.INTEGER, "amount", "Change in bank balance", true)
                .addOption(OptionType.STRING, "pc-name", "Player characters name", false)
                .addOption(OptionType.INTEGER, "dt", "Downtime used", false));

        commandDataList.add(new CommandData("buyarmor", "Manage a characters armor")
                .addOption(OptionType.STRING, "armor-type", "What is the type of armor", true)
                .addOptions(getHeadOrBodyOptionData("Is armor for head or body"))
                .addOption(OptionType.INTEGER, "sp", "Max sp of armor", true)
                .addOption(OptionType.INTEGER, "amount", "Amount spent on armor", true)
                .addOption(OptionType.STRING, "pc-name", "Player characters name", false));

        commandDataList.add(new CommandData("check", "See information on characters")
                .addOption(OptionType.STRING, "pc-name", "Player characters Name", false));

        commandDataList.add(new CommandData("fame", "Add fame to a character")
                .addOption(OptionType.STRING, "reason", "Reason for the fame increase", true)
                .addOption(OptionType.INTEGER, "amount", "Increase in fame", true)
                .addOption(OptionType.STRING, "pc-name", "Player characters name", false));

        commandDataList.add(new CommandData("gigcreate", "Create a gig")
                .addOption(OptionType.STRING, "gig-name", "Title of the gig", true)
                .addOption(OptionType.STRING, "referee", "The referees discord tag", false));

        commandDataList.add(new CommandData("gigcrit", "Give a character a critical injury")
                .addOptions(getHeadOrBodyOptionData("Was the head or body table rolled against"))
                .addOption(OptionType.INTEGER, "rolled-value", "Value rolled on the selected table", true)
                .addOption(OptionType.STRING, "pc-name", "Player characters name", false));

        commandDataList.add(new CommandData("gigend", "Mark a gig as complete")
                .addOption(OptionType.INTEGER, "gig-id", "The ID of the gig", true)
                .addOption(OptionType.INTEGER, "ref-eddies", "Eddies for the referee", true)
                .addOption(OptionType.INTEGER, "ref-ip", "IP for the referee", true));

        commandDataList.add(new CommandData("giglog", "Log a characters changes from a gig")
                .addOption(OptionType.INTEGER, "gig-id", "The ID of the gig", true)
                .addOption(OptionType.INTEGER, "eddies", "Eddies earned for the gig", true)
                .addOption(OptionType.INTEGER, "ip", "IP earned for the gig", true)
                .addOption(OptionType.INTEGER, "current-hp", "HP at the end of the gig", true)
                .addOption(OptionType.INTEGER, "head-sp", "Head SP at gig end", true)
                .addOption(OptionType.INTEGER, "body-sp", "Body SP at gig end", true)
                .addOption(OptionType.INTEGER, "fame", "Fame gained for gig", true)
                .addOption(OptionType.STRING, "fame-reason", "Action to earn fame", true)
                .addOption(OptionType.STRING, "pc-name", "Player characters name", false));

        commandDataList.add(new CommandData("heal", "Heal a character")
                .addOption(OptionType.INTEGER, "dt", "Downtime used", true)
                .addOption(OptionType.STRING, "pc-name", "Player characters name", false)
                .addOption(OptionType.STRING, "bonuses", "Bonuses to increase healing", false));

        commandDataList.add(new CommandData("hustle", "Hustle to earn eddies on with down time")
                .addOption(OptionType.INTEGER, "role-level", "Characters role level", true)
                .addOption(OptionType.INTEGER, "attempts", "Attempts at hustling", true)
                .addOption(OptionType.STRING, "pc-name", "Player characters name", false));

        commandDataList.add(new CommandData("improve", "Improve a characters skills")
                .addSubcommands(getImproveSubcommand()));

        commandDataList.add(new CommandData("info", "Show Redbot info"));

        commandDataList.add(new CommandData("install", "Install cyberware to a character")
                .addOption(OptionType.STRING, "product", "Product being installed", true)
                .addOption(OptionType.STRING, "dice", "Give the dice to roll in format Xd6/X+-X", true)
                .addOption(OptionType.STRING, "amount-or-paid", "Number of IP spent", true)
                .addOption(OptionType.STRING, "cyberware-or-borgware", "Is the product cyberware or borgware", true)
                .addOption(OptionType.STRING, "pc-name", "Player characters name", false));

        commandDataList.add(new CommandData("nanoheal", "Heal a character & their armor")
                .addOption(OptionType.INTEGER, "dt", "Downtime used", true)
                .addOption(OptionType.INTEGER, "max-sp", "Maximum SP of characters armour", true)
                .addOption(OptionType.STRING, "pc-name", "Player characters name", false)
                .addOption(OptionType.STRING, "bonuses", "Bonuses to increase healing", false));

        commandDataList.add(new CommandData("select", "See db information on characters")
                .addOption(OptionType.STRING, "pc-name", "Player characters Name", true)
                .addOptions(getTrueOrFalse("show-all", "Show all of a characters data", false)));

        commandDataList.add(new CommandData("therapy", "Give a character therapy")
                .addOption(OptionType.STRING, "therapy-type", "Pro Standard or Pro Extreme", true)
                .addOption(OptionType.STRING, "pc-name", "Player characters name", false));

        commandDataList.add(new CommandData("trade", "Trade eddies between characters")
                .addOption(OptionType.STRING, "sender", "Character sending eddies", true)
                .addOption(OptionType.STRING, "reason", "Reason for trade", true)
                .addOption(OptionType.INTEGER, "amount", "Amount of eddied traded", true)
                .addOption(OptionType.STRING, "recipient", "Character receiving eddies", true)
                .addOption(OptionType.INTEGER, "dt", "DT spent for trade", false));

        commandDataList.add(new CommandData("uninstall", "Restore max humanity when removing cyberware")
                .addOption(OptionType.STRING, "cyberware-or-borgware", "Are you removing cyberware or borgware", true)
                .addOption(OptionType.STRING, "pc-name", "Player characters name", false));

        commandDataList.add(new CommandData("update", "Update db information of a characters")
                .addOption(OptionType.STRING, "pc-name", "Player characters Name", true)
                .addOption(OptionType.STRING, "column-name", "Column to update", true)
                .addOption(OptionType.STRING, "column-value", "Value to update column with", true));

        commandDataList.add(new CommandData("updatebodyhp", "Manage a characters body & max HP")
                .addOption(OptionType.INTEGER, "body", "New body value", true)
                .addOption(OptionType.INTEGER, "hp", "New HP total", true)
                .addOption(OptionType.STRING, "reason", "Reason for update", true)
                .addOption(OptionType.STRING, "pc-name", "Player characters Name", false));
        return commandDataList;
    }

    private static OptionData getHeadOrBodyOptionData(String description) {
        return new OptionData(OptionType.STRING, "head-or-body", description, true)
                .addChoice("Head", "Head").addChoice("Body", "Body");
    }

    private static OptionData getTrueOrFalse(String name, String description, boolean isRequired) {
        return new OptionData(OptionType.STRING, name, description, isRequired).addChoices()
                .addChoice("True", "True").addChoice("False", "False");
    }

    private static Collection<Command.Choice> getSkillChoices() {
        return Arrays.stream(Skills.values()).map(skills -> {
            String s = StringUtils.snakeToCapitalizedWords(skills.toString());
            return new Command.Choice(s, s);
        }).collect(Collectors.toList());
    }

    private static Collection<Command.Choice> getSkillChoicesAToD() {
        return getSkillChoices().stream().filter(choice -> choice.getName().matches("^[a-dA-D].*")).collect(Collectors.toList());
    }

    private static Collection<Command.Choice> getSkillChoicesEToM() {
        return getSkillChoices().stream().filter(choice -> choice.getName().matches("^[e-mE-M].*")).collect(Collectors.toList());
    }

    private static Collection<Command.Choice> getSkillChoicesPToS() {
        return getSkillChoices().stream().filter(choice -> choice.getName().matches("^[p-sP-S].*")).collect(Collectors.toList());
    }

    private static Collection<Command.Choice> getSkillChoicesTToZ() {
        return getSkillChoices().stream().filter(choice -> choice.getName().matches("^[t-zT-Z].*")).collect(Collectors.toList());
    }

    private static SubcommandData[] getImproveSubcommand() {
        return new SubcommandData[] {
                new SubcommandData("skill-a-to-d", "Improve a characters skills")
                        .addOptions(new OptionData(OptionType.STRING, "skill-a-to-d", "Give Skill Name", true).addChoices(getSkillChoicesAToD()))
                        .addOption(OptionType.INTEGER, "ip", "The IP cost to upgrade skill", true)
                        .addOption(OptionType.STRING, "pc-name", "Player characters name", false),
                new SubcommandData("skill-e-to-m", "Improve a characters skills")
                        .addOptions(new OptionData(OptionType.STRING, "skill-e-to-m", "Give Skill Name", true).addChoices(getSkillChoicesEToM()))
                        .addOption(OptionType.INTEGER, "ip", "The IP cost to upgrade skill", true)
                        .addOption(OptionType.STRING, "pc-name", "Player characters name", false),
                new SubcommandData("skill-p-to-s", "Improve a characters skills")
                        .addOptions(new OptionData(OptionType.STRING, "skill-p-to-s", "Give Skill Name", true).addChoices(getSkillChoicesPToS()))
                        .addOption(OptionType.INTEGER, "ip", "The IP cost to upgrade skill", true)
                        .addOption(OptionType.STRING, "pc-name", "Player characters name", false),
                new SubcommandData("skill-t-to-z", "Improve a characters skills")
                        .addOptions(new OptionData(OptionType.STRING, "skill-t-to-z", "Give Skill Name", true).addChoices(getSkillChoicesTToZ()))
                        .addOption(OptionType.INTEGER, "ip", "The IP cost to upgrade skill", true)
                        .addOption(OptionType.STRING, "pc-name", "Player characters name", false)
        };
    }

}
