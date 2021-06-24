package com.nco.commands;

import com.nco.RedBot;
import com.nco.pojos.PlayerCharacter;
import com.nco.utils.StringUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.sql.*;

public class UpdateCommand extends AbstractCommand {

    public UpdateCommand(String[] messageArgs, GuildMessageReceivedEvent event) {
        super(messageArgs, event);
    }

    @Override
    protected String getRoleRequiredForCommand() {
        return "Tech-Support";
    }

    @Override
    protected boolean canProcessByName() {
        return messageArgs.length == 3;
    }

    @Override
    protected void processUpdateAndRespond(Connection conn, PlayerCharacter pc, EmbedBuilder builder) throws SQLException {
        builder.setTitle("Update of " + messageArgs[0]);
        String sql = "Select * From NCO_PC where character_name = ?";
        try(PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setString(1, messageArgs[0]);
            try (ResultSet rs = stat.executeQuery()) {
                ResultSetMetaData rsmd = rs.getMetaData();
                int columnType = rsmd.getColumnType(rs.findColumn(messageArgs[1]));
                if (rs.next() && updateTable(conn, pc, columnType)) {
                    builder.addField("Old " + messageArgs[1], StringUtils.checkNull(rs.getString(messageArgs[1])), true);
                    builder.addBlankField(true);
                    builder.addField("New " + messageArgs[1], messageArgs[2], true);
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private boolean updateTable(Connection conn, PlayerCharacter pc, int columnType) throws SQLException {
        String sql = "UPDATE NCO_PC set " + messageArgs[1] + " = ? Where character_name = ?";

        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            setVariableDataType(columnType, stat);
            stat.setString(2, messageArgs[0]);
            return stat.executeUpdate() == 1;
        }
    }

    private void setVariableDataType(int columnType, PreparedStatement stat) throws SQLException {
        switch (columnType) {
            case 4: // int
                stat.setInt(1, Integer.parseInt(messageArgs[2]));
                break;
            case 12: // string
                stat.setString(1, messageArgs[2]);
                break;
            case 91: // date
                stat.setDate(1, Date.valueOf(messageArgs[2]));
                break;
            case 93: // timestamp
                stat.setTimestamp(1, Timestamp.valueOf(messageArgs[2]));
                break;
        }
    }

    @Override
    protected String getHelpTitle() {
        return "Incorrect Update Formatting";
    }

    @Override
    protected String getHelpDescription() {
        return "Please use the commands below to update a column on the NCO_PC table in the database. \n" + RedBot.PREFIX +
                "update \"PC Name\" \"Column Name\" \"New Column Value\"";
    }
}
