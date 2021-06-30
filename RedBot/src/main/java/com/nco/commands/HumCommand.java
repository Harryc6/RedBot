package com.nco.commands;

import com.nco.RedBot;
import com.nco.pojos.PlayerCharacter;
import com.nco.utils.RPGDice;
import com.nco.utils.StringUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class HumCommand extends AbstractCommand {

    public HumCommand(String[] messageArgs, Object event, boolean isSlash) {
        super(messageArgs, event, isSlash);
    }

    @Override
    protected boolean canProcessByUser() {
        return messageArgs.length == 1 && containsTherapyType();
    }

    @Override
    protected boolean canProcessByName() {
        return messageArgs.length == 2 && containsTherapyType();
    }

    private boolean containsTherapyType() {
        return messageArgs[messageArgs.length - 1].equalsIgnoreCase("Pro Standard") ||
                messageArgs[messageArgs.length - 1].equalsIgnoreCase("Pro Extreme");
    }

    @Override
    protected void processUpdateAndRespond(Connection conn, PlayerCharacter pc, EmbedBuilder builder) throws SQLException {
        if (pc.getBank() <= getTherapyCost()) {
            builder.setTitle("ERROR: Not Enough Eurobucks, you need " + getTherapyCost());
            builder.addField("You have:", String.valueOf(pc.getBank()),true);
        } else if (pc.getDowntime() <= 6) {
            builder.setTitle("ERROR: Not Enough DT, you need 7 DT for Therapy.");
            builder.addField("You have:", String.valueOf(pc.getDowntime()),true);
        } else {
            String humSelect = messageArgs[1].equalsIgnoreCase("Pro Standard") ? "2d6" : "4d6";
            int humRoll = RPGDice.roll(humSelect);
            int newHum = pc.getCurrentHumanity() + humRoll;
            if (newHum > pc.getMaxHumanity()) {
                newHum = pc.getMaxHumanity();
            }
            int newBank = pc.getBank() - getTherapyCost();
            int newDT = pc.getDowntime() - 7;

//            if (updateHum(conn, newHum, newBank, newDT) && insertHum(conn, pc, newHum, newBank, newDT)) {
            if (updateHum(conn, newHum, newBank, newDT, pc)) {
                buildEmbed(builder, pc, humSelect, humRoll, newHum, newBank, newDT);
            } else {
                builder.setTitle("ERROR: Install Update Or Insert Failure");
                builder.setDescription("Please contact an administrator to get this resolved");
            }
        }
    }

    private int getTherapyCost() {
        return messageArgs[1].equalsIgnoreCase("Pro Standard") ? 500 : 1000;
    }

    private boolean updateHum(Connection conn, int newHum, int newBank, int newDT, PlayerCharacter pc) throws SQLException {
        return updatePC(conn, newBank, newDT, pc) && updateStats(conn, newHum);
    }

    private boolean updatePC(Connection conn, int newBank, int newDT, PlayerCharacter pc) throws SQLException {
        String sql = "UPDATE NCO_PC set bank = ?, downtime = ? Where character_name = ?";
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setInt(1, newBank);
            stat.setInt(2, (newDT * 12) + pc.getDowntimeRemainder());
            stat.setString(3, messageArgs[0]);
            return stat.executeUpdate() == 1;
        }
    }

    private boolean updateStats(Connection conn, int newHum) throws SQLException {
        String sql = "UPDATE nco_pc_stats set current_humanity = ? Where character_name = ?";
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setInt(1, newHum);
            stat.setString(2, messageArgs[0]);
            return stat.executeUpdate() == 1;
        }
    }

    private boolean insertHum(Connection conn, PlayerCharacter pc, int newHum, int newBank, int newDT) throws SQLException {
        String sql = "insert into nco_humanity (character_name, therapy_type, old_humanity, new_humanity, old_bank, new_bank, old_downtime, new_downtime, created_by) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setString(1, messageArgs[0]);
            stat.setString(2, messageArgs[1]);
            stat.setInt(3, pc.getCurrentHumanity());
            stat.setInt(4, newHum);
            stat.setInt(5, pc.getBank());
            stat.setInt(6, newBank);
            stat.setInt(7, pc.getDowntime());
            stat.setInt(8, newDT);
            stat.setString(9, author.getAsTag());
            return stat.executeUpdate() == 1;
        }
    }

    private void buildEmbed(EmbedBuilder builder, PlayerCharacter pc, String humSelect, int humRoll, int newHum,
                            int newBank, int newDT) {
        builder.setTitle(StringUtils.capitalizeWords(messageArgs[0]) + "'s Humanity Restored");
        builder.setDescription("Rolled a " + humSelect + " for " + humRoll + "");
        builder.addField("Old Humanity", String.valueOf(pc.getCurrentHumanity()), true);
        builder.addBlankField(true);
        builder.addField("New Humanity", String.valueOf(newHum), true);
        builder.addField("Old Bank", String.valueOf(pc.getBank()), true);
        builder.addBlankField(true);
        builder.addField("New Bank", String.valueOf(newBank), true);
        builder.addField("Old Downtime", String.valueOf(pc.getDowntime()), true);
        builder.addBlankField(true);
        builder.addField("New Downtime", String.valueOf(newDT), true);
    }

    @Override
    protected String getHelpTitle() {
        return "Incorrect Update Humanity Formatting";
    }

    @Override
    protected String getHelpDescription() {
        return "Please use the commands below to add Humanity onto a characters \n" + RedBot.PREFIX +
                "hum \"PC Name(Optional)\" “Pro Standard” or “Pro Extreme”\n";
    }
}


