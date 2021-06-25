package com.nco.commands;


import com.nco.RedBot;
import com.nco.pojos.PlayerCharacter;
import com.nco.utils.DBUtils;
import com.nco.utils.NCOUtils;
import com.nco.utils.NumberUtils;
import com.nco.utils.tables.Attribute;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class StartCommand extends AbstractCommand {

    public StartCommand(String[] messageArgs, Object event, boolean isSlash) {
        super(messageArgs, event, isSlash);
    }

    @Override
    protected boolean findPlayerCharacter() {
        return false;
    }

    @Override
    protected boolean canProcessWithoutPC() {
        return !message.getAttachments().isEmpty() && !DBUtils.doesCharacterExist(messageArgs[1]) &&
                (messageArgs.length == 4 || (messageArgs.length == 5 && NCOUtils.validRank(messageArgs[4]))) &&
                NCOUtils.validUTC(messageArgs[2]);
    }

    @Override
    protected void processUpdateAndRespond(Connection conn, PlayerCharacter pc, EmbedBuilder builder) throws SQLException {
        JSONObject jsonObject = getJsonObject();
        if (jsonObject != null) {
            List<Attribute> attribs = getAttributeList(jsonObject);
            Map<String, Attribute> map = new HashMap<>();
            for (Attribute attrib : attribs) {
                map.put(attrib.getName().toLowerCase(), attrib);
            }
            if (insertPc(conn, map) && insertStats(conn, map) && insertSkills(conn, attribs)) {
                pc = new PlayerCharacter(conn, messageArgs[1], true);
                builder.setTitle(pc.getCharacterName() +" Created");
                Member pcMember = message.getGuild().getMemberByTag(pc.getDiscordName());
                builder.setDescription(pc.getCharacterName() + " is linked to " +
                        (pcMember != null ? pcMember.getAsMention() : pc.getDiscordName()));
            }
        } else {
            builder.setTitle("ERROR: No Player Character JSON File Found");
            builder.setDescription("Please attach a characters JSON export.");
        }
    }

    private boolean insertPc(Connection conn, Map<String, Attribute> map) throws SQLException {
        String StartingRank = (messageArgs.length == 5) ? NCOUtils.getCorrectRankCase(messageArgs[4]) : "E-Sheep";
        String sql = "INSERT INTO nco_pc (discord_name, character_name, role, creation_rank, rank, street_cred, bank, head_sp, body_sp, influence_points, vault, time_zone, created_by) values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setString(1, messageArgs[0]);
            stat.setString(2, messageArgs[1]);
            stat.setString(3, NCOUtils.parseAndFormatRole(map.get("role").getCurrent()));
            stat.setString(4, StartingRank);
            stat.setString(5, StartingRank);
            stat.setInt(6, NCOUtils.getStartingStreetCredFromRank(StartingRank));
            stat.setInt(7, NumberUtils.ParseNumber(map.get("cash").getCurrent()));
            stat.setInt(8, NumberUtils.checkNullOrEmpty(map.get("headsp").getCurrent()));
            stat.setInt(9, NumberUtils.checkNullOrEmpty(map.get("bodysp").getCurrent()));
            stat.setInt(10, NumberUtils.ParseNumber(map.get("ippoints").getCurrent()));
            stat.setString(11, messageArgs[3]);
            stat.setString(12, messageArgs[2]);
            stat.setString(13, author.getAsTag());
            return stat.executeUpdate() == 1;
        }
    }

    private boolean insertStats(Connection conn, Map<String, Attribute> map) throws SQLException {
        String sql = "INSERT INTO nco_pc_stats (character_name, intelligence, reflexes, dexterity, technique, cool, " +
                "willpower, luck, usable_luck, movement, body, current_empathy, max_empathy, current_hp, max_hp, " +
                "current_humanity, max_humanity, created_by) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setString(1, messageArgs[1]);
            stat.setInt(2, Integer.parseInt(map.get("intelligence").getCurrent()));
            stat.setInt(3, Integer.parseInt(map.get("reflex").getCurrent()));
            stat.setInt(4, Integer.parseInt(map.get("dexterity").getCurrent()));
            stat.setInt(5, Integer.parseInt(map.get("technique").getCurrent()));
            stat.setInt(6, Integer.parseInt(map.get("cool").getCurrent()));
            stat.setInt(7, Integer.parseInt(map.get("willpower").getCurrent()));
            stat.setInt(8, Integer.parseInt(map.get("luck").getMax()));
            stat.setInt(9, Integer.parseInt(map.get("luck").getMax()));
            stat.setInt(10, Integer.parseInt(map.get("movement").getCurrent()));
            stat.setInt(11, Integer.parseInt(map.get("body").getCurrent()));
            stat.setInt(12, Integer.parseInt(map.get("empathy").getCurrent()));
            stat.setInt(13, Integer.parseInt(map.get("empathy").getMax()));
            stat.setInt(14, Integer.parseInt(map.get("hp").getCurrent()));
            stat.setInt(15, Integer.parseInt(map.get("hp").getMax()));
            stat.setInt(16, Integer.parseInt(map.get("humanity").getCurrent()));
            stat.setInt(17, Integer.parseInt(map.get("humanity").getMax()));
            stat.setString(18, author.getAsTag());
            return stat.executeUpdate() == 1;
        }
    }

    private boolean insertSkills(Connection conn, List<Attribute> attribs) throws SQLException {
        List<Attribute> skillList = attribs.stream().collect(Collectors.groupingBy(attribute -> attribute.getName().startsWith("LVL_"))).get(true);
        skillList.sort(Comparator.comparing(Attribute::getName));
        try (PreparedStatement stat = conn.prepareStatement(getInsertSkillsSQL(skillList))) {
            stat.setString(1, messageArgs[1]);
            for (int i = 0; i < skillList.size(); i++) {
                stat.setInt(i + 2, NumberUtils.checkNullOrEmpty(skillList.get(i).getCurrent()));
            }
            stat.setString(skillList.size() + 2, author.getAsTag());
            return stat.executeUpdate() == 1;
        }
    }

    private String getInsertSkillsSQL(List<Attribute> skillList) {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO nco_pc_skills (character_name, ");
        for (Attribute skill: skillList) {
            String[] split = skill.getName().substring(4).split("(?<!^)(?=[A-Z])");
            for (int i = 0; i < split.length; i++) {
                sb.append(split[i].toLowerCase());
                sb.append(i != split.length - 1 ? "_" : ", ");
            }
        }
        sb.append("created_by) values (?,");
        for (Attribute ignored : skillList) {
            sb.append("?,");
        }
        sb.append("?)");
        return sb.toString();
    }

    private List<Attribute> getAttributeList(JSONObject jsonObject) {
        ArrayList<Attribute> attribs = new ArrayList<>();
        JSONArray array = (JSONArray) ((JSONObject) jsonObject.get("character")).get("attribs");
        for (Object o : array) {
            JSONObject object = (JSONObject) o;
            if (object.size() == 4) {
                attribs.add(new Attribute(object.get("name"), object.get("current"), object.get("max"), object.get("id")));
            }
        }
        return attribs;
    }

    private JSONObject getJsonObject() {
        List<Message.Attachment> attachments = message.getAttachments();
        JSONObject jsonObject = null;
        if (!attachments.isEmpty() && Objects.requireNonNull(attachments.get(0).getFileExtension()).equalsIgnoreCase("JSON")) {
            CompletableFuture<File> future = attachments.get(0).downloadToFile();
            try {
                File file = future.get();
                try (FileReader fileReader = new FileReader(file)) {
                    jsonObject = (JSONObject) new JSONParser().parse(fileReader);
                }
                logger.info("File - " + file.getName() + ( file.delete() ? " successfully deleted" : " failed to delete"));
            } catch (InterruptedException | ExecutionException | IOException | ParseException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }

    @Override
    protected String getHelpTitle() {
        return "Incorrect Start Formatting";
    }

    @Override
    protected String getHelpDescription() {
        return getHelpReason() + "Please use the commands below create a characters \n" + RedBot.PREFIX +
                "start \"discordName\" \"PC Name\" \"Vault\" \"Timezone\" \"Starting Rank(Optional)\"";
    }

    private String getHelpReason() {
        if (message.getAttachments().isEmpty()) {
            return "No attachments found.\n\n";
        } else if (DBUtils.doesCharacterExist(messageArgs[1])) {
            return "Character name is already in use.\n\n";
        } else if (messageArgs.length == 5 && !NCOUtils.validRank(messageArgs[4])) {
            return "Not a recognised rank.\n\n";
        } else if (!NCOUtils.validUTC(messageArgs[2])) {
            return "Not a recognised timezone.\n\n";
        } else {
            return "";
        }
    }
}
