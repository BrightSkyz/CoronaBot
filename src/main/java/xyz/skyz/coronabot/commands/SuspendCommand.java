package xyz.skyz.coronabot.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.skyz.coronabot.Bot;
import xyz.skyz.coronabot.Command;

import java.util.Arrays;
import java.util.List;

public class SuspendCommand extends Command {

    public SuspendCommand(Bot bot) {
        super(bot, "suspend", "suspend [mention] <reason>", "Suspend a user.", Arrays.asList(), false);
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
        if (args.size() < 1) {
            sendMessageBack(event, getBot().createEmbedBuilder("Suspend", "Usage: >suspend [mention] <reason>", "Uh-oh."));
            return;
        }
        Member mentionedMember = null;
        if (event.getMessage().getMentionedMembers().size() < 1) {
            try {
                mentionedMember = event.getGuild().getMemberById(args.get(0));
            } catch (Exception e) {
                sendMessageBack(event, getBot().createEmbedBuilder("Suspend", "Invalid user.", "Uh-oh."));
                return;
            }
        } else {
            mentionedMember = event.getMessage().getMentionedMembers().get(0);
        }
        if (mentionedMember == null) {
            sendMessageBack(event, getBot().createEmbedBuilder("Suspend", "Invalid user.", "Uh-oh."));
            return;
        }
        List<Role> mentionedMemberRoles = mentionedMember.getRoles();
        boolean isSuspendable = false;
        boolean hasDefaultRole = false;
        for (Role role : mentionedMemberRoles) {
            for (String staffRole : getBot().getStaffRoles()) {
                if (role.getName().equalsIgnoreCase(staffRole)) {
                    sendMessageBack(event, getBot().createEmbedBuilder("Suspend", "You cannot suspend this person.", "Uh-oh."));
                    return;
                }
                if (role.getName().equalsIgnoreCase("Default")) {
                    hasDefaultRole = true;
                }
            }
        }
        /*if (!isSuspendable) {
            sendMessageBack(event, getBot().createEmbedBuilder("Suspend", "The person specified doesn't have the default role.", "Uh-oh."));
            return;
        }*/
        try {
            if (event.getGuild().getSelfMember().hasPermission(Permission.VOICE_MOVE_OTHERS, Permission.VOICE_CONNECT) &&
                    event.getGuild().getSelfMember().canInteract(mentionedMember) &&
                    mentionedMember.getVoiceState().inVoiceChannel()) {
                VoiceChannel voiceChannel;
                try {
                    voiceChannel = event.getGuild().createVoiceChannel("Voice Kick")
                            .setParent(mentionedMember.getVoiceState().getChannel().getParent()).reason("Kick from voice channel").complete();

                    event.getGuild().moveVoiceMember(mentionedMember, voiceChannel).complete();
                    voiceChannel.delete().reason("Kick from voice channel").complete();
                } catch (Exception e) {
                    // Ignore
                }
            }

            Category quarantineCategory = null;
            if (event.getGuild().getCategoriesByName("Quarantine", true).size() == 0) {
                sendMessageBack(event, getBot().createEmbedBuilder("Suspend", "The category doesn't exist.", "Uh-oh."));
                return;
            } else {
                quarantineCategory = event.getGuild().getCategoriesByName("Quarantine", true).get(0);
            }
            String randomString = getBot().hash((Math.random() * 100) + "abc").substring(0, 5);
            TextChannel quarantineChannel = event.getGuild().createTextChannel("q-" + randomString).setParent(quarantineCategory).complete();
            quarantineChannel.getManager().putPermissionOverride(mentionedMember, Arrays.asList(Permission.VIEW_CHANNEL), Arrays.asList()).complete();

            String reason = "";
            if (args.size() > 1) {
                boolean firstArg = true;
                for (String arg : args) {
                    if (firstArg) {
                        firstArg = false;
                    } else {
                        reason += arg + " ";
                    }
                }
            }
            String description = "User suspended: " + mentionedMember.getAsMention() + "\n";
            if (reason.equalsIgnoreCase("")) {
                description += "Reason: None";
            } else {
                description += "Reason: " + reason;
            }
            quarantineChannel.sendMessage(getBot().createEmbedBuilder("Suspend", description, null).build()).queue();

            Role suspendedRole = event.getGuild().getRolesByName("Suspended", true).get(0);
            event.getGuild().addRoleToMember(mentionedMember, suspendedRole).queue();
            if (hasDefaultRole) {
                Role defaultRole = event.getGuild().getRolesByName("Default", true).get(0);
                event.getGuild().removeRoleFromMember(mentionedMember, defaultRole).queue();
            }
            sendMessageBack(event, getBot().createEmbedBuilder("Suspend", "The person has been suspended.", "Uh-oh."));

            if (event.getGuild().getTextChannelsByName("staff", true).size() != 0) {
                TextChannel staffChannel = event.getGuild().getTextChannelsByName("staff", true).get(0);
                if (staffChannel.canTalk()) {
                    String staffDescription = event.getMember().getAsMention() + " has suspended the user " + mentionedMember.getAsMention() + ".";
                    staffChannel.sendMessage(getBot().createEmbedBuilder("Suspend", staffDescription, null).build()).queue();
                }
            }

            mentionedMember.getUser().openPrivateChannel().queue((privateChannel -> {
                privateChannel.sendMessage(getBot().createEmbedBuilder("Suspend",
                        "You have been Suspended by one of our Discord staff.\n" +
                                "To appeal your suspension/talk about your suspension please go to the quarantine channel.", null).build()).queue();
            }));
        } catch (Exception e) {
            e.printStackTrace();
            sendMessageBack(event, getBot().createEmbedBuilder("Suspend", "An error occurred.", "Uh-oh."));
        }
    }
}
