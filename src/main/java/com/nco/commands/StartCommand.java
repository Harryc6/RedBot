package com.nco.commands;


import com.nco.RedBot;
import com.nco.pojos.PlayerCharacter;
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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

            List<Attribute> skillList = attribs.stream().collect(Collectors.groupingBy(attribute -> attribute.getName().startsWith("LVL_"))).get(true);
            StringBuilder sb = new StringBuilder();
            for (Attribute skill:skillList) {
                String[] split = skill.getName().substring(4).split("(?<!^)(?=[A-Z])");
                for (int i = 0; i < split.length; i++) {
                    sb.append(split[i].toLowerCase());
                    sb.append(i != split.length - 1 ? "_" : ", ");
                }
            }
            logger.info(sb.delete(sb.length() - 2, sb.length()).toString());
        } else {
            builder.setTitle("ERROR: No Player Character JSON File Found");
            builder.setDescription("Please attach a characters JSON export.");
        }
    }

    private List<Attribute> getAttributeList(JSONObject jsonObject) {
        ArrayList<Attribute> attribs = new ArrayList<>();
        JSONArray array = (JSONArray) jsonObject.get("attribs");
        for (int i = 0; i < array.size(); i++) {
            JSONObject object = (JSONObject) array.get(i);
            if (object.size() == 4) {
                attribs.add(new Attribute(object.get("name"), object.get("current"), object.get("max"), object.get("id")));
                logger.info(attribs.get(attribs.size() - 1).toString());
            }
        }
        return attribs;
    }

    private JSONObject getJsonObject() {
        List<Message.Attachment> attachments = message.getAttachments();
        JSONObject jsonObject = null;
        if (!attachments.isEmpty() && attachments.get(0).getFileExtension().equalsIgnoreCase("JSON")) {
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
                "start \"PC Name\"";
    }
}
