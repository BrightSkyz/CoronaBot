package xyz.skyz.coronabot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.skyz.coronabot.Bot;
import xyz.skyz.coronabot.Command;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HelpCommand extends Command {

    public HelpCommand(Bot bot) {
        super(bot, "help", "help", "Gets the help menu.", Arrays.asList("cmds"), true);
    }

    @Override
    public void execute(MessageReceivedEvent event, String aliasUsed, List<String> args) {
        EmbedBuilder embedBuilder = getBot().createEmbedBuilder("Help", null, "Created by BrightSkyz (BrightSkyz#2627)");
        Map<String, Command> commands = new HashMap<>();
        for (Command command : getBot().getCommandMap().values()) {
            if (!commands.containsKey(command.getName()) && command.isShowInHelp()) {
                commands.put(command.getName(), command);
            }
        }
        for (Command command : commands.values()) {
            embedBuilder.addField(command.getHelpName(), command.getHelpDescription(), true);
        }
        sendMessageBack(event, embedBuilder);
    }
}
