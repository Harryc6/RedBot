package com.nco.commands;

import com.nco.RedBot;
import com.nco.enums.Crits;
import com.nco.pojos.PlayerCharacter;
import com.nco.utils.NumberUtils;
import com.nco.utils.StringUtils;
import net.dv8tion.jda.api.EmbedBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class GigCritCommand extends AbstractCommand {

    public GigCritCommand(String[] messageArgs, Object event, boolean isSlash) {
        super(messageArgs, event, isSlash);
    }

    @Override
    protected String[] getRoleRequiredForCommand() {
        return new String[]{"Referees", "Junior Referees"};
    }

    @Override
    protected boolean canProcessByUser() {
        return canProcess(0);
    }

    private boolean canProcess(int offset) {
        return messageArgs.length == offset + 2 && (messageArgs[offset].equalsIgnoreCase("head")
                || messageArgs[offset].equalsIgnoreCase("body")) &&
                (1 < NumberUtils.ParseNumber(messageArgs[offset + 1]) && NumberUtils.ParseNumber(messageArgs[offset + 1]) <= 12);
    }

    @Override
    protected boolean canProcessByName() {
        return canProcess(1);
    }

    @Override
    protected void processUpdateAndRespond(Connection conn, PlayerCharacter pc, EmbedBuilder builder) throws SQLException {
        Crits critRecieved= Crits.valueOfRoll(messageArgs[1], Integer.parseInt(messageArgs[2]));
        if (insertCriticalInjury(conn, critRecieved)) {
            buildEmbeddedContent(builder, critRecieved);
        } else {
            builder.setTitle("ERROR: Critical Injury Insert Failure");
            builder.setDescription("Please contact an administrator to get this resolved");
        }
    }

    private boolean insertCriticalInjury(Connection conn, Crits critRecieved) throws SQLException {
        String sql = "INSERT INTO NCO_CRITICAL_INJURIES (character_name, injury)  VALUES (?, ?)";
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setString(1, messageArgs[0]);
            stat.setString(2, critRecieved.injuryDesc);
            return stat.executeUpdate() == 1;
        }
    }

    private void buildEmbeddedContent(EmbedBuilder builder, Crits critRecieved) {
        builder.setTitle("Critical Injury Added To " + StringUtils.capitalizeWords(messageArgs[0]));
        builder.setDescription("Recieved \"" + critRecieved.injuryDesc + "\"");
        for (Crits.CritFixType critFixType : critRecieved.critFixTypes) {
            builder.addField(critFixType.type, critFixType.fix, true);
        }
    }

    @Override
    protected String getHelpTitle() {
        return "Incorrect Gig Critical Formatting";
    }

    @Override
    protected String getHelpDescription() {
        return "Please use the commands below to log a critical \n" + RedBot.PREFIX +
                "gigcrit \"PC Name(Optional)\" \"Head Or Body Table\" \"Rolled Value\"";
    }
}
