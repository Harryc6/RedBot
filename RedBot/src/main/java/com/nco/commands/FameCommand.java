package com.nco.commands;

import com.nco.RedBot;
import com.nco.pojos.PlayerCharacter;
import com.nco.utils.NCOUtils;
import com.nco.utils.NumberUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FameCommand extends AbstractCommand {

    public FameCommand(String[] messageArgs, Object event, boolean isSlash) {
        super(messageArgs, event, isSlash);
    }

    @Override
    protected boolean canProcessByUser() {
        return messageArgs.length == 2 && NumberUtils.isNumeric(messageArgs[1]);
    }

    @Override
    protected boolean canProcessByName() {
        return messageArgs.length == 3 && NumberUtils.isNumeric(messageArgs[2]);
    }

    @Override
    protected void processUpdateAndRespond(Connection conn, PlayerCharacter pc, EmbedBuilder builder) throws SQLException {
        int newFame = pc.getFame() + NumberUtils.asPositive(messageArgs[2]);
        int newReputation = NCOUtils.getReputationFromFame(newFame);
        if (updateFame(newFame, newReputation, conn) && insertFame(conn)) {
            buildEmbeddedContent(pc, builder, newFame, newReputation);
        } else {
            builder.setTitle("ERROR: Fame Update Or Insert Failure");
            builder.setDescription("Please contact an administrator to get this resolved");
        }
    }

    private boolean updateFame(int newFame, int newReputation, Connection conn) throws SQLException {
        String sql = "UPDATE NCO_PC set fame = ?, reputation = ? Where character_name = ?";
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setInt(1, newFame);
            stat.setInt(2, newReputation);
            stat.setString(3, messageArgs[0]);
            return stat.executeUpdate() == 1;
        }
    }

    private boolean insertFame(Connection conn) throws SQLException {
        String sql = "INSERT INTO NCO_FAME (character_name, reason, fame, created_by) VALUES (?,?,?,?)";
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setString(1, messageArgs[0]);
            stat.setString(2, messageArgs[1]);
            stat.setInt(3, NumberUtils.asPositive(messageArgs[2]));
            stat.setString(4, author.getAsTag());
            return stat.executeUpdate() == 1;
        }
    }

    private void buildEmbeddedContent(PlayerCharacter pc, EmbedBuilder builder, int newFame, int newReputation) {
        builder.setTitle(messageArgs[0] + "'s Fame Updated");
        builder.setDescription("For \"" + messageArgs[1] + "\"");
        builder.addField("Old Fame", String.valueOf(pc.getFame()), true);
        builder.addBlankField(true);
        builder.addField("New Fame", String.valueOf(newFame), true);
        if (pc.getReputation() != newReputation) {
            builder.addField("Old Reputation", String.valueOf(pc.getReputation()), true);
            builder.addField("New Reputation", String.valueOf(newReputation), true);
        }
    }

    @Override
    protected String getHelpTitle() {
        return "Incorrect Fame Formatting";
    }

    @Override
    protected String getHelpDescription() {
        return "Please use the commands below to add fame to a character \n" + RedBot.PREFIX +
                "fame \"PC Name(Optional)\" \"Reason\" \"Amount\"";
    }
}
