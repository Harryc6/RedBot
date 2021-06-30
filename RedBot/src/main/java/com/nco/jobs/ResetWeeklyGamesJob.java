package com.nco.jobs;

import com.nco.utils.DBUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ResetWeeklyGamesJob implements Job {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.info("Started running ResetWeeklyGamesJob");
        int updateCount = 0;
        int maxedCount = 0;
        String sql = "Select weekly_games, character_name from NCO_PC where retired_yn = 'n'";
        String sql2 = "UPDATE NCO_PC set weekly_games = 0 where character_name = ?";

        try(Connection conn = DBUtils.getConnection(); ResultSet rs = conn.prepareStatement(sql).executeQuery()) {
            conn.setAutoCommit(false);
            while (rs.next()) {
                if (rs.getInt("weekly_games") > 0) {
                    try(PreparedStatement stat = conn.prepareStatement(sql2)) {
                        stat.setString(1, rs.getString("character_name"));
                        if (stat.executeUpdate() == 1) {
                            updateCount++;
                        } else {
                            logger.error("Failed to reset weekly games for " + rs.getString("character_name"));
                        }
                    }
                } else {
                    maxedCount++;
                }
            }
            conn.commit();
            String log = "Finished running ResetWeeklyGamesJob. " + updateCount + " characters updated";
            if (maxedCount != 0) {
                log += " & " + maxedCount + " characters did not have any games.";
            }
            logger.info(log);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}
