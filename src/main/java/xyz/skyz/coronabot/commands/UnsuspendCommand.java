package xyz.skyz.coronabot.commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.skyz.coronabot.Bot;
import xyz.skyz.coronabot.Command;

import java.util.Arrays;
import java.util.List;

public class UnsuspendCommand extends Command {

    public UnsuspendCommand(Bot bot) {
        super(bot, "unsuspend", "unsuspend [mention]", "Un-suspend a user.", Arrays.asList(), false);
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
        if (args.size() != 1 || event.getMessage().getMentionedMembers().size() != 1) {
            sendMessageBack(event, getBot().createEmbedBuilder("Un-suspend", "Usage: >unsuspend [mention]", null));
            return;
        }
        Member mentionedMember = event.getMessage().getMentionedMembers().get(0);
        List<Role> mentionedMemberRoles = mentionedMember.getRoles();
        boolean isUnSuspendable = false;
        for (Role role : mentionedMemberRoles) {
            for (String staffRole : getBot().getStaffRoles()) {
                if (role.getName().equalsIgnoreCase(staffRole)) {
                    sendMessageBack(event, getBot().createEmbedBuilder("Suspend", "You cannot suspend this person.", null));
                    return;
                }
                if (role.getName().equalsIgnoreCase("Suspended")) {
                    isUnSuspendable = true;
                }
            }
        }
        if (!isUnSuspendable) {
            sendMessageBack(event, getBot().createEmbedBuilder("Un-Suspend", "The person specified doesn't have the suspended role.", null));
            return;
        }
        try {
            if (event.getTextChannel().getName().startsWith("q-")) {
                event.getTextChannel().delete().queue();
            }
            Role defaultRole = event.getGuild().getRolesByName("Default", true).get(0);
            event.getGuild().addRoleToMember(mentionedMember, defaultRole).queue();
            Role suspendedRole = event.getGuild().getRolesByName("Suspended", true).get(0);
            event.getGuild().removeRoleFromMember(mentionedMember, suspendedRole).queue();
            if (!event.getTextChannel().getName().startsWith("q-")) {
                sendMessageBack(event, getBot().createEmbedBuilder("Un-Suspend", "The person has been un-suspended.", null));
            }
            } catch (Exception e) {
            sendMessageBack(event, getBot().createEmbedBuilder("Un-Suspend", "An error occurred.", null));
        }
    }
}
