package com.nco.commands;


import com.nco.RedBot;
import com.nco.pojos.PlayerCharacter;
import com.nco.utils.NCOUtils;
import com.nco.utils.NumberUtils;
import com.nco.utils.tables.Attribute;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
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

    public StartCommand(String[] messageArgs, GuildMessageReceivedEvent event) {
        super(messageArgs, event);
    }

    @Override
    protected boolean findPlayerCharacter() {
        return false;
    }

    @Override
    protected boolean canProcessWithoutPC() {
        return messageArgs.length != 0 && !message.getAttachments().isEmpty();
    }

    @Override
    protected void processUpdateAndRespond(Connection conn, PlayerCharacter pc, EmbedBuilder builder) throws SQLException {
        builder.setTitle("working");
        JSONObject jsonObject = getJsonObject();
        if (jsonObject != null) {
            List<Attribute> attribs = getAttributeList(jsonObject);
            Map<String, Attribute> map = new HashMap<>();
            for (Attribute attrib : attribs) {
                map.put(attrib.getName().toLowerCase(), attrib);
            }
            insertPc(conn, map);
            insertStats(conn, map);
            insertSkills(conn, attribs);
        } else {
            builder.setTitle("ERROR: No Player Character JSON File Found");
            builder.setDescription("Please attach a characters JSON export.");
        }
    }

    private boolean insertPc(Connection conn, Map<String, Attribute> map) throws SQLException {
        String sql = "INSERT INTO nco_pc (discord_name, character_name, role, creation_rank, rank, street_cred, bank, influence_points, vault, created_by) values (?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setString(1, messageArgs[0]);
            stat.setString(2, messageArgs[1]);
            stat.setString(3, messageArgs[2]);
            stat.setString(4, messageArgs[3]);
            stat.setString(5, messageArgs[3]);
            stat.setInt(6, NCOUtils.getStartingStreetCredFromRank(messageArgs[3]));
            stat.setInt(7, NumberUtils.ParseNumber(map.get("cash").getCurrent()));
            stat.setInt(8, NumberUtils.checkNullOrEmpty(map.get("ippoints").getCurrent()));
            stat.setString(9, messageArgs[4]);
            stat.setString(10, author.getAsTag());
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
        logger.info("SQL : " + sb);
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
        return "Please use the commands below create a characters \n" + RedBot.PREFIX +
                "start \"discordName\" \"PC Name\" \"Role\" \"Starting Rank(Optional)\" \"Vault\"";
    }
}
