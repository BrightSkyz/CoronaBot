package xyz.skyz.coronabot.commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ContextException;
import xyz.skyz.coronabot.Bot;
import xyz.skyz.coronabot.Command;

import java.util.Arrays;
import java.util.List;

public class SuspendCommand extends Command {

    public SuspendCommand(Bot bot) {
        super(bot, "suspend", "suspend [mention]", "Suspend a user.", Arrays.asList(), false);
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
            sendMessageBack(event, getBot().createEmbedBuilder("Suspend", "Usage: !suspend [mention]", "Uh-oh."));
            return;
        }
        Member mentionedMember = event.getMessage().getMentionedMembers().get(0);
        List<Role> mentionedMemberRoles = mentionedMember.getRoles();
        boolean isSuspendable = false;
        for (Role role : mentionedMemberRoles) {
            for (String staffRole : getBot().getStaffRoles()) {
                if (role.getName().equalsIgnoreCase(staffRole)) {
                    sendMessageBack(event, getBot().createEmbedBuilder("Suspend", "You cannot suspend this person.", "Uh-oh."));
                    return;
                }
                if (role.getName().equalsIgnoreCase("Default")) {
                    isSuspendable = true;
                }
            }
        }
        if (!isSuspendable) {
            sendMessageBack(event, getBot().createEmbedBuilder("Suspend", "The person specified doesn't have the default role.", "Uh-oh."));
            return;
        }
        try {
            Role suspendedRole = event.getGuild().getRolesByName("Suspended", true).get(0);
            event.getGuild().addRoleToMember(mentionedMember, suspendedRole).queue();
            Role defaultRole = event.getGuild().getRolesByName("Default", true).get(0);
            event.getGuild().removeRoleFromMember(mentionedMember, defaultRole).queue();
            sendMessageBack(event, getBot().createEmbedBuilder("Suspend", "The person has been suspended.", "Uh-oh."));

            mentionedMember.getUser().openPrivateChannel().queue((privateChannel -> {
                privateChannel.sendMessage(getBot().createEmbedBuilder("Suspend",
                        "You have been Suspended by one of our Discord staff.\n" +
                                "To appeal your suspension/talk about your suspension please go to the #quarantine channel.", null).build()).queue();
            }));
        } catch (Exception e) {
            e.printStackTrace();
            sendMessageBack(event, getBot().createEmbedBuilder("Suspend", "An error occurred.", "Uh-oh."));
        }
    }
}
