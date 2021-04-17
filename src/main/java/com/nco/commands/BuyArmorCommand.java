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

public class BuyArmorCommand extends AbstractCommand {

    public BuyArmorCommand(String[] messageArgs, User author, MessageChannel channel) {
        super(messageArgs, author, channel);
    }

    @Override
    protected boolean canProcessByUser() {
        return ((messageArgs.length == 4 && isArmorLocation(1) && NumberUtils.isNumeric(messageArgs[0])) ||
                (messageArgs.length == 7 && NumberUtils.isNumeric(messageArgs[2]) && NumberUtils.isNumeric(messageArgs[5]) &&
                ((isBody(1) && isHead(4)) || isHead(1) && isBody(4))))
                && (doesBankChange() || isArmorPaid());
    }

    private boolean isArmorLocation(int argsNum) {
        return isBody(argsNum) || isHead(argsNum);
    }

    private boolean isBody(int argsNum) {
        return messageArgs[argsNum].equalsIgnoreCase("body");
    }


    private boolean isHead(int argsNum) {
        return messageArgs[argsNum].equalsIgnoreCase("head");
    }

    @Override
    protected boolean canProcessByName() {
        return (isSingleLocation() || isMultiLocation()) && (doesBankChange() || isArmorPaid());
    }

    private boolean isArmorPaid() {
        return messageArgs[messageArgs.length - 1].equalsIgnoreCase("paid") ||
                (NumberUtils.isNumeric(messageArgs[messageArgs.length - 1]) &&
                        NumberUtils.asPositive(messageArgs[messageArgs.length - 1]) == 0);
    }

    private boolean doesBankChange() {
        return NumberUtils.isNumeric(messageArgs[messageArgs.length - 1]) &&
                NumberUtils.asPositive(messageArgs[messageArgs.length - 1]) != 0;
    }

    private boolean isSingleLocation() {
        return messageArgs.length == 5 && isArmorLocation(2) && NumberUtils.isNumeric(messageArgs[3]);
    }

    private boolean isMultiLocation() {
        return messageArgs.length == 8 &&
                ((isBody(2) && isHead(5)) || isHead(2) && isBody(5)) &&
                NumberUtils.isNumeric(messageArgs[3]) && NumberUtils.isNumeric(messageArgs[6]);
    }

    @Override
    protected void processUpdateAndRespond(Connection conn, ResultSet rs, EmbedBuilder builder) throws SQLException {
        if (!isArmorPaid() && doesBankChange() && NumberUtils.asPositive(messageArgs[messageArgs.length - 1]) > rs.getInt("Bank")) {
            builder.setTitle("ERROR: Not Enough Eurobucks");
            builder.setDescription(messageArgs[0] + " has only " + rs.getString("Bank") + "eb available " +
                    "where " + NumberUtils.asPositive(messageArgs[messageArgs.length - 1]) + "eb is being spent.");
        } else if (updateArmor(conn, rs) && insertBuyArmor(conn)) {
            builder.setTitle(messageArgs[0] + "'s Armor Updated");
            if (isMultiLocation()) {
                builder.addField("Old Head SP", rs.getString("HeadSP"), true);
                builder.addBlankField(true);
                builder.addField("New Head SP", (isHead(2) ? messageArgs[3] : messageArgs[6]), true);
                builder.addField("Old Body SP", rs.getString("BodySP"), true);
                builder.addBlankField(true);
                builder.addField("New Body SP", (isBody(2) ? messageArgs[3] : messageArgs[6]), true);
            } else {
                if (isHead(2)) {
                    builder.addField("Old Head SP", rs.getString("HeadSP"), true);
                    builder.addBlankField(true);
                    builder.addField("New Head SP", messageArgs[3], true);
                } else {
                    builder.addField("Old Body SP", rs.getString("BodySP"), true);
                    builder.addBlankField(true);
                    builder.addField("New Body SP", messageArgs[3], true);
                }
            }
            if (!isArmorPaid() && doesBankChange()) {
                int newBank = rs.getInt("Bank");
                int changeBank = Integer.parseInt(messageArgs[messageArgs.length - 1]);
                newBank -= (changeBank < 0 ? -changeBank : changeBank);
                builder.addField("Old Bank", rs.getInt("Bank") + "eb", true);
                builder.addBlankField(true);
                builder.addField("New Bank", newBank + "eb", true);
            }
        } else {
            builder.setTitle("ERROR: Improve Update Or Insert Failure");
            builder.setDescription("Please contact an administrator to get this resolved");
        }
    }

