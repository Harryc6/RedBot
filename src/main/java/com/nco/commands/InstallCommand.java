package com.nco.commands;

import com.nco.RedBot;
import com.nco.utils.NumberUtils;
import com.nco.utils.RPGDice;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InstallCommand extends AbstractCommand {

    public InstallCommand(String[] messageArgs, User author, MessageChannel channel) {
        super(messageArgs, author, channel);
    }

    @Override
    protected boolean canProcessByUser() {
        return messageArgs.length >= 4;
    }

    @Override
    protected boolean canProcessByName() {
        return messageArgs.length >= 5;
    }

    @Override
    protected void processUpdateAndRespond(Connection conn, ResultSet rs, EmbedBuilder builder) throws SQLException {
        String valueRolled = RPGDice.roll(messageArgs[2]);
        if (validateArgs(valueRolled)) {
            builder.setTitle(getHelpTitle());
            builder.setDescription(getHelpDescription());
            return;
        }
        if (updateInstall(valueRolled, rs, conn) && insertInstall(valueRolled, conn)) {

            builder.setTitle(messageArgs[0] + "'s Installs Updated");
            builder.setDescription("Installing \"" + messageArgs[1] + "\"");

            if (NumberUtils.isNumeric(messageArgs[3])) {
                int oldBank = rs.getInt("Bank");
                int newBank = oldBank;
                int cost = Integer.parseInt(messageArgs[3]);
                newBank -= (0 > cost ? cost * -1 : cost);
                builder.addField("Old Balance", oldBank + "eb", true);
                builder.addBlankField(true);
                builder.addField("New Balance", newBank + "eb", true);
            }

            builder.addField("Old Humanity", rs.getString("Humanity") + "/" +
                    rs.getString("MaxHumanity"), true);
            builder.addField("Roll: " + messageArgs[2],  valueRolled, true);
            builder.addField("New Humanity", (rs.getInt("Humanity") - Integer.parseInt(valueRolled)) + "/" +
                    (rs.getInt("MaxHumanity") - (messageArgs[4].equalsIgnoreCase("cyberware") ? 2 : 4)), true);
        } else {
            builder.setTitle("ERROR: Install Update Or Insert Failure");
            builder.setDescription("Please contact an administrator to get this resolved");
        }


    }

    private boolean validateArgs(String valueRolled) {
        return valueRolled == null && (NumberUtils.isNumeric(messageArgs[3]) || messageArgs[3].equalsIgnoreCase("paid"))
                && (messageArgs[4].equalsIgnoreCase("cyberware") || messageArgs[4].equalsIgnoreCase("borgware"));
    }


    private boolean updateInstall(String valueRolled, ResultSet rs, Connection conn) throws SQLException {
        int newHumanity = rs.getInt("Humanity") - Integer.parseInt(valueRolled);
        int newMaxHumanity = rs.getInt("MaxHumanity") - (messageArgs[4].equalsIgnoreCase("cyberware") ? 2 : 4);
        int newBank = rs.getInt("Bank");
        if (NumberUtils.isNumeric(messageArgs[3])) {
            int cost = Integer.parseInt(messageArgs[3]);
            newBank -= (0 > cost ? cost * -1 : cost);
        }
        String sql = "UPDATE NCO_PC set Humanity = ?, MaxHumanity = ?, Bank = ? Where CharacterName = ?";

        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setInt(1, newHumanity);
            stat.setInt(2, newMaxHumanity);
            stat.setInt(3, newBank);
            stat.setString(4, messageArgs[0]);
            return stat.executeUpdate() == 1;
        }
    }

    private boolean insertInstall(String valueRolled, Connection conn) throws SQLException {
        String sql = "INSERT INTO NCO_INSTALL (CharacterName, Product, Dice, HumanityLoss, Amount, CyberOrBorg, CreatedBy) VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setString(1, messageArgs[0]);
            stat.setString(2, messageArgs[1]);
            stat.setString(3, messageArgs[2]);
            stat.setString(4, valueRolled);
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
