package com.nco.commands;

import com.nco.RedBot;
import com.nco.utils.NumberUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UpdateBodyHpCommand extends AbstractCommand {

    public UpdateBodyHpCommand(String[] messageArgs, User author, MessageChannel channel) {
        super(messageArgs, author, channel);
    }

    @Override
    protected boolean canProcessByUser() {
        return messageArgs.length == 3;
    }

    @Override
    protected boolean canProcessByName() {
        return messageArgs.length == 4;
    }

    @Override
    protected void processUpdateAndRespond(Connection conn, ResultSet rs, EmbedBuilder builder) throws SQLException {
        if (updateBodyHp(conn) && insertBodyHp(conn, rs)) {

            builder.setTitle(messageArgs[0] + "'s Body & HP Updated");
            builder.setDescription("For \"" + messageArgs[3] + "\"");
            builder.addField("Old Body", rs.getString("BodyScore"), true);
            builder.addBlankField(true);
            builder.addField("New Body", messageArgs[1], true);

            builder.addField("Old Max HP", rs.getString("MaxHP"), true);
            builder.addBlankField(true);
            builder.addField("New Max HP", messageArgs[2], true);

        } else {
            builder.setTitle("ERROR: Improve Update Or Insert Failure");
            builder.setDescription("Please contact an administrator to get this resolved");
        }
    }

    private boolean updateBodyHp(Connection conn) throws SQLException {
        String sql = "UPDATE NCO_PC set BodyScore = ?, MaxHP = ? Where CharacterName = ?";
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setString(1, messageArgs[1]);
            stat.setString(2, messageArgs[2]);
            stat.setString(3, messageArgs[0]);
            return stat.executeUpdate() == 1;
        }
    }

    private boolean insertBodyHp(Connection conn, ResultSet rs) throws SQLException {
        String sql  = "INSERT INTO NCO_BODY_HP_UPDATE (CharacterName, OldBody, NewBody, OldMaxHP, NewMaxHP, Reason, CreatedBy) VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setString(1, messageArgs[0]);
            stat.setString(2, rs.getString("BodyScore"));
            stat.setString(3, messageArgs[1]);
            stat.setString(4, rs.getString("MaxHP"));
            stat.setString(5, messageArgs[2]);
            stat.setString(6, messageArgs[3]);
            stat.setString(7, author.getAsTag());
            return stat.executeUpdate() == 1;
        }
    }

    @Override
    protected String getHelpTitle() {
        return "Incorrect Updating Body & HP Formatting";
    }

    @Override
    protected String getHelpDescription() {
        return "Please use the commands below to manage a characters body & max HP\n" + RedBot.PREFIX +
                "updatebodyhp \"PC Name(Optional)\" \"New Body Score\" \"New HP Score\" \"Reason\"";
    }
}
