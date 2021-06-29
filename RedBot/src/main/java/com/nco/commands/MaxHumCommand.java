package com.nco.commands;

import com.nco.RedBot;
import com.nco.pojos.PlayerCharacter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MaxHumCommand extends AbstractCommand {

    public MaxHumCommand(String[] messageArgs, Object event, boolean isSlash) {
        super(messageArgs, event, isSlash);
    }

    @Override
    protected boolean canProcessByUser() {
        return messageArgs.length == 1 && containsCyberBorg();
    }

    @Override
    protected boolean canProcessByName() {
        return messageArgs.length == 2 && containsCyberBorg();
    }

    private boolean containsCyberBorg() {
        return messageArgs[messageArgs.length - 1].equalsIgnoreCase("cyberware") ||
                messageArgs[messageArgs.length - 1].equalsIgnoreCase("borgware");
    }

    @Override
    protected void processUpdateAndRespond(Connection conn, PlayerCharacter pc, EmbedBuilder builder) throws SQLException {
        int oldMaxHum = pc.getMaxHumanity();
        int newMaxHum = oldMaxHum + (messageArgs[1].equalsIgnoreCase("cyberware") ? 2 : 4);
//        if (updateMaxHum(newMaxHum, conn) && insertMaxHum(conn, oldMaxHum, newMaxHum)) {
        if (updateMaxHum(newMaxHum, conn)) {
            builder.setTitle(messageArgs[0] + "'s MaxHumanity Updated");
            builder.setDescription("Removed \"" + messageArgs[1] + "\"");
            builder.addField("Old MaxHumanity", String.valueOf(oldMaxHum), true);
            builder.addBlankField(true);
            builder.addField("New MaxHumanity", String.valueOf(newMaxHum), true);
        } else {
            builder.setTitle("ERROR: MaxHum Update Or Insert Failure");
            builder.setDescription("Please contact an administrator to get this resolved");
        }
    }

    private boolean updateMaxHum(int newMaxHumTotal, Connection conn) throws SQLException {
        String sql = "UPDATE nco_pc_stats set max_humanity = ? Where character_name = ?";
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setInt(1, newMaxHumTotal);
            stat.setString(2, messageArgs[0]);
            return stat.executeUpdate() == 1;
        }
    }

    private boolean insertMaxHum(Connection conn, int oldMaxHum, int newMaxHum) throws SQLException {
        String sql = "insert into nco_max_humanity (character_name, cyber_or_borg, old_max_humanity, new_max_humanity, created_by) " +
                "values (?, ?, ?, ?, ?)";
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setString(1, messageArgs[0]);
            stat.setString(2, messageArgs[1]);
            stat.setInt(3, oldMaxHum);
            stat.setInt(4, newMaxHum);
            stat.setString(5, author.getAsTag());
            return stat.executeUpdate() == 1;
        }
    }

    @Override
    protected String getHelpTitle() {
        return "Incorrect Update Humanity Formatting";
    }

    @Override
    protected String getHelpDescription() {
        return "Please use the commands below to add MaxHumanity onto a characters \n" + RedBot.PREFIX +
                "maxhum \"PC Name(Optional)\" “cyberware” or “borgware”\n";
    }
}
