package com.nco;

import com.nco.utils.ConfigVar;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;

public class RedBot {


    public static String PREFIX = "#";

    public static void main(String[] args) {
        try {
            JDA jda = JDABuilder.createDefault(ConfigVar.getDiscordToken())
                    .addEventListeners(new MessageListener())
                    .build();
            jda.getPresence().setStatus(OnlineStatus.ONLINE);
            jda.getPresence().setActivity(Activity.playing("Cyberpunk Red"));
            jda.awaitReady();
            Logger logger = LoggerFactory.getLogger(RedBot.class);
            logger.info("Finished Setting Up JDA For RedBot");
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
