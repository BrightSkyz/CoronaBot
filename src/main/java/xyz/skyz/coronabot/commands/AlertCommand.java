package xyz.skyz.coronabot.commands;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.skyz.coronabot.Bot;
import xyz.skyz.coronabot.Command;

import java.util.Arrays;
import java.util.List;

public class AlertCommand extends Command {

    public AlertCommand(Bot bot) {
        super(bot, "alert", "alert [message]", "Send a custom alert.", Arrays.asList(), false);
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
        String description = "";
        for (String arg : args) {
            description += arg + " ";
        }
        sendMessageBack(event, getBot().createEmbedBuilder("Alert", description, "Sent by " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator()));
    }
}
