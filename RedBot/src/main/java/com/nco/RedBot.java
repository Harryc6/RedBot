package com.nco;

import com.nco.jobs.IncrementDownTimeJob;
import com.nco.jobs.ResetWeeklyGamesJob;
import com.nco.utils.ConfigVar;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.jetbrains.annotations.NotNull;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.quartz.impl.StdSchedulerFactory;

import static org.quartz.CronScheduleBuilder.dailyAtHourAndMinute;
import static org.quartz.CronScheduleBuilder.weeklyOnDayAndHourAndMinute;
import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;


import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.List;

public class RedBot {


    public static String PREFIX = "#";

    public static void main(String[] args) {
        try {
            JDA jda = JDABuilder.createDefault(ConfigVar.getDiscordToken())
                    .setActivity(Activity.playing("Cyberpunk Red"))
                    .setStatus(OnlineStatus.ONLINE)
                    .addEventListeners(new MessageListener())
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
                    .withIdentity("incrementDownTime", "group1")
                    .build();
            JobDetail resetWeeklyGames = newJob(ResetWeeklyGamesJob.class)
                    .withIdentity("resetWeeklyGames", "group2")
                    .build();

            // Trigger the job to run now, and then repeat every 40 seconds
            Trigger dailyTrigger = newTrigger()
                    .withIdentity("daily", "group1")
                    .startNow().withSchedule(dailyAtHourAndMinute(0, 0))
                    .build();
            Trigger weeklyTrigger = newTrigger()
                    .withIdentity("weekly", "group2")
                    .startNow().withSchedule(weeklyOnDayAndHourAndMinute(2,0,0))
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
        return commandDataList;
    }

}
