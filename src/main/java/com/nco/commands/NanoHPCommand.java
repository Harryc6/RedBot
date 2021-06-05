package com.nco.commands;

import com.nco.RedBot;
import com.nco.pojos.PlayerCharacter;
import com.nco.utils.DBUtils;
import com.nco.utils.NumberUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class NanoHPCommand extends AbstractCommand {

    int dtAmount;

    public NanoHPCommand(String[] messageArgs, User author, MessageChannel channel, Member member) {
        super(messageArgs, author, channel, member);
    }

    @Override
    protected boolean canProcessByUser() {
        return messageArgs.length >= 2 && !DBUtils.doesCharacterExist(messageArgs[0]) && NumberUtils.isNumeric(messageArgs[0]);
    }

    @Override
    protected boolean canProcessByName() {
        return messageArgs.length >= 3 && DBUtils.doesCharacterExist(messageArgs[0]) && NumberUtils.isNumeric(messageArgs[1]);
    }

    @Override
    protected void processUpdateAndRespond(Connection conn, PlayerCharacter pc, EmbedBuilder builder) throws SQLException {
        if (Integer.parseInt(messageArgs[1]) > pc.getDowntime()) {
            builder.setTitle("ERROR: Not Enough DT");
            builder.addField("You have:", String.valueOf(pc.getDowntime()), true);
        } else if(pc.getHeadSP() == Integer.parseInt(messageArgs[2]) && pc.getBodySP() == Integer.parseInt(messageArgs[2])) {
            builder.setTitle("ERROR: You are at full HeadSP and BodySP");
            builder.addField("Your current HeadSP:", String.valueOf(pc.getHeadSP()), true);
            builder.addBlankField(true);
            builder.addField("Your max HeadSP:", messageArgs[2], true);
            builder.addField("Your current BodySP:", String.valueOf(pc.getBodySP()), true);
            builder.addBlankField(true);
            builder.addField("Your max BodySP:", messageArgs[2], true);
        } else {
            int newDT = pc.getDowntime() - NumberUtils.asPositive(messageArgs[1]);
            dtAmount = NumberUtils.asPositive(messageArgs[1]);
            int[] hpAndArmor = getNewHPAndArmor(pc);
            newDT += dtAmount;

            logger.info("PC : " + messageArgs[0] + "\nCurrent HP : " + pc.getCurrentHP() +
                    "\nBody : " + pc.getBodyScore() + "\nBonus : " + getBonuses(pc) +
                    "\nArmorBonus : " + getArmorBonuses(pc) + "\n Current Head Armor : " + pc.getBodySP() +
                    "\n Current Body Armor : " + pc.getHeadSP() +
                    "\n New HeadSP : " + hpAndArmor[1] + " New BodySP : " + hpAndArmor[2] + " New HP : " + hpAndArmor[0] );

            if (updateNanoHP(conn, hpAndArmor, newDT) && insertNanoHP(conn, pc, hpAndArmor, newDT)) {
                buildEmbed(builder, pc, newDT, hpAndArmor);
            } else {
                builder.setTitle("ERROR: Install Update Or Insert Failure");
                builder.setDescription("Please contact an administrator to get this resolved");
            }
        }
    }

    public int[] getNewHPAndArmor(PlayerCharacter pc) {
        int newHP = pc.getCurrentHP();
        int newHeadSP = pc.getHeadSP();
        int newBodySP = pc.getBodySP();
        while (dtAmount > 0 && (newBodySP < Integer.parseInt(messageArgs[2]) || newHeadSP < Integer.parseInt(messageArgs[2])
                || newHP < pc.getMaxHP())) {
            newHP += pc.getBodyScore() + getBonuses(pc);
            if (messageArgs[3].toLowerCase().contains("speedheal")) {
                newHP += pc.getBodyScore() + pc.getWillScore();
            }
            newBodySP += 1 + getArmorBonuses(pc);
            newHeadSP += 1 + getArmorBonuses(pc);
            dtAmount--;
        }
        if (messageArgs[3].toLowerCase().contains("cryotank")) {
            newHP *= 2;
        }
        if (newHP > pc.getMaxHP()) {
            newHP = pc.getMaxHP();
        }
        if (newHeadSP > Integer.parseInt(messageArgs[2])) {
            newHeadSP = Integer.parseInt(messageArgs[2]);
        }
        if (newBodySP > Integer.parseInt(messageArgs[2])) {
            newBodySP = Integer.parseInt(messageArgs[2]);
        }
        return new int[]{newHP, newHeadSP, newBodySP};
    }

    private int getBonuses(PlayerCharacter pc) {
        int bonuses = 0;
        if (messageArgs.length > 3) {
            if (messageArgs[3].toLowerCase().contains("enhanced")) {
                bonuses += pc.getBodyScore();
            }
            if (messageArgs[3].toLowerCase().contains("antibodies")) {
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

    private int getArmorBonuses(PlayerCharacter pc) {
        int armorBonuses = 0;

        if (pc.getLifestyle().equalsIgnoreCase("Generic Prepak")) {
            armorBonuses += 1;
        }
        if (pc.getLifestyle().equalsIgnoreCase("Good Prepak")) {
            armorBonuses += 2;
        }
        if (pc.getLifestyle().equalsIgnoreCase("Fresh Food")) {
            armorBonuses += 3;
        }
        return armorBonuses;
    }

    private boolean updateNanoHP(Connection conn, int[] hpAndArmor, int newDT) throws SQLException {
        String sql = "UPDATE NCO_PC set current_hp = ?, head_sp = ?, body_sp = ?, downtime = ? Where character_name = ?";
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setInt(1, hpAndArmor[0]);
            stat.setInt(2, hpAndArmor[1]);
            stat.setInt(3, hpAndArmor[2]);
            stat.setInt(4, newDT);
            stat.setString(5, messageArgs[0]);
            return stat.executeUpdate() == 1;
        }
    }

    private boolean insertNanoHP(Connection conn, PlayerCharacter pc, int[] hpAndArmor, int newDT) throws SQLException {
        String sql = "insert into nco_nano_hp(character_name, old_hp, new_hp, old_head_sp, new_head_sp," +
                "old_body_sp, new_body_sp, old_dt, new_dt, bonuses_used, hp_multiplier, armor_multiplier, created_by) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setString(1, messageArgs[0]);
            stat.setInt(2, pc.getCurrentHP());
            stat.setInt(3, hpAndArmor[0]);
            stat.setInt(4, pc.getHeadSP());
            stat.setInt(5, hpAndArmor[1]);
            stat.setInt(6, pc.getBodySP());
            stat.setInt(7, hpAndArmor[2]);
            stat.setInt(8, pc.getDowntime());
            stat.setInt(9, newDT);
            stat.setString(10, (messageArgs.length > 3 ? messageArgs[3] : ""));
            stat.setInt(11, getBonuses(pc));
            stat.setInt(12, getArmorBonuses(pc));
            stat.setString(13, author.getAsTag());
            return stat.executeUpdate() == 1;
        }
    }

    private void buildEmbed(EmbedBuilder builder, PlayerCharacter pc, int newDT, int[] hpAndArmor) {
        builder.setTitle(messageArgs[0] + "'s HP and/or Armor Restored");
        builder.setDescription("Used " + (NumberUtils.asPositive(messageArgs[1]) - dtAmount) + " DT ");
        builder.addField("Old HP", String.valueOf(pc.getCurrentHP()), true);
        builder.addBlankField(true);
        builder.addField("New HP", String.valueOf(hpAndArmor[0]), true);
        builder.addField("Old Head SP", String.valueOf(pc.getHeadSP()), true);
        builder.addBlankField(true);
        builder.addField("New Head SP", String.valueOf(hpAndArmor[1]), true);
        builder.addField("Old Body SP", String.valueOf(pc.getBodySP()), true);
        builder.addBlankField(true);
        builder.addField("New Body SP", String.valueOf(hpAndArmor[2]), true);
        builder.addField("Old Downtime", String.valueOf(pc.getDowntime()), true);
        builder.addBlankField(true);
        builder.addField("New Downtime", String.valueOf(newDT), true);
    }

    @Override
    protected String getHelpTitle() {
        return "Incorrect nanoHP Formatting";
    }

    @Override
    protected String getHelpDescription() {
        return "Please use the commands below to heal and restore SP when using Subdermal Armour or Skin Weave \n" +
                RedBot.PREFIX + "nanohp \"PC Name(Optional)\" “DT” ”Max Head & Body SP” “Improvements(Optional)”\n";
    }
}
