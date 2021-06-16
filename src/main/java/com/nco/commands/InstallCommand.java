package com.nco.commands;

import com.nco.RedBot;
import com.nco.pojos.PlayerCharacter;
import com.nco.utils.NumberUtils;
import com.nco.utils.RPGDice;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InstallCommand extends AbstractCommand {

    public InstallCommand(String[] messageArgs, GuildMessageReceivedEvent event) {
        super(messageArgs, event);
    }

    @Override
    protected boolean canProcessByUser() {
        return messageArgs.length == 4 && (isPaid() || doesBankChange()) && containsCyberwareType();
    }

    private boolean isPaid() {
        return messageArgs[messageArgs.length - 2].equalsIgnoreCase("paid") ||
                (NumberUtils.isNumeric(messageArgs[messageArgs.length - 2]));
    }

    private boolean doesBankChange() {
        return NumberUtils.isNumeric(messageArgs[messageArgs.length - 2]);
    }

    private boolean containsCyberwareType() {
        return messageArgs[messageArgs.length - 1].equalsIgnoreCase("cyberware")
                || messageArgs[messageArgs.length - 1].equalsIgnoreCase("borgware");
    }

    @Override
    protected boolean canProcessByName() {
        return messageArgs.length == 5 && (isPaid() || doesBankChange()) && containsCyberwareType();
    }

    @Override
    protected void processUpdateAndRespond(Connection conn, PlayerCharacter pc, EmbedBuilder builder) throws SQLException {
        int valueRolled = RPGDice.roll(messageArgs[2]);
        int newHumanity = pc.getCurrentHumanity() - valueRolled;
        int newMaxHumanity = pc.getMaxHumanity() - (messageArgs[4].equalsIgnoreCase("cyberware") ? 2 : 4);
        int newBank = pc.getBank() - (isPaid() ? 0 : NumberUtils.asPositive(messageArgs[3]));
        if (!isPaid() && newBank < 0) {
                builder.setTitle("ERROR: Not Enough Eurobucks");
                builder.setDescription(messageArgs[0] + " has only " + pc.getBank() + "eb available " +
                        "where " + NumberUtils.asPositive(messageArgs[messageArgs.length - 1]) + "eb is being spent.");
        } else if (updateInstall(newHumanity, newMaxHumanity, newBank, conn) && insertInstall(valueRolled, conn)) {
            buildEmbeddedContent(pc, builder, valueRolled, newHumanity, newMaxHumanity, newBank);
        } else {
            builder.setTitle("ERROR: Install Update Or Insert Failure");
            builder.setDescription("Please contact an administrator to get this resolved");
        }
    }

    private boolean updateInstall(int newHumanity, int newMaxHumanity, int newBank, Connection conn) throws SQLException {
        String sql = "UPDATE NCO_PC set humanity = ?, max_humanity = ?, bank = ? Where character_name = ?";
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setInt(1, newHumanity);
            stat.setInt(2, newMaxHumanity);
            stat.setInt(3, newBank);
            stat.setString(4, messageArgs[0]);
            return stat.executeUpdate() == 1;
        }
    }

    private boolean insertInstall(int valueRolled, Connection conn) throws SQLException {
        String sql = "INSERT INTO NCO_INSTALL (character_name, product, dice, humanity_loss, amount, cyber_or_borg, created_by) VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setString(1, messageArgs[0]);
            stat.setString(2, messageArgs[1]);
            stat.setString(3, messageArgs[2]);
            stat.setInt(4, valueRolled);
            stat.setString(5, messageArgs[3]);
            stat.setString(6, messageArgs[4]);
            stat.setString(7, author.getAsTag());
            return stat.executeUpdate() == 1;
        }
    }

    private void buildEmbeddedContent(PlayerCharacter pc, EmbedBuilder builder, int valueRolled, int newHumanity, int newMaxHumanity, int newBank) {
        builder.setTitle(messageArgs[0] + "'s Installs Updated");
        builder.setDescription("Installing \"" + messageArgs[1] + "\"");

        if (doesBankChange()) {
            builder.addField("Old Balance", pc.getBank() + "eb", true);
            builder.addBlankField(true);
            builder.addField("New Balance", newBank + "eb", true);
        }

        builder.addField("Old Humanity", pc.getCurrentHumanity() + "/" + pc.getMaxHumanity(), true);
        builder.addField("Roll: " + messageArgs[2], String.valueOf(valueRolled), true);
        builder.addField("New Humanity", newHumanity + "/" + newMaxHumanity, true);
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
