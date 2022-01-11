package com.nco.commands;

import com.nco.RedBot;
import com.nco.pojos.PlayerCharacter;
import com.nco.utils.NCOUtils;
import com.nco.utils.NumberUtils;
import com.nco.utils.StringUtils;
import net.dv8tion.jda.api.EmbedBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GigLogCommand extends AbstractCommand {

    public GigLogCommand(String[] messageArgs, Object event, boolean isSlash) {
        super(messageArgs, event, isSlash);
    }

    @Override
    protected boolean canProcessByUser() {
        return isValidGigLogFormatting(0);
    }

    private boolean isValidGigLogFormatting(int offset) {
        return messageArgs.length == 8 + offset && NumberUtils.isNumeric(messageArgs[0 + offset]) &&
                NumberUtils.isNumeric(messageArgs[1 + offset]) && NumberUtils.isNumeric(messageArgs[2 + offset]) &&
                NumberUtils.isNumeric(messageArgs[3 + offset]) && NumberUtils.isNumeric(messageArgs[4 + offset]) &&
                NumberUtils.isNumeric(messageArgs[5 + offset]) && NumberUtils.isNumeric(messageArgs[6 + offset]);
    }

    @Override
    protected boolean canProcessByName() {
        return isValidGigLogFormatting(1);
    }

    @Override
    protected void processUpdateAndRespond(Connection conn, PlayerCharacter pc, EmbedBuilder builder) throws SQLException {
        if (validGigID(conn)) {
            if (pc.getMaxHp() >= Integer.parseInt(messageArgs[4])) {
                if (updateCharacter(pc, conn) && insertFame(conn) && updateMonthly(pc, conn)) {
                    buildEmbeddedContent(conn, pc, builder);
                } else {
                    builder.setTitle("ERROR: Install Update Or Insert Failure");
                    builder.setDescription("Please contact an administrator to get this resolved");
                }
            } else {
                builder.setTitle("ERROR: Can not Exceed Max HP");
                builder.setDescription("You can not set current HP higher than your maximum HP");
            }
        } else {
            builder.setTitle("No Active Gig Found");
            builder.setDescription("No active gig was found with the ID of " + messageArgs[1]);
        }
    }

    private void buildEmbeddedContent(Connection conn, PlayerCharacter pc, EmbedBuilder builder) throws SQLException {
        builder.setTitle(StringUtils.capitalizeWords(messageArgs[0]) + "'s Gig #" + messageArgs[1] + " Logging");
        builder.setDescription((pc.getGamesTillMonthlyDue() <= 1 ? "**Monthly Due!**\n" : "") +
                "Gig " + getGigName(conn) + "\nReputation was gained for \"" + messageArgs[8] + "\"");
        builder.addField("Old Balance", pc.getBank() + "eb", true);
        builder.addBlankField(true);
        builder.addField("New Balance", getNewBank(pc) + "eb", true);
        builder.addField("Old IP", String.valueOf(pc.getImprovementPoints()), true);
        builder.addBlankField(true);
        builder.addField("New IP", String.valueOf(getNewIP(pc)), true);
        builder.addField("Old HP", pc.getCurrentHp() + " / " + pc.getMaxHp(), true);
        builder.addBlankField(true);
        builder.addField("New HP", messageArgs[4] + " / " + pc.getMaxHp(), true);
        builder.addField("Old SP H | B", pc.getHeadSp() + " | " + pc.getBodySp(), true);
        builder.addBlankField(true);
        builder.addField("New SP H | B", messageArgs[5] + " | " + messageArgs[6], true);
        builder.addField("Old Fame | Rep", pc.getFame() + " | " + pc.getReputation(), true);
        builder.addBlankField(true);
        builder.addField("New Fame | Rep", getNewFame(pc) + " | " + getNewReputation(pc), true);
    }

    private String getGigName(Connection conn) throws SQLException {
        String sql = "select gig_name from nco_gig where id = ? and completed_yn = 'n'";
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setInt(1, NumberUtils.ParseNumber(messageArgs[1]));
            try (ResultSet rs = stat.executeQuery()) {
                if (rs.next()) {
                    return " \"" + rs.getString("gig_name") + "\"";
                } else {
                    return "";
                }
            }
        }
    }

    private boolean validGigID(Connection conn) throws SQLException {
        String sql = "select * from nco_gig where id = ? and completed_yn = 'n'";
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setInt(1, NumberUtils.ParseNumber(messageArgs[1]));
            try (ResultSet rs = stat.executeQuery()) {
                return rs.next();
            }
        }
    }

    private boolean updateCharacter(PlayerCharacter pc, Connection conn) throws SQLException {
        return updatePC(pc, conn) && updateStats(conn);
    }

    private boolean updatePC(PlayerCharacter pc, Connection conn) throws SQLException {
        String sql = "UPDATE NCO_PC set bank = ?, improvement_points = ?, head_sp = ?, body_sp = ?, reputation = ?, fame = ?, weekly_games = ?, games_overall = ? Where character_name = ?";
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setInt(1, getNewBank(pc));
            stat.setInt(2, getNewIP(pc));
            stat.setInt(3, NumberUtils.ParseNumber(messageArgs[5]));
            stat.setInt(4, NumberUtils.ParseNumber(messageArgs[6]));
            stat.setInt(5, getNewReputation(pc));
            stat.setInt(6, getNewFame(pc));
            stat.setInt(7, pc.getWeeklyGames() + 1);
            stat.setInt(8, pc.getGamesOverall() + 1);
            stat.setString(9, messageArgs[0]);
            return stat.executeUpdate() == 1;
        }
    }

    private int getNewBank(PlayerCharacter pc) {
        return pc.getBank() + NumberUtils.ParseNumber(messageArgs[2]);
    }

    private int getNewIP(PlayerCharacter pc) {
        return pc.getImprovementPoints() + NumberUtils.ParseNumber(messageArgs[3]);
    }

    private int getNewReputation(PlayerCharacter pc) {
        return NCOUtils.getReputationFromFame(getNewFame(pc));
    }

    private int getNewFame(PlayerCharacter pc) {
        return pc.getFame() + NumberUtils.asPositive(messageArgs[7]);
    }

    private boolean updateStats(Connection conn) throws SQLException {
        String sql = "UPDATE nco_pc set current_hp = ? Where character_name = ?";
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setInt(1, NumberUtils.ParseNumber(messageArgs[4]));
            stat.setString(2, messageArgs[0]);
            return stat.executeUpdate() == 1;
        }
    }

    private boolean insertFame(Connection conn) throws SQLException {
        String sql = "INSERT INTO NCO_FAME (character_name, reason, fame, created_by) VALUES (?,?,?,?)";
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setString(1, messageArgs[0]);
            stat.setString(2, messageArgs[8]);
            stat.setInt(3, NumberUtils.asPositive(messageArgs[7]));
            stat.setString(4, author.getAsTag());
            return stat.executeUpdate() == 1;
        }
    }

    private boolean updateMonthly(PlayerCharacter pc, Connection conn) throws SQLException {
        if (pc.getGamesTillMonthlyDue() <= 1) {
            return updateMonthlyDues(pc, conn);
        } else {
            return true;
        }
    }

    private boolean updateMonthlyDues(PlayerCharacter pc, Connection conn) throws SQLException {
        String sql = "UPDATE NCO_PC set monthly_debt = ?, monthly_due = ? Where character_name = ?";
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setInt(1, getDebt(pc));
            stat.setInt(2, pc.getMonthlyDue() + 5);
            stat.setString(3, messageArgs[0]);
            return stat.executeUpdate() == 1;
        }
    }

    private int getDebt(PlayerCharacter pc) {
        int debt = pc.getMonthlyDebt();
        debt += NCOUtils.getHousing().get(pc.getRent());
        debt += NCOUtils.getLifestyle().get(pc.getLifestyle());
        return debt;
    }


    @Override
    protected String getHelpTitle() {
        return "Incorrect Gig Log Formatting";
    }

    @Override
    protected String getHelpDescription() {
        return "Please use the commands below to log a characters time \n" + RedBot.PREFIX +
                "giglog \"PC Name(Optional)\" \"Gig ID\" \"Eddies\" \"IP\" \"HP\" \"Head SP\" \"Body SP\" \"Fame\" \"Fame Reason\"";
    }

}
