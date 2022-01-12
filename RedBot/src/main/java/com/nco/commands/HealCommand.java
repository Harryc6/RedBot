package com.nco.commands;

import com.nco.RedBot;
import com.nco.pojos.PlayerCharacter;
import com.nco.utils.DBUtils;
import com.nco.utils.NumberUtils;
import com.nco.utils.StringUtils;
import net.dv8tion.jda.api.EmbedBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class HealCommand extends AbstractCommand {

    public HealCommand(String[] messageArgs, Object event, boolean isSlash) {
        super(messageArgs, event, isSlash);
    }

    @Override
    protected boolean canProcessByUser() {
        return messageArgs.length >= 1 && !DBUtils.doesCharacterExist(messageArgs[0]) && NumberUtils.isNumeric(messageArgs[0]);
    }

    @Override
    protected boolean canProcessByName() {
        return messageArgs.length >= 2 && DBUtils.doesCharacterExist(messageArgs[0]) && NumberUtils.isNumeric(messageArgs[1]);
    }

    @Override
    protected void processUpdateAndRespond(Connection conn, PlayerCharacter pc, EmbedBuilder builder) throws SQLException {
        if (Integer.parseInt(messageArgs[1]) > pc.getDowntime()) {
            builder.setTitle("ERROR: Not Enough DT");
            builder.addField("You have:", String.valueOf(pc.getDowntime()), true);
        } else if (pc.getCurrentHp() == pc.getMaxHp()) {
            builder.setTitle("ERROR: You are at full HP");
            builder.addField("Your current HP :", String.valueOf(pc.getCurrentHp()), true);
            builder.addField("Your max HP:", String.valueOf(pc.getMaxHp()), true);
        } else {
            int dtAmount = NumberUtils.asPositive(messageArgs[1]);
            int newHP = getNewHP(pc, dtAmount);
            int newDT = pc.getDowntime() - getTotalDTUsed(pc, dtAmount);
            if (updateHP(conn, newHP, newDT, pc)) {
                buildEmbed(builder, pc, newHP, newDT, dtAmount);
            } else {
                builder.setTitle("ERROR: Install Update Or Insert Failure");
                builder.setDescription("Please contact an administrator to get this resolved");
            }
        }
    }

    public int getNewHP(PlayerCharacter pc, int dtAmount) {
        int newHP = getHealedHPByDT(pc, dtAmount);
        if (newHP > pc.getMaxHp()) {
            newHP = pc.getMaxHp();
        }
        return newHP;
    }

    private int getHealedHPByDT(PlayerCharacter pc, int dtAmount) {
        int newHP = pc.getCurrentHp();
        while (dtAmount > 0 && newHP < pc.getMaxHp()) {
            newHP += getDaysHealedHP(pc);
            dtAmount--;
        }
        return newHP;
    }

    private int getTotalDTUsed(PlayerCharacter pc, int dtAmount) {
        int newHP = pc.getCurrentHp();
        int dtRemaining = dtAmount;
        while (dtRemaining > 0 && newHP < pc.getMaxHp()) {
            newHP += getDaysHealedHP(pc);
            dtRemaining--;
        }
        return dtAmount - dtRemaining;
    }

    private int getDaysHealedHP(PlayerCharacter pc) {
        int healedHP = (pc.getBody() + getBonuses(pc)) * getMultiplier();
        if (messageArgs.length == 3 && messageArgs[2].toLowerCase().contains("speedheal")) {
            healedHP += pc.getBody() + pc.getWillpower();
        }
        return healedHP;
    }

    private int getMultiplier() {
        return messageArgs.length == 3 && messageArgs[2].toLowerCase().contains("cryotank") ? 2 : 1;
    }

    private int getBonuses(PlayerCharacter pc) {
        int bonuses = 0;
        if (messageArgs.length == 3) {
            if (messageArgs[2].toLowerCase().contains("enhanced")) {
                bonuses += pc.getBody();
            }
            if (messageArgs[2].toLowerCase().contains("antibodies")) {
                bonuses += 2;
            }
        }
        if (pc.getLifestyle().equalsIgnoreCase("Generic Prepak")) {
            bonuses += 2;
        }
        if (pc.getLifestyle().equalsIgnoreCase("Good Prepak")) {
            bonuses += 3;
        }
        if (pc.getLifestyle().equalsIgnoreCase("Fresh Food")) {
            bonuses += 4;
        }
        return bonuses;
    }

    private boolean updateHP(Connection conn, int newHP, int newDT, PlayerCharacter pc) throws SQLException {
        return updatePc(conn, newDT, pc) && updateStats(conn, newHP);
    }

    private boolean updatePc(Connection conn, int newDT, PlayerCharacter pc) throws SQLException {
        String sql = "UPDATE NCO_PC set downtime = ? Where character_name = ?";
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setInt(1, (newDT * 12) + pc.getDowntimeRemainder());
            stat.setString(2, messageArgs[0]);
            return stat.executeUpdate() == 1;
        }
    }

    private boolean updateStats(Connection conn, int newHP) throws SQLException {
        String sql = "UPDATE nco_pc set current_hp = ? Where character_name = ?";
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setInt(1, newHP);
            stat.setString(2, messageArgs[0]);
            return stat.executeUpdate() == 1;
        }
    }

    private void buildEmbed(EmbedBuilder builder, PlayerCharacter pc, int newHP, int newDT, int dtAmount) {
        builder.setTitle(StringUtils.capitalizeWords(messageArgs[0]) + "'s HP Restored");
        builder.setDescription("Used " + getTotalDTUsed(pc, dtAmount) + " DT to heal " +
                (getDaysHealedHP(pc) * getTotalDTUsed(pc, dtAmount)) + " HP capped by max HP");
        builder.addField("Old HP", pc.getCurrentHp() + "/" + pc.getMaxHp(), true);
        builder.addBlankField(true);
        builder.addField("New HP", newHP + "/" + pc.getMaxHp(), true);
        builder.addField("Old DT", pc.getDowntimeToDisplay(), true);
        builder.addBlankField(true);
        builder.addField("New DT", getNewDowntimeToDisplay(pc, newDT), true);

        if (messageArgs.length == 3 && messageArgs[2].toLowerCase().contains("speedheal")) {
            builder.addField("Body | Will", pc.getBody() + " | " + pc.getWillpower(), true);
        } else {
            builder.addField("Body", String.valueOf(pc.getBody()), true);
        }
        builder.addField("Bonus", "+" + getBonuses(pc), true);
        builder.addField("Multiplier", "x" + getMultiplier(), true);
    }

    private String getNewDowntimeToDisplay(PlayerCharacter pc, int newDT) {
        return newDT + ((pc.getDowntimeRemainder() == 0) ? "" :
                " " + StringUtils.superscript(String.valueOf(pc.getDowntimeRemainder())) + "/" + StringUtils.subscript("12"));
    }

    @Override
    protected String getHelpTitle() {
        return "Incorrect Heal Formatting";
    }

    @Override
    protected String getHelpDescription() {
        return "Please use the commands below to heal a character \n" + RedBot.PREFIX +
                "heal \"PC Name(Optional)\", \"DT\", \"Improvements”\"";
    }
}
