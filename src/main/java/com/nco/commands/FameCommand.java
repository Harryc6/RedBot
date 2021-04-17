package com.nco.commands;

import com.nco.RedBot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FameCommand extends AbstractCommand {

    public FameCommand(String[] messageArgs, User author, MessageChannel channel) {
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
    protected void processUpdateAndRespond(Connection conn, ResultSet rs, EmbedBuilder builder) throws SQLException {
        if (updateFame(rs.getInt("Fame"), conn) && insertFame(conn)) {
            int oldFame = rs.getInt("Fame");
            int newFame = oldFame + Integer.parseInt(messageArgs[2]);
            int oldReputation = rs.getInt("Reputation");
            int newReputation = newFame / 20;

            builder.setTitle(messageArgs[0] + "'s Fame Updated");
            builder.setDescription("For \"" + messageArgs[1] + "\"");
            builder.addField("Old Fame", String.valueOf(oldFame), true);
            builder.addBlankField(true);
            builder.addField("New Fame", String.valueOf(newFame), true);

            if (oldReputation != newReputation) {
                builder.addField("Old Reputation", rs.getString("Reputation"), true);
                builder.addField("New Reputation", String.valueOf(newReputation), true);
            }
        } else {
            builder.setTitle("ERROR: Fame Update Or Insert Failure");
            builder.setDescription("Please contact an administrator to get this resolved");
        }
    }

    private boolean updateFame(int currentFame, Connection conn) throws SQLException {
        int newFameTotal = currentFame + Integer.parseInt(messageArgs[2]);
        int newReputation = newFameTotal / 20;
        String sql = "UPDATE NCO_PC set Fame = ?, Reputation = ? Where CharacterName = ?";
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setInt(1, newFameTotal);
            stat.setInt(2, newReputation);
            stat.setString(3, messageArgs[0]);
            return stat.executeUpdate() == 1;
        }
    }

    private boolean insertFame(Connection conn) throws SQLException {
        String sql = "INSERT INTO NCO_FAME (CharacterName, Reason, Fame, CreatedBy) VALUES (?,?,?,?)";
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
        return "Incorrect Fame Formatting";
    }

    @Override
    protected String getHelpDescription() {
        return "Please use the commands below to add fame onto a characters \n" + RedBot.PREFIX +
                "fame \"PC Name(Optional)\" \"Reason\" \"Amount\"";
    }
}
