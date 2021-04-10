package com.nco.events;

import com.nco.RedBot;
import net.dv8tion.jda.api.EmbedBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ImproveEvent extends AbstractEvent {

    @Override
    protected boolean canProcessByUser(String[] messageArgs) {
        return messageArgs.length == 2;
    }

    @Override
    protected boolean canProcessByName(String[] messageArgs) {
        return messageArgs.length == 3;
    }



    @Override
    protected void processUpdateAndRespond(Connection conn, ResultSet rs, String[] messageArgs, EmbedBuilder builder) throws SQLException {
        if (updateImprove(messageArgs, rs, conn) && insertImprove(messageArgs, conn)) {
            int changeIP = Integer.parseInt(messageArgs[2]);
            int oldIP = rs.getInt("InfluencePoints");
            int newIP = oldIP - (changeIP < 0 ? -changeIP : changeIP);

            builder.setTitle(messageArgs[0] + "'s IP Updated");
            builder.setDescription("For \"" + messageArgs[1] + "\"");
            builder.addField("Old IP", String.valueOf(oldIP), true);
            builder.addBlankField(true);
            builder.addField("New IP", String.valueOf(newIP), true);

        } else {
            builder.setTitle("ERROR: Improve Update Or Insert Failure");
            builder.setDescription("Please contact an administrator to get this resolved");
        }
    }

    private static boolean updateImprove(String[] messageArg, ResultSet rs, Connection conn) throws SQLException {
        int changeIP = Integer.parseInt(messageArg[2]);
        int oldIP = rs.getInt("InfluencePoints");
        int newIP = oldIP - (changeIP < 0 ? -changeIP : changeIP);

        String sql = "UPDATE NCO_PC set InfluencePoints = ? Where CharacterName = ?";

        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setInt(1, newIP);
            stat.setString(2, messageArg[0]);
            return stat.executeUpdate() == 1;
        }
    }

    private static boolean insertImprove(String[] messageArg, Connection conn) throws SQLException {
        String sql  = "INSERT INTO NCO_IMPROVE (CharacterName, Reason, InfluencePoints) VALUES (?,?,?)";
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setString(1, messageArg[0]);
            stat.setString(2, messageArg[1]);
            stat.setString(3, messageArg[2]);
            return stat.executeUpdate() == 1;
        }
    }

    @Override
    protected String getHelpTitle() {
        return "Incorrect Improve Formatting";
    }

    @Override
    protected String getHelpDescription() {
        return "Please use the commands below to improve a characters bank\n" + RedBot.PREFIX +
                "improve \"PC Name\" \"Skill Name, Current Skill Level, New Skill Level\" \"Amount\" \nor \n"
                + RedBot.PREFIX + "improve \"Skill Name, Current Skill Level, New Skill Level\" \"Amount\"";
    }

}
