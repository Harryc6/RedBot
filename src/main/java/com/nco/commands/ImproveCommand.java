package com.nco.commands;

import com.nco.RedBot;
import com.nco.pojos.PlayerCharacter;
import com.nco.utils.NumberUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ImproveCommand extends AbstractCommand {

    public ImproveCommand(String[] messageArgs, User author, MessageChannel channel, Member member) {
        super(messageArgs, author, channel, member);
    }

    @Override
    protected boolean canProcessByUser() {
        return messageArgs.length == 2 && NumberUtils.isNumeric(messageArgs[1]);
    }

    @Override
    protected boolean canProcessByName() {
        return messageArgs.length == 3 && NumberUtils.isNumeric(messageArgs[2]);
    }



    @Override
    protected void processUpdateAndRespond(Connection conn, PlayerCharacter pc, EmbedBuilder builder) throws SQLException {
        int newIP = pc.getInfluencePoints() - NumberUtils.asPositive(messageArgs[2]);
        if (newIP < 0) {
            builder.setTitle("ERROR: Not Enough IP");
            builder.setDescription(messageArgs[0] + " has only " + pc.getInfluencePoints() + " IP available " +
                    "where " + NumberUtils.asPositive(messageArgs[2]) + " IP is being used.");
        } else if (updateImprove(newIP, conn) && insertImprove(conn)) {
            buildEmbeddedContent(pc, builder, newIP);
        } else {
            builder.setTitle("ERROR: Improve Update Or Insert Failure");
            builder.setDescription("Please contact an administrator to get this resolved");
        }
    }

    private boolean updateImprove(int newIP, Connection conn) throws SQLException {
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
            stat.setInt(3, Integer.parseInt(messageArgs[2]));
            stat.setString(4, author.getAsTag());
            return stat.executeUpdate() == 1;
        }
    }

    private void buildEmbeddedContent(PlayerCharacter pc, EmbedBuilder builder, int newIP) {
        builder.setTitle(messageArgs[0] + "'s IP Updated");
        builder.setDescription("For \"" + messageArgs[1] + "\"");
        builder.addField("Old IP", String.valueOf(pc.getInfluencePoints()), true);
        builder.addBlankField(true);
        builder.addField("New IP", String.valueOf(newIP), true);
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
