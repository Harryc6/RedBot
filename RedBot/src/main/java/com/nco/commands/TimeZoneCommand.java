package com.nco.commands;

import com.nco.RedBot;
import com.nco.pojos.PlayerCharacter;
import com.nco.utils.NCOUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Deprecated //No reason to store timezone
public class TimeZoneCommand extends AbstractCommand {

    private final static String[] UTCArray = NCOUtils.getUTCArray();

    public TimeZoneCommand(String[] messageArgs, Object event, boolean isSlash) {
        super(messageArgs, event, isSlash);
    }

    @Override
    protected boolean canProcessByUser() {
        return messageArgs.length == 1 && NCOUtils.validUTC(messageArgs[0]);
    }

    @Override
    protected boolean canProcessByName() {
        return messageArgs.length == 2 && NCOUtils.validUTC(messageArgs[1]);
    }

    @Override
    protected void processUpdateAndRespond(Connection conn, PlayerCharacter pc, EmbedBuilder builder) throws SQLException {
        if (updateTimeZone(conn) && insertTimeZone(conn, pc)) {
            builder.setTitle(messageArgs[0] + "'s Time Zone Updated");
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
