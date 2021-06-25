package com.nco.commands;

import com.nco.RedBot;
import com.nco.pojos.PlayerCharacter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.sql.Connection;
import java.sql.SQLException;

public class StartRoleCommand extends AbstractCommand {

    public StartRoleCommand(String[] messageArgs, Object event, boolean isSlash) {
        super(messageArgs, event, isSlash);
    }

    @Override
    protected boolean canProcessByUser() {
        return super.canProcessByUser();
    }

    @Override
    protected boolean canProcessByName() {
        return super.canProcessByName();
    }

    @Override
    protected void processUpdateAndRespond(Connection conn, PlayerCharacter pc, EmbedBuilder builder) throws SQLException {

    }

    @Override
    protected String getHelpTitle() {
        return "Incorrect Start Role Formatting";
    }

    @Override
    protected String getHelpDescription() {
        return "Please use the commands below create a characters \n" + RedBot.PREFIX +
                "start\"PC Name(Optional)\" \"Timezone\" \"Starting Rank(Optional)\"";
        // medtech Surgery, Pharmaceuticals, Cryosystem Operation
        // tech Field Expertise, Upgrade Expertise, Fabrication Expertise, Invention Expertise
    }


}
