package xyz.skyz.coronabot.commands;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.skyz.coronabot.Bot;
import xyz.skyz.coronabot.Command;

import java.util.Arrays;
import java.util.List;

public class MovePoliticsCommand extends Command {

    public MovePoliticsCommand(Bot bot) {
        super(bot, "movepolitics", "movepolitics", "Send a premade message.", Arrays.asList("movedebate"), false);
    }

    @Override
    public void execute(MessageReceivedEvent event, String aliasUsed, List<String> args) {
        if (!event.isFromGuild()) {
            return;
        }
        List<Role> memberRoles = event.getMember().getRoles();
        boolean hasRole = false;
        for (Role role : memberRoles) {
            for (String staffRole : getBot().getStaffRoles()) {
                if (role.getName().equalsIgnoreCase(staffRole)) {
                    hasRole = true;
                    break;
                }
            }
        }
        if (!hasRole) {
            return;
        }
        String description = event.getAuthor().getAsMention() + " wants you to move out of " + event.getTextChannel().getAsMention() +
                " and keep your heated discussion into the appropriate channel aka ";
        List<TextChannel> foundTextChannels = event.getGuild().getTextChannelsByName("debate", true);
        if (foundTextChannels.size() == 0) {
            description += "#debate";
        } else {
            description += foundTextChannels.get(0).getAsMention();
        }
        sendMessageBack(event, getBot().createEmbedBuilder("Alert", description, "Failure to comply might lead to warnings or the fabled banhammer!"));
    }
}
