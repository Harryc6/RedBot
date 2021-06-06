package com.nco.commands;

import com.nco.RedBot;
import com.nco.pojos.PlayerCharacter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;

public class TimeZoneCommand extends AbstractCommand {

    private final static String[] UTCArray = { "UTC-12", "UTC-11", "UTC-10", "UTC-9:30", "UTC-9", "UTC-8", "UTC-7", "UTC-6", "UTC-5", "UTC-4", "UTC-3:30", "UTC-3", "UTC-2", "UTC-1", "UTC+0", "UTC+1", "UTC+2", "UTC+3", "UTC+3:30", "UTC+4", "UTC+4:30", "UTC+5", "UTC+5:30", "UTC+5:45", "UTC+6", "UTC+6:30", "UTC+7", "UTC+8", "UTC+8:45", "UTC+9", "UTC+9:30", "UTC+10", "UTC+10:30", "UTC+11", "UTC+12", "UTC+12:45", "UTC+13", "UTC+14" };

    public TimeZoneCommand(String[] messageArgs, User author, MessageChannel channel, Member member) {
        super(messageArgs, author, channel, member);
    }

    @Override
    protected boolean canProcessByUser() {
        return messageArgs.length == 1 && Arrays.stream(UTCArray).anyMatch(s -> messageArgs[0].equalsIgnoreCase(s));
    }

    @Override
    protected boolean canProcessByName() {
        return messageArgs.length == 2 && Arrays.stream(UTCArray).anyMatch(s -> messageArgs[1].equalsIgnoreCase(s));
    }

    @Override
    protected void processUpdateAndRespond(Connection conn, PlayerCharacter pc, EmbedBuilder builder) throws SQLException {
        if (updateTimeZone(conn) && insertTimeZone(conn, pc)) {
            builder.setTitle(messageArgs[0] + "'s Time Zone Updated");
            builder.setDescription("For \"" + messageArgs[1] + "\"");
            builder.addField("Old Time Zone", pc.getTimeZone(), true);
            builder.addBlankField(true);
            builder.addField("New Time Zone", messageArgs[1], true);
        } else {
            builder.setTitle("ERROR: Bank Update Or Insert Failure");
            builder.setDescription("Please contact an administrator to get this resolved");
        }
    }

    private boolean updateTimeZone(Connection conn) throws SQLException {
        String sql = "UPDATE NCO_PC set time_zone = ? Where character_name = ?";
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setString(1, messageArgs[1]);
            stat.setString(2, messageArgs[0]);
            return stat.executeUpdate() == 1;
        }
    }

    private boolean insertTimeZone(Connection conn, PlayerCharacter pc) throws SQLException {
        String sql = "INSERT INTO nco_time_zone (character_name, old_time_zone, new_time_zone, created_by) VALUES (?,?,?,?)";
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setString(1, messageArgs[0]);
            stat.setString(2, pc.getTimeZone());
            stat.setString(3, messageArgs[1]);
            stat.setString(4, author.getAsTag());
            return stat.executeUpdate() == 1;
        }
    }

    @Override
    protected String getHelpTitle() {
        return "Incorrect Time Zone Formatting";
    }

    @Override
    protected String getHelpDescription() {
        return "Please use the commands below to update your timezone. \n" + RedBot.PREFIX +
                "timezone \"PC Name(Optional)\" \"UTC-12 - UTC+14\"";
    }

}
