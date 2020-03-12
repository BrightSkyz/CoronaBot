package xyz.skyz.coronabot;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.List;

public class CommandListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        if (event.getAuthor().isFake()) return;
        if (event.getChannelType().isGuild()) {
            if (!event.getTextChannel().canTalk()) {
                return;
            }
        }
        Message message = event.getMessage();
        String content = message.getContentRaw();
        if (content.startsWith(">")) {
            String cmd = "";
            List<String> args = new ArrayList<>();
            if (content.contains(" ")) {
                boolean first = true;
                for (String arg : content.split(" ")) {
                    if (first) {
                        cmd = arg.replace(">", "");
                        first = false;
                    } else {
                        args.add(arg);
                    }
                }
            } else {
                cmd = content.replace(">", "");
            }

            if (Bot.getBot().getCommandMap().containsKey(cmd)) {
                Command command = Bot.getBot().getCommandMap().get(cmd);
                command.execute(event, cmd, args);
            }
        }
    }
}
