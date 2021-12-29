package com.nco.commands;

import com.nco.RedBot;
import com.nco.pojos.PlayerCharacter;
import net.dv8tion.jda.api.EmbedBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class GigCreateCommand extends AbstractCommand {

    public GigCreateCommand(String[] messageArgs, Object event, boolean isSlash) {
        super(messageArgs, event, isSlash);
    }

    @Override
    protected boolean findPlayerCharacter() {
        return false;
    }

    @Override
    protected String[] getRoleRequiredForCommand() {
        return new String[]{"Referees", "Junior Referees"};
    }

    @Override
    protected boolean canProcessWithoutPC() {
        return (messageArgs.length == 1 && !messageArgs[0].isEmpty()) || (messageArgs.length == 2 && guild.getMemberByTag(messageArgs[0]) != null);
    }

    @Override
    protected void processUpdateAndRespond(Connection conn, PlayerCharacter pc, EmbedBuilder builder) throws SQLException {
        if (createNewGig(conn)) {
            String gigID = getGigId(conn);
            if (gigID != null) {
                builder.setTitle("Gig Created");
                builder.addField("Gig ID", gigID, true);
                builder.addField("Gig Name", getGigName(), true);
                builder.addField("Referee", getRefereeAsMention(), true);
            } else {
                builder.setTitle("ERROR: Create Gig Select Failure");
                builder.setDescription("Please contact an administrator to get this resolved");
            }
        } else {
            builder.setTitle("ERROR: Create Gig Insert Failure");
            builder.setDescription("Please contact an administrator to get this resolved");
        }
    }

    private boolean createNewGig(Connection conn) throws SQLException {
        String sql = "INSERT into nco_gig (gig_name, referee, created_by) VALUES (?,?,?)";
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setString(1, getGigName());
            stat.setString(2, getRefereeAsTag());
            stat.setString(3, author.getAsTag());
            return stat.executeUpdate() == 1;
        }
    }

    private String getRefereeAsMention() {
        return messageArgs.length == 2 ? guild.getMemberByTag(messageArgs[0]).getAsMention() : author.getAsMention();
    }

    private String getRefereeAsTag() {
        return messageArgs.length == 2 ? messageArgs[0] : author.getAsTag();
    }

    private String getGigName() {
        return messageArgs[messageArgs.length - 1];
    }

    private String getGigId(Connection conn) throws SQLException {
        String sql = "select id from nco_gig where referee = ? and completed_yn = 'n' order by id desc";
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setString(1, getRefereeAsTag());
            try (ResultSet rs = stat.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("id");
                } else {
                    return null;
                }
            }
        }
    }

    @Override
    protected String getHelpTitle() {
        return "Incorrect Create Gig Formatting";
    }

    @Override
    protected String getHelpDescription() {
        return "Please use the commands below to create a gig in the system \n" + RedBot.PREFIX +
                "gigcreate \"Referee's Discord Tag(Optional)\", \"Gig Name\"";
    }

}
