package com.nco.jobs;

import com.nco.utils.DBUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class IncrementDownTimeJob implements Job {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.info("Started running IncrementDownTimeJob");
        int updateCount = 0;
        int maxedCount = 0;
        String sql = "Select * from NCO_PC where retired_yn = 'n'";
        String sql2 = "UPDATE NCO_PC set downtime = ? where character_name = ?";

        try(Connection conn = DBUtils.getConnection(); ResultSet rs = conn.prepareStatement(sql).executeQuery()) {
            conn.setAutoCommit(false);
            while (rs.next()) {
                if (rs.getInt("downtime") < 84) {
                    try(PreparedStatement stat = conn.prepareStatement(sql2)) {
                        stat.setInt(1, rs.getInt("downtime") + 1);
                        stat.setString(2, rs.getString("character_name"));
                        if (stat.executeUpdate() == 1) {
                            updateCount++;
                        } else {
                            logger.error("Failed up increment DT for " + rs.getString("character_name"));
                        }
                    }
                } else {
                    maxedCount++;
                }
            }
            conn.commit();
            String log = "Finished running IncrementDownTimeJob. " + updateCount + " characters updated";
            if (maxedCount != 0) {
                log += " & " + maxedCount + " characters skipped";
            }
            logger.info(log);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
