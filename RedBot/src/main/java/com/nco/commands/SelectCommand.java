package com.nco.commands;

import com.nco.RedBot;
import com.nco.pojos.PlayerCharacter;
import com.nco.utils.StringUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.sql.*;

public class SelectCommand extends AbstractCommand {

    public SelectCommand(String[] messageArgs, Object event, boolean isSlash) {
        super(messageArgs, event, isSlash);
    }

    @Override
    protected String getRoleRequiredForCommand() {
        return "Tech-Support";
    }

    @Override
    protected boolean canProcessByUser() {
        return messageArgs[0].isEmpty();
    }

    @Override
    protected boolean canProcessByName() {
        return messageArgs.length == 1;
    }

    @Override
    protected void processUpdateAndRespond(Connection conn, PlayerCharacter pc, EmbedBuilder builder) throws SQLException {
        builder.setTitle("Select of " + StringUtils.capitalizeWords(messageArgs[0]));

        String sql = "Select * From NCO_PC where character_name = ?";
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setString(1, messageArgs[0]);
            try (ResultSet rs = stat.executeQuery()) {
                ResultSetMetaData rsmd = rs.getMetaData();
                if (rs.next()) {
                    for (int i = 1; i < rsmd.getColumnCount(); i++) {
                        if (builder.getFields().size() >= 25) {
                            channel.sendMessage(builder.build()).queue();
                            builder.clear();
                            builder.setColor(Color.red);
                            builder.setTitle("Select of " + StringUtils.capitalizeWords(messageArgs[0]) + " Continued");
                        }
                        builder.addField(rsmd.getColumnName(i), StringUtils.checkNull(rs.getString(i)), true);
                    }
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    protected String getHelpTitle() {
        return "Incorrect Select Formatting";
    }

    @Override
    protected String getHelpDescription() {
        return "Please use the commands below to select the data directly from the NCO_PC table in the database. \n" + RedBot.PREFIX +
                "select \"PC Name(Optional)\"";
    }
}
