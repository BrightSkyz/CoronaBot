package xyz.skyz.coronabot.commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.skyz.coronabot.Bot;
import xyz.skyz.coronabot.Command;

import java.util.Arrays;
import java.util.List;

public class StuckChannelCommand extends Command {

    public StuckChannelCommand(Bot bot) {
        super(bot, "stuckchannel", "stuckchannel", "Remove a stuck quarantine channel.", Arrays.asList(), false);
    }

    @Override
    public void execute(MessageReceivedEvent event, String aliasUsed, List<String> args) {
        if (!event.isFromGuild()) {
            return;
        }
        List<Role> memberRoles = event.getMember().getRoles();
        boolean hasRole = false;
        for (Role role : memberRoles) {
            for (String staffRole : getBot().getPrivilegedStaffRoles()) {
                if (role.getName().equalsIgnoreCase(staffRole)) {
                    hasRole = true;
                    break;
                }
            }
        }
        if (!hasRole) {
            return;
         }
        if (event.getTextChannel().getName().startsWith("q-")) {
            event.getTextChannel().delete().queue();
        }
    }
}
