package com.nco.commands;

import com.nco.RedBot;
import com.nco.pojos.PlayerCharacter;
import com.nco.utils.DBUtils;
import com.nco.utils.NumberUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class NanoHealCommand extends AbstractCommand {

    int dtAmount;

    public NanoHealCommand(String[] messageArgs, Object event, boolean isSlash) {
        super(messageArgs, event, isSlash);
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
        } else if(pc.getHeadSp() == Integer.parseInt(messageArgs[2]) && pc.getBodySp() == Integer.parseInt(messageArgs[2])) {
            builder.setTitle("ERROR: You are at full HeadSP and BodySP");
            builder.addField("Your current HeadSP:", String.valueOf(pc.getHeadSp()), true);
            builder.addBlankField(true);
            builder.addField("Your max HeadSP:", messageArgs[2], true);
            builder.addField("Your current BodySP:", String.valueOf(pc.getBodySp()), true);
            builder.addBlankField(true);
            builder.addField("Your max BodySP:", messageArgs[2], true);
        } else {
            int newDT = pc.getDowntime() - NumberUtils.asPositive(messageArgs[1]);
            dtAmount = NumberUtils.asPositive(messageArgs[1]);
            int[] hpAndArmor = getNewHPAndArmor(pc);
            newDT += dtAmount;

//            logger.info("PC : " + messageArgs[0] + "\nCurrent HP : " + pc.getCurrentHp() +
//                    "\nBody : " + pc.getBody() + "\nBonus : " + getBonuses(pc) +
//                    "\nArmorBonus : " + getArmorBonuses(pc) + "\n Current Head Armor : " + pc.getBodySp() +
//                    "\n Current Body Armor : " + pc.getHeadSp() +
//                    "\n New HeadSP : " + hpAndArmor[1] + " New BodySP : " + hpAndArmor[2] + " New HP : " + hpAndArmor[0] );

//            if (updateNanoHP(conn, hpAndArmor, newDT) && insertNanoHP(conn, pc, hpAndArmor, newDT)) {
            if (updateNanoHP(conn, hpAndArmor, newDT, pc)) {
                buildEmbed(builder, pc, newDT, hpAndArmor);
            } else {
                builder.setTitle("ERROR: Install Update Or Insert Failure");
                builder.setDescription("Please contact an administrator to get this resolved");
            }
        }
    }

    public int[] getNewHPAndArmor(PlayerCharacter pc) {
        int newHP = pc.getCurrentHp();
        int newHeadSP = pc.getHeadSp();
        int newBodySP = pc.getBodySp();
        int multiplier = messageArgs.length == 4 && messageArgs[3].toLowerCase().contains("cryotank") ? 2 : 1;
        while (dtAmount > 0 && (newBodySP < Integer.parseInt(messageArgs[2]) || newHeadSP < Integer.parseInt(messageArgs[2])
                || newHP < pc.getMaxHp())) {
            newHP += (pc.getBody() + getBonuses(pc)) * multiplier;
            if (messageArgs.length == 4 && messageArgs[2].toLowerCase().contains("speedheal")) {
                newHP += pc.getBody() + pc.getWillpower();
            }
            newBodySP += 1 + getArmorBonuses(pc);
            newHeadSP += 1 + getArmorBonuses(pc);
            dtAmount--;
        }
        if (newHP > pc.getMaxHp()) {
            newHP = pc.getMaxHp();
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
        if (messageArgs.length == 4) {
            if (messageArgs[3].toLowerCase().contains("enhanced")) {
                bonuses += pc.getBody();
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

    private boolean updateNanoHP(Connection conn, int[] hpAndArmor, int newDT, PlayerCharacter pc) throws SQLException {
        return updatePc(conn, hpAndArmor, newDT, pc) && updateStats(conn, hpAndArmor);
    }

    private boolean updatePc(Connection conn, int[] hpAndArmor, int newDT, PlayerCharacter pc) throws SQLException {
        String sql = "UPDATE NCO_PC set head_sp = ?, body_sp = ?, downtime = ? Where character_name = ?";
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setInt(1, hpAndArmor[1]);
            stat.setInt(2, hpAndArmor[2]);
            stat.setInt(3, (newDT * 12) + pc.getDowntimeRemainder());
            stat.setString(4, messageArgs[0]);
            return stat.executeUpdate() == 1;
        }
    }


    private boolean updateStats(Connection conn, int[] hpAndArmor) throws SQLException {
        String sql = "UPDATE nco_pc set current_hp = ? Where character_name = ?";
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setInt(1, hpAndArmor[0]);
            stat.setString(2, messageArgs[0]);
            return stat.executeUpdate() == 1;
        }
    }

    private void buildEmbed(EmbedBuilder builder, PlayerCharacter pc, int newDT, int[] hpAndArmor) {
        builder.setTitle(pc.getCharacterDisplayName() + "'s HP and Armor Restored");
        builder.setDescription("Used " + (NumberUtils.asPositive(messageArgs[1]) - dtAmount) + " DT ");
        builder.addField("Old HP", String.valueOf(pc.getCurrentHp()), true);
        builder.addBlankField(true);
        builder.addField("New HP", String.valueOf(hpAndArmor[0]), true);
        builder.addField("Old SP H | B", pc.getHeadSp() + " | " + pc.getBodySp(), true);
        builder.addBlankField(true);
        builder.addField("New SP H | B", hpAndArmor[1] + " | " + hpAndArmor[2], true);
        builder.addField("Old Downtime", String.valueOf(pc.getDowntime()), true);
        builder.addBlankField(true);
        builder.addField("New Downtime", String.valueOf(newDT), true);
    }

    @Override
    protected String getHelpTitle() {
        return "Incorrect NanoHeal Formatting";
    }

    @Override
    protected String getHelpDescription() {
        return "Please use the commands below to heal and restore SP when using Subdermal Armour or Skin Weave \n" +
                RedBot.PREFIX + "nanoheal \"PC Name(Optional)\" \"DT\" \"Max Head & Body SP\" \"Improvements(Optional)\"";
    }
}
