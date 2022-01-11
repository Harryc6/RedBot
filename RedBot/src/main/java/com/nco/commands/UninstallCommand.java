package com.nco.commands;

import com.nco.RedBot;
import com.nco.pojos.PlayerCharacter;
import com.nco.utils.StringUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UninstallCommand extends AbstractCommand {

    public UninstallCommand(String[] messageArgs, Object event, boolean isSlash) {
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
        int newMaxHum = pc.getMaxHumanity() + (messageArgs[1].equalsIgnoreCase("cyberware") ? 2 : 4);
        if (newMaxHum > pc.getMaxEmpathy() * 10) {
            newMaxHum = pc.getMaxEmpathy() * 10;
        }
//        if (updateMaxHum(newMaxHum, conn) && insertMaxHum(conn, oldMaxHum, newMaxHum)) {
        if (updateMaxHum(newMaxHum, conn)) {
            builder.setTitle(StringUtils.capitalizeWords(messageArgs[0]) + " Uninstalled " + StringUtils.capitalizeWords(messageArgs[1]));
            builder.addField("Old Max Humanity", String.valueOf(pc.getMaxHumanity()), true);
            builder.addBlankField(true);
            builder.addField("New Max Humanity", String.valueOf(newMaxHum), true);
        } else {
            builder.setTitle("ERROR: Uninstall Update Failure");
            builder.setDescription("Please contact an administrator to get this resolved");
        }
    }

    private boolean updateMaxHum(int newMaxHumTotal, Connection conn) throws SQLException {
        String sql = "UPDATE nco_pc set max_humanity = ? Where character_name = ?";
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setInt(1, newMaxHumTotal);
            stat.setString(2, messageArgs[0]);
            return stat.executeUpdate() == 1;
        }
    }

    @Override
    protected String getHelpTitle() {
        return "Incorrect Uninstall Formatting";
    }

    @Override
    protected String getHelpDescription() {
        return "Please use the commands below to Uninstall cyberware from a characters \n" + RedBot.PREFIX +
                "uninstall \"PC Name(Optional)\" “cyberware” or “borgware”\n";
    }
}
