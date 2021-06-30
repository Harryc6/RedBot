package com.nco.commands;

import com.nco.RedBot;
import com.nco.pojos.PlayerCharacter;
import com.nco.utils.NumberUtils;
import com.nco.utils.StringUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BuyArmorCommand extends AbstractCommand {

    public BuyArmorCommand(String[] messageArgs, Object event, boolean isSlash) {
        super(messageArgs, event, isSlash);
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
    protected void processUpdateAndRespond(Connection conn, PlayerCharacter pc, EmbedBuilder builder) throws SQLException {
        if (!isArmorPaid() && doesBankChange() && NumberUtils.asPositive(messageArgs[messageArgs.length - 1]) > pc.getBank()) {
            builder.setTitle("ERROR: Not Enough Eurobucks");
            builder.setDescription(StringUtils.capitalizeWords(messageArgs[0]) + " has only " + pc.getBank() + "eb available " +
                    "where " + NumberUtils.asPositive(messageArgs[messageArgs.length - 1]) + "eb is being spent.");
//        } else if (updateArmor(conn, pc) && insertBuyArmor(conn)) {
        } else if (updateArmor(conn, pc)) {
            builder.setTitle(StringUtils.capitalizeWords(messageArgs[0]) + "'s Armor Updated");
            if (isMultiLocation()) {
                builder.addField("Old Head SP", String.valueOf(pc.getHeadSp()), true);
                builder.addBlankField(true);
                builder.addField("New Head SP", (isHead(2) ? messageArgs[3] : messageArgs[6]), true);
                builder.addField("Old Body SP", String.valueOf(pc.getBodySp()), true);
                builder.addBlankField(true);
                builder.addField("New Body SP", (isBody(2) ? messageArgs[3] : messageArgs[6]), true);
            } else {
                if (isHead(2)) {
                    builder.addField("Old Head SP", String.valueOf(pc.getHeadSp()), true);
                    builder.addBlankField(true);
                    builder.addField("New Head SP", messageArgs[3], true);
                } else {
                    builder.addField("Old Body SP", String.valueOf(pc.getBodySp()), true);
                    builder.addBlankField(true);
                    builder.addField("New Body SP", messageArgs[3], true);
                }
            }
            if (!isArmorPaid() && doesBankChange()) {
                int newBank = pc.getBank();
                int changeBank = Integer.parseInt(messageArgs[messageArgs.length - 1]);
                newBank -= (changeBank < 0 ? -changeBank : changeBank);
                builder.addField("Old Bank", pc.getBank() + "eb", true);
                builder.addBlankField(true);
                builder.addField("New Bank", newBank + "eb", true);
            }
        } else {
            builder.setTitle("ERROR: Improve Update Or Insert Failure");
            builder.setDescription("Please contact an administrator to get this resolved");
        }
    }

    private boolean updateArmor(Connection conn, PlayerCharacter pc) throws SQLException {
        int newBank = pc.getBank();
        if (!isArmorPaid() && doesBankChange()) {
            int changeBank = Integer.parseInt(messageArgs[messageArgs.length - 1]);
            newBank -= (changeBank < 0 ? -changeBank : changeBank);
        }
        String sql = "UPDATE NCO_PC set Bank = ?, ";
        if (isMultiLocation()) {
            sql += "body_sp = ?, head_sp = ? Where character_name = ?";
        } else {
            sql += ((isBody(2)) ? "body_sp = ?" : "head_sp = ?") + " Where character_name = ?";
        }
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setInt(1, newBank);
            if (isMultiLocation()) {
                stat.setInt(2, Integer.parseInt((isBody(2) ? messageArgs[3] : messageArgs[6])));
                stat.setInt(3, Integer.parseInt((isHead(2) ? messageArgs[3] : messageArgs[6])));
                stat.setString(4, messageArgs[0]);
            } else {
                stat.setInt(2, Integer.parseInt(messageArgs[3]));
                stat.setString(3, messageArgs[0]);
            }
            return stat.executeUpdate() == 1;
        }
    }

    private boolean insertBuyArmor(Connection conn) throws SQLException {
        String sql  = "INSERT INTO NCO_BUY_ARMOR (character_name, amount, head_armor_type, head_sp," +
                " body_armor_type, body_sp, created_by) VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setString(1, messageArgs[0]);
            if (!isArmorPaid() && doesBankChange()) {
                int changeBank = Integer.parseInt(messageArgs[messageArgs.length - 1]);
                stat.setInt(2, changeBank < 0 ? -changeBank : changeBank);
            } else {
                stat.setInt(2, Integer.parseInt(messageArgs[messageArgs.length - 1]));
            }
            if (isMultiLocation()) {
                stat.setString(3, (isHead(2) ? messageArgs[1] : messageArgs[4]));
                stat.setInt(4, Integer.parseInt((isHead(2) ? messageArgs[3] : messageArgs[6])));
                stat.setString(5, (isBody(2)) ? messageArgs[1] : messageArgs[4]);
                stat.setInt(6, Integer.parseInt((isBody(2)) ? messageArgs[3] : messageArgs[6]));
            } else {
                stat.setString(3, (isHead(2) ? messageArgs[1] : ""));
                stat.setInt(4, Integer.parseInt((isHead(2) ? messageArgs[3] : "")));
                stat.setString(5, (isBody(2)) ? messageArgs[1] : "");
                stat.setInt(6, Integer.parseInt((isBody(2)) ? messageArgs[3] : ""));
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
                "buyarmor \"PC Name(Optional)\" \"Armor Type\" \"Body\" \"SP Value\" \"Amount\" \nor\n" +
                RedBot.PREFIX + "buyarmor \"PC Name(Optional)\" \"Armor Type\" \"Head\" \"SP Value\" \"Amount\" \nor\n" +
                RedBot.PREFIX + "buyarmor \"PC Name(Optional)\" \"Armor Type\" \"Body\" \"SP Value\" " +
                "\"Armor Type\" \"Head\" \"SP Value\" \"Amount\"";
    }
}
