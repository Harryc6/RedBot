package com.nco.commands;

import com.nco.RedBot;
import com.nco.pojos.PlayerCharacter;
import com.nco.utils.NumberUtils;
import com.nco.utils.RPGDice;
import com.nco.utils.tables.HustleRow;
import com.nco.utils.tables.HustleTables;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class HustleCommand extends AbstractCommand {

    public HustleCommand(String[] messageArgs, User author, MessageChannel channel, Member member) {
        super(messageArgs, author, channel, member);
    }

    @Override
    protected boolean canProcessByUser() {
        return messageArgs.length == 2 && NumberUtils.isNumeric(messageArgs[0]) && NumberUtils.isNumeric(messageArgs[1]) && rollLevelInRange();
    }

    private boolean rollLevelInRange() {
        return Integer.parseInt(messageArgs[messageArgs.length - 2]) > 0 &&
                Integer.parseInt(messageArgs[messageArgs.length - 2]) <= 10;
    }

    @Override
    protected boolean canProcessByName() {
        return messageArgs.length == 3 && NumberUtils.isNumeric(messageArgs[1]) && NumberUtils.isNumeric(messageArgs[2]) && rollLevelInRange();
    }

    @Override
    protected void processUpdateAndRespond(Connection conn, PlayerCharacter pc, EmbedBuilder builder) throws SQLException {
        int attempts = NumberUtils.asPositive(messageArgs[2]);
        ArrayList<HustleRow> jobList = getJobList(pc, attempts);
        if (pc.getDowntime() < (attempts * 7)) {
            builder.setTitle("ERROR: Not Enough DT");
            builder.setDescription(messageArgs[0] + " has " + pc.getDowntime() + " DT available where " +
                    (attempts * 7) + " DT is required for " + attempts + " attempts");
        } else if (updateHustle(conn, pc, jobList, attempts) && insertHustle(conn, pc, jobList, attempts)){
            buildEmbeddedContent(pc, builder, attempts, jobList);
        } else {
            builder.setTitle("ERROR: Improve Update Or Insert Failure");
            builder.setDescription("Please contact an administrator to get this resolved");
        }
    }

    private boolean updateHustle(Connection conn, PlayerCharacter pc, ArrayList<HustleRow> jobList, int attempts) throws SQLException {
        String sql = "UPDATE NCO_PC set bank = ?, downtime = ? Where character_name = ?";
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setInt(1, pc.getBank() + getTotalEarnings(jobList));
            stat.setInt(2, pc.getDowntime() - (attempts * 7));
            stat.setString(3, messageArgs[0]);
            return stat.executeUpdate() == 1;
        }
    }

    private boolean insertHustle(Connection conn, PlayerCharacter pc, ArrayList<HustleRow> jobList, int attempts) throws SQLException {
        String sql  = "INSERT INTO nco_hustle (character_name, role_level, total_earnings, old_dt, new_dt, attempts, created_by) VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setString(1, messageArgs[0]);
            stat.setInt(2, Integer.parseInt(messageArgs[1]));
            stat.setInt(3, getTotalEarnings(jobList));
            stat.setInt(4, pc.getDowntime());
            stat.setInt(5, pc.getDowntime() - (attempts * 7));
            stat.setInt(6, Integer.parseInt(messageArgs[2]));
            stat.setString(7, author.getAsTag());
            return stat.executeUpdate() == 1;
        }
    }

    private ArrayList<HustleRow> getJobList(PlayerCharacter pc, int attempts) {
        ArrayList<HustleRow> jobList = new ArrayList();
        for (int i = 0; i < attempts; i++) {
            jobList.add(HustleTables.getRoleTable(pc).get(RPGDice.roll("1d6-1")));
        }
        return jobList;
    }

    private void buildEmbeddedContent(PlayerCharacter pc, EmbedBuilder builder, int attempts, ArrayList<HustleRow> jobList) {
        builder.setTitle(messageArgs[0] + "'s Hustle");
        for (HustleRow row : jobList) {
            builder.addField(row.getJob(), getPayout(row) + "eb", false);
        }
        builder.addField("Old Bank", pc.getBank() + "eb", true);
        if (jobList.size() > 1) {
            builder.addField("Total Earnings", getTotalEarnings(jobList) + "eb", true);
        } else {
            builder.addBlankField(true);
        }
        builder.addField("New Bank", (pc.getBank() + getTotalEarnings(jobList)) + "eb", true);
        builder.addField("Old DT", String.valueOf(pc.getDowntime()), true);
        builder.addBlankField(true);
        builder.addField("New DT", String.valueOf(pc.getDowntime() - (attempts * 7)), true);
    }

    private int getPayout(HustleRow row) {
        if (Integer.parseInt(messageArgs[1]) < 5) {
            return row.getLowPayout();
        } else if (Integer.parseInt(messageArgs[1]) < 8) {
            return row.getMediumPayout();
        } else {
            return row.getHighPayout();
        }
    }


    private int getTotalEarnings(ArrayList<HustleRow> jobList) {
        int total = 0;
        for (HustleRow row : jobList) {
            total += getPayout(row);
        }
        return total;
    }

    @Override
    protected String getHelpTitle() {
        return "Incorrect Hustle Formatting";
    }

    @Override
    protected String getHelpDescription() {
        return "Please use the commands below to hustle \n" + RedBot.PREFIX +
                "hustle \"PC Name(Optional)\" \"Role Level\" \"Attempts\"";
    }
}
