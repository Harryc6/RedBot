package com.nco.commands;

import com.nco.RedBot;
import com.nco.pojos.PlayerCharacter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
    protected void processUpdateAndRespond(Connection conn, PlayerCharacter pc, EmbedBuilder builder) throws SQLException {
        if (updateBodyHp(conn) && insertBodyHp(conn, pc)) {

            builder.setTitle(messageArgs[0] + "'s Body & HP Updated");
            builder.setDescription("For \"" + messageArgs[3] + "\"");
            builder.addField("Old Body", String.valueOf(pc.getBodyScore()), true);
            builder.addBlankField(true);
            builder.addField("New Body", messageArgs[1], true);

            builder.addField("Old Max HP", String.valueOf(pc.getMaxHP()), true);
            builder.addBlankField(true);
            builder.addField("New Max HP", messageArgs[2], true);

        } else {
            builder.setTitle("ERROR: Improve Update Or Insert Failure");
            builder.setDescription("Please contact an administrator to get this resolved");
        }
    }

    private boolean updateBodyHp(Connection conn) throws SQLException {
        String sql = "UPDATE NCO_PC set body_score = ?, max_hp = ? Where character_name = ?";
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setString(1, messageArgs[1]);
            stat.setString(2, messageArgs[2]);
            stat.setString(3, messageArgs[0]);
            return stat.executeUpdate() == 1;
        }
    }

    private boolean insertBodyHp(Connection conn, PlayerCharacter pc) throws SQLException {
        String sql  = "INSERT INTO NCO_BODY_HP_UPDATE (character_name, old_body, new_body, old_max_hp, new_max_hp, reason, created_by) VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setString(1, messageArgs[0]);
            stat.setInt(2, pc.getBodyScore());
            stat.setString(3, messageArgs[1]);
            stat.setInt(4, pc.getMaxHP());
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
