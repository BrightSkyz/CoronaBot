package xyz.skyz.coronabot.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.skyz.coronabot.Bot;
import xyz.skyz.coronabot.Command;

import java.util.Arrays;
import java.util.List;

public class MigrateRolesCommand extends Command {

    public MigrateRolesCommand(Bot bot) {
        super(bot, "migrateroles", "migrateroles", "Migrate the roles from the old to the new.", Arrays.asList(), false);
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
        if (event.getAuthor().getId().equalsIgnoreCase("209769851651227648") && event.getAuthor().getId().equalsIgnoreCase("175565741506953216")) {
            return;
        }
        sendMessageBack(event, getBot().createEmbedBuilder("Migrate Roles", "Migrating roles! Please do not re-run this command. This make take awhile.", null));
        for (Member member : event.getGuild().getMembers()) {
            swapRole(event.getGuild(), member, "US", "North America");
            swapRole(event.getGuild(), member, "GB", "Europe");
            swapRole(event.getGuild(), member, "Australia", "Oceania");
            swapRole(event.getGuild(), member, "Canada", "North America");
            swapRole(event.getGuild(), member, "India", "Asia");
            swapRole(event.getGuild(), member, "South Korea", "Asia");
            swapRole(event.getGuild(), member, "Singapore", "Asia");
            swapRole(event.getGuild(), member, "HK", "Asia");
        }
        sendMessageBack(event, getBot().createEmbedBuilder("Migrate Roles", "The roles have been migrated.", null));
    }

    private void swapRole(Guild guild, Member member, String oldRoleName, String newRoleName) {
        List<Role> memberRoles = member.getRoles();
        boolean hasOldRole = false;
        for (Role role : memberRoles) {
            if (role.getName().equalsIgnoreCase(oldRoleName)) {
                hasOldRole = true;
            }
        }
        if (!hasOldRole) {
            return;
        }
        boolean hasNewRole = false;
        for (Role role : memberRoles) {
            if (role.getName().equalsIgnoreCase(newRoleName)) {
                hasNewRole = true;
            }
        }
        if (hasNewRole) {
            return;
        }
        /*Role oldRole = guild.getRolesByName(oldRoleName, true).get(0);
        guild.removeRoleFromMember(member, oldRole).queue();*/
        Role newRole = guild.getRolesByName(newRoleName, true).get(0);
        guild.addRoleToMember(member, newRole).queue();
    }
}
