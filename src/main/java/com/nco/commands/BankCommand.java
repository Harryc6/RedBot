package com.nco.commands;

import com.nco.RedBot;
import com.nco.pojos.PlayerCharacter;
import com.nco.utils.NumberUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BankCommand extends AbstractCommand {

    public BankCommand(String[] messageArgs, User author, MessageChannel channel) {
        super(messageArgs, author, channel);
    }

    @Override
    protected boolean canProcessByUser() {
        return (messageArgs.length == 2 || (messageArgs.length == 3 && NumberUtils.isNumeric(messageArgs[2])))
                && NumberUtils.isNumeric(messageArgs[1]);
    }

    @Override
    protected boolean canProcessByName() {
        return (messageArgs.length == 3 || (messageArgs.length == 4 && NumberUtils.isNumeric(messageArgs[3])))
                && NumberUtils.isNumeric(messageArgs[2]);
    }

    @Override
    protected void processUpdateAndRespond(Connection conn, PlayerCharacter pc, EmbedBuilder builder) throws SQLException {
        if (messageArgs.length == 4  && NumberUtils.asPositive(messageArgs[3]) > pc.getDowntime()) {
            builder.setTitle("ERROR: Not Enough DT");
            builder.setDescription(messageArgs[0] + " has only " + pc.getDowntime() + " available DT " +
                    "where " + NumberUtils.asPositive(messageArgs[3]) + " DT was requested.");
        } else if (Integer.parseInt(messageArgs[2]) < 0 && NumberUtils.asPositive(messageArgs[2]) > pc.getBank()) {
            builder.setTitle("ERROR: Not Enough Eurobucks");
            builder.setDescription(messageArgs[0] + " has only " + pc.getBank() + "eb available " +
                    "where " + NumberUtils.asPositive(messageArgs[2]) + "eb is being spent.");

        } else if (updateBank(pc, conn) && insertBank(conn)) {
            int oldBank = pc.getBank();
            int newBank = oldBank + Integer.parseInt(messageArgs[2]);
            builder.setTitle(messageArgs[0] + "'s Bank Balance Updated");
            builder.setDescription("For \"" + messageArgs[1] + "\"");
            builder.addField("Old Balance", oldBank + "eb", true);
            builder.addBlankField(true);
            builder.addField("New Balance", newBank + "eb", true);
            if (messageArgs.length == 4) {
                int oldDownTime = pc.getDowntime();
                int changeDT = Integer.parseInt(messageArgs[3]);
                int newDownTime = oldDownTime - (changeDT < 0 ? -changeDT : changeDT);

                builder.addField("Old DT", String.valueOf(oldDownTime), true);
                builder.addBlankField(true);
                builder.addField("New DT", String.valueOf(newDownTime), true);
            }
        } else {
            builder.setTitle("ERROR: Bank Update Or Insert Failure");
            builder.setDescription("Please contact an administrator to get this resolved");
        }
    }


    private boolean updateBank(PlayerCharacter pc, Connection conn) throws SQLException {
        int newBalance = pc.getBank() + Integer.parseInt(messageArgs[2]);
        int newDownTime = pc.getDowntime();
        if (messageArgs.length == 4) {
            int changeDT = Integer.parseInt(messageArgs[3]);
            newDownTime -= (changeDT < 0 ? -changeDT : changeDT);
        }
        String sql = "UPDATE NCO_PC set Bank = ?, DownTime = ? Where character_name = ?";

        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setInt(1, newBalance);
            stat.setInt(2, newDownTime);
            stat.setString(3, messageArgs[0]);
            return stat.executeUpdate() == 1;
        }
    }

    private boolean insertBank(Connection conn) throws SQLException {
        String sql;
        if (messageArgs.length == 4) {
            sql = "INSERT INTO NCO_BANK (character_name, reason, amount, created_by, downtime) VALUES (?,?,?,?,?)";
        } else {
            sql = "INSERT INTO NCO_BANK (character_name, reason, amount, created_by) VALUES (?,?,?,?)";
        }
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setString(1, messageArgs[0]);
            stat.setString(2, messageArgs[1]);
            stat.setString(3, messageArgs[2]);
            stat.setString(4, author.getAsTag());
            if (messageArgs.length == 4) {
                int changeDT = Integer.parseInt(messageArgs[3]);
                stat.setInt(5, (changeDT < 0 ? -changeDT : changeDT));
            }
            return stat.executeUpdate() == 1;
        }
    }

    @Override
    protected String getHelpTitle() {
        return "Incorrect Bank Formatting";
    }

    @Override
    protected String getHelpDescription() {
        return "Please use the commands below to manage a characters bank\n" + RedBot.PREFIX +
                "bank \"PC Name(Optional)\" \"Reason\" \"Amount\" \"DT(Optional)\"";
    }

}
