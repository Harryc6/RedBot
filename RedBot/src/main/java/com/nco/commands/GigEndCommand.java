package com.nco.commands;

import com.nco.RedBot;
import com.nco.pojos.PlayerCharacter;
import com.nco.utils.NumberUtils;
import net.dv8tion.jda.api.EmbedBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GigEndCommand extends AbstractCommand {

    public GigEndCommand(String[] messageArgs, Object event, boolean isSlash) {
        super(messageArgs, event, isSlash);
    }

    @Override
    protected boolean findPlayerCharacter() {
        return false;
    }

    protected String[] getRoleRequiredForCommand() {
        return new String[]{"Referees", "Junior Referees"};
    }


    @Override
    protected boolean canProcessWithoutPC() {
        return messageArgs.length == 3 && NumberUtils.isNumeric(messageArgs[0]) &&
                NumberUtils.isNumeric(messageArgs[1]) && NumberUtils.isNumeric(messageArgs[2]);
    }

    @Override
    protected void processUpdateAndRespond(Connection conn, PlayerCharacter pc, EmbedBuilder builder) throws SQLException {
        if (validGigID(conn)) {
            PlayerCharacter ref = new PlayerCharacter(conn, getRefName(conn), false);
            if (ref.getCharacterName() != null) {
                if (updateGig(conn) && updateRef(conn, ref)) {
                    buildEmbed(builder, ref);
                } else {
                    builder.setTitle("ERROR: GigEnd Update Failure");
                    builder.setDescription("Please contact an administrator to get this resolved");
                }
            } else {
                builder.setTitle("No Character Found");
                builder.setDescription("No active character was found tied to the ref " + ref.getDiscordName());
            }
        } else {
            builder.setTitle("No Active Gig Found");
            builder.setDescription("No active gig was found with the ID of " + messageArgs[0]);
        }
    }

    private boolean validGigID(Connection conn) throws SQLException {
        String sql = "select * from nco_gig where id = ? and completed_yn = 'n'";
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setInt(1, NumberUtils.ParseNumber(messageArgs[0]));
            try (ResultSet rs = stat.executeQuery()) {
                return rs.next();
            }
        }
    }

    private boolean updateGig(Connection conn) throws SQLException {
        String sql = "update nco_gig set completed_yn = 'y', completed_on = current_timestamp where id = ?";
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setInt(1, NumberUtils.ParseNumber(messageArgs[0]));
            return stat.executeUpdate() == 1;
        }
    }

    private boolean updateRef(Connection conn, PlayerCharacter ref) throws SQLException {
        String sql = "UPDATE NCO_PC set bank = ?, improvement_points = ? Where character_name = ?";
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setInt(1, ref.getBank() + NumberUtils.asPositive(messageArgs[1]));
            stat.setInt(2, ref.getImprovementPoints() + NumberUtils.asPositive(messageArgs[2]));
            stat.setString(3, ref.getCharacterName());
            return stat.executeUpdate() == 1;
        }
    }

    private String getRefName(Connection conn) throws SQLException {
        String sql = "select referee from nco_gig where id = ?";
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setInt(1, NumberUtils.ParseNumber(messageArgs[0]));
            try (ResultSet rs = stat.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("referee");
                } else {
                    return "";
                }
            }
        }
    }

    private void buildEmbed(EmbedBuilder builder, PlayerCharacter ref) {
        builder.setTitle("Gig #" + messageArgs[0] + " Closed");
        builder.setDescription(ref.getCharacterDisplayName() + " updated.");
        builder.addField("Old Bank", String.valueOf(ref.getBank()), true);
        builder.addBlankField(true);
        builder.addField("New Bank", String.valueOf(ref.getBank() + NumberUtils.asPositive(messageArgs[1])), true);
        builder.addField("Old IP", String.valueOf(ref.getImprovementPoints()), true);
        builder.addBlankField(true);
        builder.addField("New IP", String.valueOf(ref.getImprovementPoints() + NumberUtils.asPositive(messageArgs[2])), true);
    }

    @Override
    protected String getHelpTitle() {
        return "Incorrect Gig End Formatting";
    }

    @Override
    protected String getHelpDescription() {
        return "Please use the commands below to end a gig in the system \n" + RedBot.PREFIX +
                "gigend \"Gig ID\" \"Eddies\" \"IP\"";
    }

}
