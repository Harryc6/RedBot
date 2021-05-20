package com.nco.commands;

import com.nco.RedBot;
import com.nco.pojos.PlayerCharacter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ImproveCommand extends AbstractCommand {

    public ImproveCommand(String[] messageArgs, User author, MessageChannel channel) {
        super(messageArgs, author, channel);
    }

    @Override
    protected boolean canProcessByUser() {
        return messageArgs.length == 2;
    }

    @Override
    protected boolean canProcessByName() {
        return messageArgs.length == 3;
    }



    @Override
    protected void processUpdateAndRespond(Connection conn, PlayerCharacter pc, EmbedBuilder builder) throws SQLException {
        if (updateImprove(pc, conn) && insertImprove(conn)) {
            int changeIP = Integer.parseInt(messageArgs[2]);
            int oldIP = pc.getInfluencePoints();
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

    private boolean updateImprove(PlayerCharacter pc, Connection conn) throws SQLException {
        int changeIP = Integer.parseInt(messageArgs[2]);
        int oldIP = pc.getInfluencePoints();
        int newIP = oldIP - (changeIP < 0 ? -changeIP : changeIP);

        String sql = "UPDATE NCO_PC set influence_points = ? Where character_name = ?";

        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setInt(1, newIP);
            stat.setString(2, messageArgs[0]);
            return stat.executeUpdate() == 1;
        }
    }

    private boolean insertImprove(Connection conn) throws SQLException {
        String sql  = "INSERT INTO NCO_IMPROVE (character_name, reason, influence_points, created_by) VALUES (?,?,?,?)";
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setString(1, messageArgs[0]);
            stat.setString(2, messageArgs[1]);
            stat.setString(3, messageArgs[2]);
            stat.setString(4, author.getAsTag());
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
                "improve \"PC Name(Optional)\" \"Skill Name, Current Skill Level, New Skill Level\" \"Amount\"";
    }

}
