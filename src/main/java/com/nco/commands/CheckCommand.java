package com.nco.commands;

import com.nco.RedBot;
import com.nco.pojos.PlayerCharacter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CheckCommand extends AbstractCommand {

    public CheckCommand(String[] messageArgs, User author, MessageChannel channel, Member member) {
        super(messageArgs, author, channel, member);
    }

    @Override
    protected boolean canProcessByUser() {
        return messageArgs[0].isEmpty();
    }

    @Override
    protected boolean canProcessByName() {
        return messageArgs.length == 1;
    }

    @Override
    protected void processUpdateAndRespond(Connection conn, PlayerCharacter pc, EmbedBuilder builder) throws SQLException {
        String characterName = pc.getCharacterName();
        int currentHum = pc.getHumanity();

        builder.setTitle(characterName);
        builder.addField("Bank", String.valueOf(pc.getBank()), true);
        builder.addField("HP", pc.getCurrentHP() + "/" +
                pc.getMaxHP(), true);
        builder.addField("Humanity", currentHum + "/" +
                pc.getMaxHumanity(), true);
        builder.addField("Monthly", pc.getPayDues(), true);
        builder.addField("Down Time", String.valueOf(pc.getDowntime()), true);
        builder.addField("IP", String.valueOf(pc.getInfluencePoints()), true);
        builder.addField("Reputation", String.valueOf(pc.getReputation()), true);
        builder.addField("Weekly Games", String.valueOf(pc.getWeeklyGames()), true);

        buildDescription(builder, currentHum, characterName, conn);
    }

    private void buildDescription(EmbedBuilder builder, int currentHum, String characterName, Connection conn) throws SQLException {
        buildDescriptionForInjury(builder, characterName, conn);
        buildDescriptionForPsychosis(builder, currentHum);
    }

    private static void buildDescriptionForInjury(EmbedBuilder builder, String characterName, Connection conn) throws SQLException {
        String sql = "SELECT * FROM NCO_CRITICAL_INJURIES WHERE character_name = ?";
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setString(1, characterName);
            try (ResultSet rs = stat.executeQuery()) {
                boolean first = true;
                while (rs.next()) {
                    if (first) {
                        builder.setDescription(rs.getString("Injury"));
                        first = false;
                    } else {
                        builder.appendDescription("\n" + rs.getString("Injury"));
                    }
                }
            }
        }
    }

    private static void buildDescriptionForPsychosis(EmbedBuilder builder, int currentHum) {
        if (currentHum < 30) {
            if (!builder.getDescriptionBuilder().toString().isEmpty()) {
                builder.appendDescription("\n");
            }
            if (currentHum >= 20) {
                builder.appendDescription("Borderline dissociative disorder");
            } else if (currentHum >= 10) {
                builder.appendDescription("Dissociative disorder, borderline cyberpsychosis");
            } else if (currentHum >= 0) {
                builder.appendDescription("Cyberpsychosis");
            } else {
                builder.appendDescription("Extreme cyberpsychosis");
            }
        }
    }

    @Override
    protected String getHelpTitle() {
        return "Incorrect Check Formatting";
    }

    @Override
    protected String getHelpDescription() {
        return "Please use the commands below to see information on characters \n" + RedBot.PREFIX +
                "check \"PC Name(Optional)\"";
    }

}
