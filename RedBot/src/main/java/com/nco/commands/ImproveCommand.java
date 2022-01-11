package com.nco.commands;

import com.nco.RedBot;
import com.nco.enums.Skills;
import com.nco.pojos.PlayerCharacter;
import com.nco.utils.NCOUtils;
import com.nco.utils.NumberUtils;
import com.nco.utils.StringUtils;
import net.dv8tion.jda.api.EmbedBuilder;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ImproveCommand extends AbstractCommand {

    public ImproveCommand(String[] messageArgs, Object event, boolean isSlash) {
        super(messageArgs, event, isSlash);
    }

    @Override
    protected boolean canProcessByUser() {
        return messageArgs.length == 2 && NumberUtils.isNumeric(messageArgs[1]);
    }

    @Override
    protected boolean canProcessByName() {
        return messageArgs.length == 3 && NumberUtils.isNumeric(messageArgs[2]);
    }



    @Override
    protected void processUpdateAndRespond(Connection conn, PlayerCharacter pc, EmbedBuilder builder) throws Exception {
        int newIP = pc.getImprovementPoints() - NumberUtils.asPositive(messageArgs[2]);
        Skills skill = StringUtils.getEnumFromString(Skills.class, StringUtils.formalToSnake(messageArgs[1]));
        int skillLevel = getSkillLevel(pc, skill);
        if (skill == null) {
            builder.setTitle("ERROR: Skill Not Recognised");
            builder.setDescription("Please contact an administrator to get this resolved");
        } else if (NCOUtils.getSkillLevelUpIP(skill, skillLevel) != NumberUtils.asPositive(messageArgs[2])) {
            builder.setTitle("ERROR: Incorrect IP To Upgrade Skill");
            builder.setDescription(StringUtils.camelToFormal(skill.toString()) + " is at level " + skillLevel +
                    " and requires " + NCOUtils.getSkillLevelUpIP(skill, skillLevel) + " IP move to level "
                    + (skillLevel + 1) + ".");
        } else if (newIP < 0) {
            builder.setTitle("ERROR: Not Enough IP");
            builder.setDescription(StringUtils.capitalizeWords(messageArgs[0]) + " has only " + pc.getImprovementPoints() + " IP available " +
                    "where " + NumberUtils.asPositive(messageArgs[2]) + " IP is being used.");
        } else if (updateImprove(newIP, conn) && updateSkill(skill, skillLevel, conn)) {
            buildEmbeddedContent(pc, builder, newIP, skill, skillLevel);
        } else {
            builder.setTitle("ERROR: Improve Update Or Insert Failure");
            builder.setDescription("Please contact an administrator to get this resolved");
        }
    }

    private int getSkillLevel(PlayerCharacter pc, Skills skill) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
         if (skill == null) {
             return 0;
         } else {
             return (int) pc.getClass().getMethod("get" + StringUtils.capitalSnakeToCamelCase(skill.toString())).invoke(pc);
         }
    }

    private boolean updateImprove(int newIP, Connection conn) throws SQLException {
        String sql = "UPDATE NCO_PC set improvement_points = ? where character_name = ?";
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setInt(1, newIP);
            stat.setString(2, messageArgs[0]);
            return stat.executeUpdate() == 1;
        }
    }

    private boolean updateSkill(Skills skill, int skillLevel, Connection conn) throws SQLException {
        String sql = "UPDATE nco_pc set " + skill.toString() + " = ? where character_name = ?";
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setInt(1, skillLevel + 1);
            stat.setString(2, messageArgs[0]);
            return stat.executeUpdate() == 1;
        }
    }

    private void buildEmbeddedContent(PlayerCharacter pc, EmbedBuilder builder, int newIP, Skills skill, int skillLevel) {
        builder.setTitle(StringUtils.capitalizeWords(messageArgs[0]) + "'s Skills & IP Updated");
        builder.addField("Old " + StringUtils.snakeToFormal(skill.toString()), String.valueOf(skillLevel), true);
        builder.addBlankField(true);
        builder.addField("New " + StringUtils.snakeToFormal(skill.toString()), String.valueOf(skillLevel + 1), true);
        builder.addField("Old IP", String.valueOf(pc.getImprovementPoints()), true);
        builder.addBlankField(true);
        builder.addField("New IP", String.valueOf(newIP), true);
    }

    @Override
    protected String getHelpTitle() {
        return "Incorrect Improve Formatting";
    }

    @Override
    protected String getHelpDescription() {
        return "Please use the commands below to improve a characters bank\n" + RedBot.PREFIX +
                "improve \"PC Name(Optional)\" \"Skill Name\" \"IP To Next Level\"";
    }

}
