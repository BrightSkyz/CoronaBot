package xyz.skyz.coronabot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public abstract class Command {

    private Bot bot;
    private String name;
    private String helpName;
    private String helpDescription;
    private List<String> aliases;
    private boolean showInHelp;

    public Command(Bot bot, String name, String helpName, String helpDescription, List<String> aliases, boolean showInHelp) {
        this.bot = bot;
        this.name = name;
        this.helpName = helpName;
        this.helpDescription = helpDescription;
        this.aliases = aliases;
        this.showInHelp = showInHelp;
    }

    public abstract void execute(MessageReceivedEvent event, String aliasUsed, List<String> args);

    public Bot getBot() {
        return bot;
    }

    public String getName() {
        return name;
    }

    public String getHelpName() {
        return helpName;
    }

    public String getHelpDescription() {
        return helpDescription;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public boolean isShowInHelp() {
        return showInHelp;
    }

    public void sendMessageBack(MessageReceivedEvent event, EmbedBuilder embedBuilder) {
        if (event.getChannelType().isGuild()) {
            if (!event.getTextChannel().canTalk()) {
                return;
            }
        }
        if (event.getChannelType().isGuild() && !event.getGuild().getSelfMember().hasPermission(event.getTextChannel(), Permission.MESSAGE_EMBED_LINKS)) {
            MessageEmbed embed = embedBuilder.build();
            if (embed.getTitle() == null) {
                return;
            }
            if (embed.getDescription() == null) {
                return;
            }
            return;
            /*event.getTextChannel().sendMessage("**" + embed.getTitle() + "** (Requested by " +
                    MessageHelper.sanitizeMessage(getBot(), event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator(), false) + ")\n" +
                    embed.getDescription()).queue();*/
        } else {
            event.getChannel().sendMessage(embedBuilder.build()).queue();
        }
    }
}
