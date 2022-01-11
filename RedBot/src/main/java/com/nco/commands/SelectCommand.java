package com.nco.commands;

import com.nco.RedBot;
import com.nco.enums.Skills;
import com.nco.pojos.PlayerCharacter;
import com.nco.utils.StringUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import org.apache.commons.lang3.EnumUtils;

import java.awt.*;
import java.sql.*;

public class SelectCommand extends AbstractCommand {

    public SelectCommand(String[] messageArgs, Object event, boolean isSlash) {
        super(messageArgs, event, isSlash);
    }

    @Override
    protected String[] getRoleRequiredForCommand() {
        return new String[]{"Tech-Support"};
    }

    @Override
    protected boolean canProcessByName() {
        return (messageArgs.length == 1 || messageArgs.length == 2) && !messageArgs[0].isEmpty();
    }

    @Override
    protected void processUpdateAndRespond(Connection conn, PlayerCharacter pc, EmbedBuilder builder) throws SQLException {
        String sql = "Select * From NCO_PC where character_name = ?";
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setString(1, messageArgs[0]);
            try (ResultSet rs = stat.executeQuery()) {
                ResultSetMetaData rsmd = rs.getMetaData();
                if (rs.next()) {
                    int endIndex = getEndIndex(rsmd);
                    int embedCount = 1;
                    builder.setTitle("Select of " + StringUtils.capitalizeWords(messageArgs[0]) +
                            " (1/" + Math.round((endIndex / 25) + 0.5) + ")");
                    for (int i = 1; i < rsmd.getColumnCount() + 1; i++) {
                        if (builder.getFields().size() >= 24) {
                            if (event == null) {
                                    channel.sendMessageEmbeds(builder.build()).queue();
                            } else {
                                event.replyEmbeds(builder.build()).complete();
                                event = null;
                            }
                            builder.clear();
                            builder.setColor(Color.red);
                            builder.setTitle("Select of " + StringUtils.capitalizeWords(messageArgs[0]) +
                                    " (" + ++embedCount + "/" + Math.round((endIndex / 25) + 0.5) + ")");
                        }
                        Skills skill = StringUtils.getEnumFromString(Skills.class, rsmd.getColumnName(i));
                        if (showAllColumns() || skill == null) {
                            builder.addField(rsmd.getColumnName(i), StringUtils.checkNull(rs.getString(i)), true);
                        }

                    }
                }
            }
        }
    }

    private int getEndIndex(ResultSetMetaData rsmd) throws SQLException {
        if (showAllColumns()) {
            return rsmd.getColumnCount() + 1;
        }
        int columnCount = 1;
        for (int i = 1; i < rsmd.getColumnCount(); i++) {
            Skills skill = StringUtils.getEnumFromString(Skills.class, rsmd.getColumnName(i));
            if (skill == null) {
                columnCount++;
            }
        }
        return columnCount;
    }

    private boolean showAllColumns() {
        return messageArgs.length == 2 && messageArgs[1].equalsIgnoreCase("true");
    }

    @Override
    protected String getHelpTitle() {
        return "Incorrect Select Formatting";
    }

    @Override
    protected String getHelpDescription() {
        return "Please use the commands below to select the data directly from the NCO_PC table in the database. \n" + RedBot.PREFIX +
                "select \"PC Name\" \"Show Skills(Optional)\"";
    }
}
