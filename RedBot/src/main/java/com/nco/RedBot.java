package com.nco;

import com.nco.jobs.IncrementDownTimeJob;
import com.nco.utils.ConfigVar;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.quartz.impl.StdSchedulerFactory;

import static org.quartz.CronScheduleBuilder.dailyAtHourAndMinute;
import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;


import javax.security.auth.login.LoginException;

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
            JobDetail job = newJob(IncrementDownTimeJob.class)
                    .withIdentity("job1", "group1")
                    .build();

            // Trigger the job to run now, and then repeat every 40 seconds
            Trigger trigger = newTrigger()
                    .withIdentity("trigger1", "group1")
                    .startNow().withSchedule(dailyAtHourAndMinute(19, 0))
                    .build();

            // Tell quartz to schedule the job using our trigger
            scheduler.scheduleJob(job, trigger);

//            Thread.sleep(60000);
//            scheduler.shutdown();

        } catch (SchedulerException se) {
            se.printStackTrace();
        }
    }

    private static void insertSlashCommands(JDA jda) {
        for (Guild guild : jda.getGuilds()) {
            CommandData commandData = new CommandData("check", "checking something").addOption(OptionType.STRING, "pc-name", "Player characters Name", false);
            guild.upsertCommand(commandData).submit();
        }
    }

}