    private boolean updateArmor(Connection conn, ResultSet rs) throws SQLException {
        int newBank = rs.getInt("Bank");
        if (!isArmorPaid() && doesBankChange()) {
            int changeBank = Integer.parseInt(messageArgs[messageArgs.length - 1]);
            newBank -= (changeBank < 0 ? -changeBank : changeBank);
        }
        String sql = "UPDATE NCO_PC set Bank = ?, ";
        if (isMultiLocation()) {
            sql += "BodySP = ?, HeadSP = ? Where CharacterName = ?";
        } else {
            sql += ((isBody(2)) ? "BodySP = ?" : "HeadSP = ?") + " Where CharacterName = ?";
        }
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setInt(1, newBank);
            if (isMultiLocation()) {
                stat.setString(2, (isBody(2) ? messageArgs[3] : messageArgs[6]));
                stat.setString(3, (isHead(2) ? messageArgs[3] : messageArgs[6]));
                stat.setString(4, messageArgs[0]);
            } else {
                stat.setString(2, messageArgs[3]);
                stat.setString(3, messageArgs[0]);
            }
            return stat.executeUpdate() == 1;
        }
    }

    private boolean insertBuyArmor(Connection conn) throws SQLException {
        String sql  = "INSERT INTO NCO_BUY_ARMOR (CharacterName, Amount, HeadArmorType, HeadSP," +
                " BodyArmorType, BodySP, CreatedBy) VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setString(1, messageArgs[0]);
            if (!isArmorPaid() && doesBankChange()) {
                int changeBank = Integer.parseInt(messageArgs[messageArgs.length - 1]);
                stat.setInt(2, changeBank < 0 ? -changeBank : changeBank);
            } else {
                stat.setString(2, messageArgs[messageArgs.length - 1]);
            }
            if (isMultiLocation()) {
                stat.setString(3, (isHead(2) ? messageArgs[1] : messageArgs[4]));
                stat.setString(4, (isHead(2) ? messageArgs[3] : messageArgs[6]));
                stat.setString(5, (isBody(2)) ? messageArgs[1] : messageArgs[4]);
                stat.setString(6, (isBody(2)) ? messageArgs[3] : messageArgs[6]);
            } else {
                stat.setString(3, (isHead(2) ? messageArgs[1] : ""));
                stat.setString(4, (isHead(2) ? messageArgs[3] : ""));
                stat.setString(5, (isBody(2)) ? messageArgs[1] : "");
                stat.setString(6, (isBody(2)) ? messageArgs[3] : "");
            }
            stat.setString(7, author.getAsTag());
            return stat.executeUpdate() == 1;
        }
    }

    @Override
    protected String getHelpTitle() {
        return "Incorrect Buy Armor Formatting";
    }

    @Override
    protected String getHelpDescription() {
        return "Please use the commands below to manage a characters armor\n" + RedBot.PREFIX +
                "updatebodyhp \"PC Name(Optional)\" \"Armor Type\" \"Body\" \"SP Value\" \"Amount\" \nor\n" +
                RedBot.PREFIX + "updatebodyhp \"PC Name(Optional)\" \"Armor Type\" \"Head\" \"SP Value\" \"Amount\" \nor\n" +
                RedBot.PREFIX + "updatebodyhp \"PC Name(Optional)\" \"Armor Type\" \"Body\" \"SP Value\" " +
                "\"Armor Type\" \"Head\" \"SP Value\" \"Amount\"";
    }
}
