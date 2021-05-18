package com.nco.commands;

import com.nco.RedBot;
import com.nco.pojos.PlayerCharacter;
import com.nco.utils.DBUtils;
import com.nco.utils.NumberUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TradeCommand extends AbstractCommand {

    public TradeCommand(String[] messageArgs, User author, MessageChannel channel) {
        super(messageArgs, author, channel);
    }

    @Override
    protected boolean canProcessByUser() {
        return false;
    }

    @Override
    protected boolean canProcessByName() {
        return (messageArgs.length == 4 && NumberUtils.isNumeric(messageArgs[2]) ||
                (messageArgs.length == 5 && NumberUtils.isNumeric(messageArgs[2])
                && NumberUtils.isNumeric(messageArgs[4]))) && DBUtils.doesCharacterExist(messageArgs[3]);
    }

    @Override
    protected void processUpdateAndRespond(Connection conn, PlayerCharacter pc, EmbedBuilder builder) throws SQLException {
        if (messageArgs.length == 5  && NumberUtils.asPositive(messageArgs[4]) > pc.getDownTime()) {
            builder.setTitle("ERROR: Not Enough DT");
            builder.setDescription(messageArgs[0] + " has only " + pc.getDownTime() + " available DT " +
                    "where " + NumberUtils.asPositive(messageArgs[4]) + " DT was requested.");
        } else if (pc.getBank() < NumberUtils.asPositive(messageArgs[2])) {
            builder.setTitle("ERROR: Not Enough Eurobucks");
            builder.setDescription(messageArgs[0] + " has only " + pc.getBank() + "eb available " +
                    "where " + NumberUtils.asPositive(messageArgs[2]) + "eb is being spent.");
        } else if (updateSenderTrade(pc, conn) && updateReceiverTrade(conn) && insertTrade(conn)) {
            builder.setDescription(NumberUtils.asPositive(messageArgs[2]) + "eb sent to " + messageArgs[3]);
            int oldBank = pc.getBank();
            int newBank = oldBank + NumberUtils.asNegative(messageArgs[2]);
            builder.setTitle(messageArgs[0] + "'s Bank Balance Updated");
            builder.setDescription("For \"" + messageArgs[1] + "\"");
            builder.addField("Old Balance", oldBank + "eb", true);
            builder.addBlankField(true);
            builder.addField("New Balance", newBank + "eb", true);
            if (messageArgs.length == 5) {
                int oldDownTime = pc.getDownTime();
                int changeDT = Integer.parseInt(messageArgs[4]);
                int newDownTime = oldDownTime - (changeDT < 0 ? -changeDT : changeDT);

                builder.addField("Old DT", String.valueOf(oldDownTime), true);
                builder.addBlankField(true);
                builder.addField("New DT", String.valueOf(newDownTime), true);
            }
        } else {
            builder.setTitle("ERROR: Trade Update Or Insert Failure");
            builder.setDescription("Please contact an administrator to get this resolved");
        }
    }

    private boolean updateSenderTrade(PlayerCharacter pc, Connection conn) throws SQLException {
        int newBalance = pc.getBank() - NumberUtils.asPositive(messageArgs[2]);
        int newDownTime = pc.getDownTime();
        if (messageArgs.length == 5) {
            int changeDT = Integer.parseInt(messageArgs[4]);
            newDownTime -= (changeDT < 0 ? -changeDT : changeDT);
        }
        String sql = "UPDATE NCO_PC set Bank = ?, DownTime = ? Where CharacterName = ?";

        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setInt(1, newBalance);
            stat.setInt(2, newDownTime);
            stat.setString(3, messageArgs[0]);
            return stat.executeUpdate() == 1;
        }
    }

    private boolean updateReceiverTrade(Connection conn) {
        PlayerCharacter pc = DBUtils.getCharacter(messageArgs[3]);
        if (pc != null) {
            int newBalance = pc.getBank() + NumberUtils.asPositive(messageArgs[2]);
            String sql = "UPDATE NCO_PC set Bank = ? Where CharacterName = ?";
            try (PreparedStatement stat = conn.prepareStatement(sql)) {
                stat.setInt(1, newBalance);
                stat.setString(2, messageArgs[3]);
                return stat.executeUpdate() == 1;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return false;
    }

    private boolean insertTrade(Connection conn) throws SQLException {
        return insertSenderTrade(conn) && insertReceiverTrade(conn);
    }

    private boolean insertSenderTrade(Connection conn) throws SQLException {
        String sql;
        if (messageArgs.length == 5) {
            sql = "INSERT INTO NCO_BANK (CharacterName, Reason, Amount, CreatedBy, DownTime) VALUES (?,?,?,?,?)";
        } else {
            sql = "INSERT INTO NCO_BANK (CharacterName, Reason, Amount, CreatedBy) VALUES (?,?,?,?)";
        }
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setString(1, messageArgs[0]);
            stat.setString(2, "Sender: " + messageArgs[1]);
            stat.setInt(3, NumberUtils.asNegative(messageArgs[2]));
            stat.setString(4, author.getAsTag());
            if (messageArgs.length == 5) {
                stat.setInt(5, NumberUtils.asPositive(messageArgs[4]));
            }
            return stat.executeUpdate() == 1;
        }
    }

    private boolean insertReceiverTrade(Connection conn) throws SQLException {
        String sql = "INSERT INTO NCO_BANK (CharacterName, Reason, Amount, CreatedBy) VALUES (?,?,?,?)";
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setString(1, messageArgs[3]);
            stat.setString(2, "Receiver: " + messageArgs[1]);
            stat.setInt(3, NumberUtils.asPositive(messageArgs[2]));
            stat.setString(4, author.getAsTag());
            return stat.executeUpdate() == 1;
        }
    }

    @Override
    protected String getHelpTitle() {
        return "Incorrect Trade Formatting";
    }

    @Override
    protected String getHelpDescription() {
        return "Please use the commands below to manage trades\n" + RedBot.PREFIX +
                "trade \"Senders PC Name\" \"Reason\" \"Amount\" \"Recipient PC Name\" \"DT(Optional)\"";
    }
}
