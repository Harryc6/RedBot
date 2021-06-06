package com.nco;

import com.nco.commands.*;
import com.nco.utils.StringUtils;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;


public class MessageListener extends ListenerAdapter {
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
//        JSONObject jsonObject = null;
//        List<Message.Attachment> attachments = event.getMessage().getAttachments();
//        if (!attachments.isEmpty() && attachments.get(0).getFileExtension().equalsIgnoreCase("JSON")) {
//            CompletableFuture<File> future = attachments.get(0).downloadToFile();
//            try {
//                File file = future.get();
//                FileReader fileReader = new FileReader(file);
//                jsonObject = (JSONObject) new JSONParser().parse(fileReader);
//                file.delete();
//            } catch (InterruptedException | ExecutionException | IOException | ParseException e) {
//                e.printStackTrace();
//            }
//        }
//
//        assert jsonObject != null;
//        jsonObject.get("attribs");
        String messageEvent = event.getMessage().getContentRaw().split(" ")[0];
        if (!event.getAuthor().isBot() && messageEvent.startsWith(RedBot.PREFIX)) {
            String[] messageArgs = StringUtils.parseArgsString(event.getMessage().getContentRaw().substring(messageEvent.length()).trim());

            Commands eventType = StringUtils.getEnumFromString(Commands.class, messageEvent.substring(1));
            if (eventType == null) {
                eventType = Commands.UNKNOWN;
            }
            switch (eventType) {
                case ADDICTION:
                    break;
                case BANK:
                    new BankCommand(messageArgs, event);
                    break;
                case BUYARMOR:
                    new BuyArmorCommand(messageArgs, event);
                    break;
                case CHECK:
                     new CheckCommand(messageArgs, event);
                    break;
                case COVERAGE:
                    break;
                case FAME:
                    new FameCommand(messageArgs, event);
                    break;
                case FIXER:
                    break;
                case FIXERDEBT:
                    break;
                case HOSPITAL:
                    break;
                case HP:
                    new HPCommand(messageArgs, event);
                    break;
                case HUM:
                    new HumCommand(messageArgs, event);
                    break;
                case HUSTLE:
                    new HustleCommand(messageArgs, event);
                    break;
                case IMPROVE:
                    new ImproveCommand(messageArgs, event);
                    break;
                case INFO:
                    new InfoCommand(messageArgs, event);
                    break;
                case INSTALL:
                    new InstallCommand(messageArgs, event);
                    break;
                case LIFESTYLE:
                    break;
                case MAXHUM:
                    new MaxHumCommand(messageArgs, event);
                    break;
                case MEDTECH:
                    break;
                case MONTHLY:
                    break;
                case MOTO:
                    break;
                case NANOHP:
                    new NanoHPCommand(messageArgs, event);
                    break;
                case PROPERTY:
                    break;
                case RENT:
                    break;
                case SELECT:
                    new SelectCommand(messageArgs, event);
                    break;
                case TEAM:
                    break;
                case TECHIE:
                    break;
                case TIMEZONE:
                    new TimeZoneCommand(messageArgs, event);
                    break;
                case TRADE:
                    new TradeCommand(messageArgs, event);
                    break;
                case TRAUMADEBT:
                    break;
                case UPDATE:
                    new UpdateCommand(messageArgs, event);
                    break;
                case UNKNOWN:
                    event.getChannel().sendMessage(messageEvent + " Is An Unrecognised Command").queue();
                    break;
                case UPDATEBODYHP:
                    new UpdateBodyHpCommand(messageArgs, event);
                    break;
            }
        }
    }
}
