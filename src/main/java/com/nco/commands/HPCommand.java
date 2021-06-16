package com.nco.commands;

import com.nco.RedBot;
import com.nco.pojos.PlayerCharacter;
import com.nco.utils.DBUtils;
import com.nco.utils.NumberUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class HPCommand extends AbstractCommand {

    int dtAmount;

    public HPCommand(String[] messageArgs, GuildMessageReceivedEvent event) {
        super(messageArgs, event);
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
            int newDT = pc.getDowntime() - NumberUtils.asPositive(messageArgs[1]);
            dtAmount = NumberUtils.asPositive(messageArgs[1]);
            int newHP = getNewHP(pc);
            newDT += dtAmount;
            logger.info("PC : " + messageArgs[0] + "\nCurrent HP : " + pc.getCurrentHp() +
                    "\nBody : " + pc.getBody() + "\nBonus : " + getBonuses(pc) +
                    "\n DT Used : " + dtAmount + "\nCombines to new HP of " + newHP);
            if (updateHP(conn, newHP, newDT) && insertHP(conn, pc, newHP, newDT)) {
                buildEmbed(builder, pc, newHP, newDT);
            } else {
                builder.setTitle("ERROR: Install Update Or Insert Failure");
                builder.setDescription("Please contact an administrator to get this resolved");
            }
        }
    }

    public int getNewHP(PlayerCharacter pc) {
        int newHP = pc.getCurrentHp();
        int multiplier = messageArgs[2].toLowerCase().contains("cryotank") ? 2 : 1;
        while (dtAmount > 0 && newHP < pc.getMaxHp()) {
            newHP += (pc.getBody() + getBonuses(pc)) * multiplier;
            if (messageArgs[2].toLowerCase().contains("speedheal")) {
                newHP += pc.getBody() + pc.getWillpower();
            }
            dtAmount--;
        }
        if (newHP > pc.getMaxHp()) {
            newHP = pc.getMaxHp();
        }
        return newHP;
    }

    private int getBonuses(PlayerCharacter pc) {
        int bonuses = 0;
        if (messageArgs.length > 3) {
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

    private boolean updateHP(Connection conn, int newHP, int newDT) throws SQLException {
            String sql = "UPDATE NCO_PC set current_hp = ?, downtime = ? Where character_name = ?";
         try (PreparedStatement stat = conn.prepareStatement(sql)) {
                   stat.setInt(1, newHP);
             stat.setInt(2, newDT);
             stat.setString(3, messageArgs[0]);
             return stat.executeUpdate() == 1;
          }
      }

       private boolean insertHP(Connection conn, PlayerCharacter pc, int newHP, int newDT) throws SQLException {
          String sql = "insert into nco_hp(character_name, old_hp, new_hp, old_dt, new_dt, bonuses_used, multiplier, created_by) " +
                  "values (?, ?, ?, ?, ?, ?, ?, ?)";
         try (PreparedStatement stat = conn.prepareStatement(sql)) {
             stat.setString(1, messageArgs[0]);
             stat.setInt(2, pc.getCurrentHp());
             stat.setInt(3, newHP);
             stat.setInt(4, pc.getDowntime());
             stat.setInt(5, newDT);
             stat.setString(6, (messageArgs.length > 2 ? messageArgs[2] : ""));
             stat.setInt(7, getBonuses(pc));
             stat.setString(8, author.getAsTag());
             return stat.executeUpdate() == 1;
        }
       }

    private void buildEmbed(EmbedBuilder builder, PlayerCharacter pc, int newHP, int newDT) {
        builder.setTitle(messageArgs[0] + "'s HP Restored");
        builder.setDescription("Used " + (NumberUtils.asPositive(messageArgs[1]) - dtAmount) + " DT ");
        builder.addField("Old HP", String.valueOf(pc.getCurrentHp()), true);
        builder.addBlankField(true);
        builder.addField("New HP", String.valueOf(newHP), true);
        builder.addField("Old Downtime", String.valueOf(pc.getDowntime()), true);
        builder.addBlankField(true);
        builder.addField("New Downtime", String.valueOf(newDT), true);
    }

    @Override
    protected String getHelpTitle() {
        return "Incorrect HP Formatting";
    }

    @Override
    protected String getHelpDescription() {
        return "Please use the commands below to add MaxHumanity onto a characters \n" + RedBot.PREFIX +
                "hp \"PC Name(Optional)\", “DT”, “improvements”\n";
    }
}
