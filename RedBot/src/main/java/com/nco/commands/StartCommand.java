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
    protected String getRoleRequiredForCommand() {
        return "Tech-Support";
    }

    @Override
    protected boolean findPlayerCharacter() {
        return false;
    }

    @Override
    protected boolean canProcessWithoutPC() {
        return !message.getAttachments().isEmpty() && !DBUtils.doesCharacterExist(messageArgs[1].toLowerCase()) &&
                NCOUtils.validRank(messageArgs[3]) && NCOUtils.validRole(messageArgs[4])
                && NumberUtils.isNumeric(messageArgs[5]) && (isCorrectRole() || validMedtech() || validTech());
    }

    private boolean isCorrectRole() {
        return messageArgs.length == 6 && !messageArgs[4].equalsIgnoreCase("Medtech") &&
                !messageArgs[4].equalsIgnoreCase("Tech");
    }

    private boolean validMedtech() {
        return messageArgs.length == 9 && NumberUtils.isNumeric(messageArgs[6]) && NumberUtils.isNumeric(messageArgs[7])
                && NumberUtils.isNumeric(messageArgs[8]) && medtechAddsUpToRoleRank()
                && messageArgs[4].equalsIgnoreCase("Medtech");
    }

    private boolean medtechAddsUpToRoleRank() {
        return Integer.parseInt(messageArgs[5]) == (Integer.parseInt(messageArgs[6]) + Integer.parseInt(messageArgs[7])
                + Integer.parseInt(messageArgs[8]));
    }

    private boolean validTech() {
        return messageArgs.length == 10 && NumberUtils.isNumeric(messageArgs[6]) && NumberUtils.isNumeric(messageArgs[7])
                && NumberUtils.isNumeric(messageArgs[8]) && NumberUtils.isNumeric(messageArgs[8]) && techAddsUpToRoleRank()
                && messageArgs[4].equalsIgnoreCase("tech");
    }

    private boolean techAddsUpToRoleRank() {
        return (Integer.parseInt(messageArgs[5]) * 2) == (Integer.parseInt(messageArgs[6]) + Integer.parseInt(messageArgs[7])
                + Integer.parseInt(messageArgs[8]));
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
                pc = new PlayerCharacter(conn, messageArgs[1].toLowerCase(), true);
                builder.setTitle(pc.getCharacterDisplayName() +" Created");
                Member pcMember = message.getGuild().getMemberByTag(pc.getDiscordName());
                builder.setDescription(pc.getCharacterDisplayName() + " is linked to " +
                        (pcMember != null ? pcMember.getAsMention() : pc.getDiscordName()));
            }
        } else {
            builder.setTitle("ERROR: No Player Character JSON File Found");
            builder.setDescription("Please attach a characters JSON export.");
        }
    }

    private boolean insertPc(Connection conn, Map<String, Attribute> map) throws SQLException {
        String StartingRank = NCOUtils.getCorrectRankCase(messageArgs[3]);
        int reputation = NumberUtils.ParseNumber(checkNull(map.get("reputation")).getCurrent());
        String sql = getPcSql();
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setString(1, messageArgs[0]);
            stat.setString(2, messageArgs[1].toLowerCase());
            stat.setString(3, messageArgs[4]);
            stat.setString(4, StartingRank);
            stat.setString(5, StartingRank);
            stat.setInt(6, NCOUtils.getStartingStreetCredFromRank(StartingRank));
            stat.setInt(7, NumberUtils.ParseNumber(checkNull(map.get("cash")).getCurrent()));
            stat.setInt(8, NumberUtils.checkNullOrEmpty(checkNull(map.get("headsp")).getCurrent()));
            stat.setInt(9, NumberUtils.checkNullOrEmpty(checkNull(map.get("bodysp")).getCurrent()));
            stat.setInt(10, NumberUtils.ParseNumber(checkNull(map.get("ippoints")).getCurrent()));
            stat.setInt(11, reputation);
            stat.setInt(12, NCOUtils.getFameFromReputation(reputation));
            stat.setString(13, messageArgs[2]);
            stat.setString(14, author.getAsTag());
            if (validMedtech()) {
                stat.setInt(15, Integer.parseInt(messageArgs[6]));
                stat.setInt(16, Integer.parseInt(messageArgs[7]));
                stat.setInt(17, Integer.parseInt(messageArgs[8]));
            } else if (validTech()) {
                stat.setInt(15, Integer.parseInt(messageArgs[6]));
                stat.setInt(16, Integer.parseInt(messageArgs[7]));
                stat.setInt(17, Integer.parseInt(messageArgs[8]));
                stat.setInt(18, Integer.parseInt(messageArgs[9]));
            }
            return stat.executeUpdate() == 1;
        }
    }

    private Attribute checkNull(Attribute attr) {
        if (attr == null) {
            return new Attribute("", "", "", "");
        }
        return attr;
    }

    private String getPcSql() {
        StringBuilder sb = new StringBuilder("INSERT INTO nco_pc (discord_name, character_name, role, creation_rank, rank, street_cred, bank, head_sp, body_sp, influence_points, reputation, fame, vault, created_by");
        if (validMedtech()) {
            sb.append(", surgery, pharmaceuticals, cryosystem_operation)");
        } else if (validTech()) {
            sb.append(", field_expertise, upgrade_expertise, fabrication_expertise, invention_expertise)");
        } else {
            sb.append(")");
        }
        sb.append(" values (?,?,?,?,?,?,?,?,?,?,?,?,?,?");
        if (validMedtech()) {
            sb.append(",?,?,?)");
        } else if (validTech()) {
            sb.append(",?,?,?,?)");
        } else {
            sb.append(")");
        }
        return sb.toString();
//        return "INSERT INTO nco_pc (discord_name, character_name, role, creation_rank, rank, street_cred, bank, head_sp, body_sp, influence_points, reputation, fame, vault, created_by) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    }

    private boolean insertStats(Connection conn, Map<String, Attribute> map) throws SQLException {
        String sql = "INSERT INTO nco_pc_stats (character_name, intelligence, reflexes, dexterity, technique, cool, " +
                "willpower, luck, usable_luck, movement, body, current_empathy, max_empathy, current_hp, max_hp, " +
                "current_humanity, max_humanity, created_by) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setString(1, messageArgs[1].toLowerCase());
            stat.setInt(2, Integer.parseInt(checkNull(map.get("intelligence")).getCurrent()));
            stat.setInt(3, Integer.parseInt(checkNull(map.get("reflex")).getCurrent()));
            stat.setInt(4, Integer.parseInt(checkNull(map.get("dexterity")).getCurrent()));
            stat.setInt(5, Integer.parseInt(checkNull(map.get("technique")).getCurrent()));
            stat.setInt(6, Integer.parseInt(checkNull(map.get("cool")).getCurrent()));
            stat.setInt(7, Integer.parseInt(checkNull(map.get("willpower")).getCurrent()));
            stat.setInt(8, Integer.parseInt(checkNull(map.get("luck")).getMax()));
            stat.setInt(9, Integer.parseInt(checkNull(map.get("luck")).getMax()));
            stat.setInt(10, Integer.parseInt(checkNull(map.get("movement")).getCurrent()));
            stat.setInt(11, Integer.parseInt(checkNull(map.get("body")).getCurrent()));
            stat.setInt(12, Integer.parseInt(checkNull(map.get("empathy")).getCurrent()));
            stat.setInt(13, Integer.parseInt(checkNull(map.get("empathy")).getMax()));
            stat.setInt(14, Integer.parseInt(checkNull(map.get("hp")).getCurrent()));
            stat.setInt(15, Integer.parseInt(checkNull(map.get("hp")).getMax()));
            stat.setInt(16, Integer.parseInt(checkNull(map.get("humanity")).getCurrent()));
            stat.setInt(17, Integer.parseInt(checkNull(map.get("humanity")).getMax()));
            stat.setString(18, author.getAsTag());
            return stat.executeUpdate() == 1;
        }
    }

    private boolean insertSkills(Connection conn, List<Attribute> attribs) throws SQLException {
        List<Attribute> skillList = attribs.stream().collect(Collectors.groupingBy(attribute -> attribute.getName().startsWith("LVL_"))).get(true);
        skillList.sort(Comparator.comparing(Attribute::getName));
        try (PreparedStatement stat = conn.prepareStatement(getInsertSkillsSQL(skillList))) {
            stat.setString(1, messageArgs[1].toLowerCase());
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
                "start \"discordName\" \"PC Name\" \"Vault\" \"Starting Rank\" \"Role\" \"Role Rank\"\n" +
                "If Medtech add on \"Surgery\" \"Pharmaceuticals\" \"Cryosystem Operation\"\n" +
                "If Tech add on \"Field Expertise\" \"Upgrade Expertise\" \"Fabrication Expertise\" \"Invention Expertise\"";
    }

    private String getHelpReason() {
        if (message.getAttachments().isEmpty()) {
            return "No attachments found.\n\n";
        } else if (DBUtils.doesCharacterExist(messageArgs[1].toLowerCase())) {
            return "Character name is already in use.\n\n";
        } else if (!NCOUtils.validRank(messageArgs[3])) {
            return "Not a recognised rank.\n\n";
        } else if (!NCOUtils.validRole(messageArgs[4])) {
            return "Not a recognised role.\n\n";
        } else if (!validMedtech()) {
            return "Medtech information incorrect.\n\n";
        } else if (!validTech()) {
            return "Tech information incorrect.\n\n";
        } else {
            return "";
        }
    }
}
