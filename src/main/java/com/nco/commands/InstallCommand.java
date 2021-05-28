package com.nco.commands;

import com.nco.RedBot;
import com.nco.pojos.PlayerCharacter;
import com.nco.utils.NumberUtils;
import com.nco.utils.RPGDice;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InstallCommand extends AbstractCommand {

    public InstallCommand(String[] messageArgs, User author, MessageChannel channel, Member member) {
        super(messageArgs, author, channel, member);
    }

    @Override
    protected boolean canProcessByUser() {
        return messageArgs.length == 4 && (isPaid() || doesBankChange());
    }

    private boolean isPaid() {
        return messageArgs[messageArgs.length - 2].equalsIgnoreCase("paid") ||
                (NumberUtils.isNumeric(messageArgs[messageArgs.length - 2]) &&
                        NumberUtils.asPositive(messageArgs[messageArgs.length - 2]) == 0);
    }

    private boolean doesBankChange() {
        return NumberUtils.isNumeric(messageArgs[messageArgs.length - 2]) &&
                NumberUtils.asPositive(messageArgs[messageArgs.length - 2]) != 0;
    }

    @Override
    protected boolean canProcessByName() {
        return messageArgs.length == 5 && (isPaid() || doesBankChange());
    }

    @Override
    protected void processUpdateAndRespond(Connection conn, PlayerCharacter pc, EmbedBuilder builder) throws SQLException {
        String valueRolled = RPGDice.roll(messageArgs[2]);
        if (validateArgs(valueRolled)) {
            builder.setTitle(getHelpTitle());
            builder.setDescription(getHelpDescription());
        } else if (!isPaid() && doesBankChange() && NumberUtils.asPositive(messageArgs[3]) > pc.getBank()) {
                builder.setTitle("ERROR: Not Enough Eurobucks");
                builder.setDescription(messageArgs[0] + " has only " + pc.getBank() + "eb available " +
                        "where " + NumberUtils.asPositive(messageArgs[messageArgs.length - 1]) + "eb is being spent.");
        } else if (updateInstall(valueRolled, pc, conn) && insertInstall(valueRolled, conn)) {

            builder.setTitle(messageArgs[0] + "'s Installs Updated");
            builder.setDescription("Installing \"" + messageArgs[1] + "\"");

            if (doesBankChange()) {
                int oldBank = pc.getBank();
                int newBank = oldBank;
                int cost = Integer.parseInt(messageArgs[3]);
                newBank -= (0 > cost ? cost * -1 : cost);
                builder.addField("Old Balance", oldBank + "eb", true);
                builder.addBlankField(true);
                builder.addField("New Balance", newBank + "eb", true);
            }

            builder.addField("Old Humanity", pc.getHumanity() + "/" +
                    pc.getMaxHumanity(), true);
            builder.addField("Roll: " + messageArgs[2],  valueRolled, true);
            builder.addField("New Humanity", (pc.getHumanity() - Integer.parseInt(valueRolled)) + "/" +
                    (pc.getMaxHumanity() - (messageArgs[4].equalsIgnoreCase("cyberware") ? 2 : 4)), true);
        } else {
            builder.setTitle("ERROR: Install Update Or Insert Failure");
            builder.setDescription("Please contact an administrator to get this resolved");
        }


    }

    private boolean validateArgs(String valueRolled) {
        return valueRolled == null || (NumberUtils.isNumeric(messageArgs[3]) || messageArgs[3].equalsIgnoreCase("paid"))
                && (messageArgs[4].equalsIgnoreCase("cyberware") || messageArgs[4].equalsIgnoreCase("borgware"));
    }


    private boolean updateInstall(String valueRolled, PlayerCharacter pc, Connection conn) throws SQLException {
        int newHumanity = pc.getHumanity() - Integer.parseInt(valueRolled);
        int newMaxHumanity = pc.getMaxHumanity() - (messageArgs[4].equalsIgnoreCase("cyberware") ? 2 : 4);
        int newBank = pc.getBank();
        if (NumberUtils.isNumeric(messageArgs[3])) {
            int cost = Integer.parseInt(messageArgs[3]);
            newBank -= (0 > cost ? cost * -1 : cost);
        }
        String sql = "UPDATE NCO_PC set humanity = ?, max_humanity = ?, bank = ? Where character_name = ?";

        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setInt(1, newHumanity);
            stat.setInt(2, newMaxHumanity);
            stat.setInt(3, newBank);
            stat.setString(4, messageArgs[0]);
            return stat.executeUpdate() == 1;
        }
    }

    private boolean insertInstall(String valueRolled, Connection conn) throws SQLException {
        String sql = "INSERT INTO NCO_INSTALL (character_name, product, dice, humanity_loss, amount, cyber_or_borg, created_by) VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setString(1, messageArgs[0]);
            stat.setString(2, messageArgs[1]);
            stat.setString(3, messageArgs[2]);
            stat.setInt(4, Integer.parseInt(valueRolled));
            stat.setString(5, messageArgs[3]);
            stat.setString(6, messageArgs[4]);
            stat.setString(7, author.getAsTag());
            return stat.executeUpdate() == 1;
        }
    }

    @Override
    protected String getHelpTitle() {
        return "Incorrect Install Formatting";
    }

    @Override
    protected String getHelpDescription() {
        return "Please use the commands below to install a characters cyberware\n" + RedBot.PREFIX +
                "install \"PC Name(Optional)\" \"Product\" \"Dice\" \"Amount\" or “Paid” “cyberware” or “borgware”\n";
    }



}
