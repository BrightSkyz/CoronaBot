package xyz.skyz.coronabot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.apache.commons.codec.binary.Hex;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import xyz.skyz.coronabot.commands.*;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bot {

    private JDA jda = null;
    private Map<String, Command> commandMap = new HashMap<>();
    private List<String> privilegedStaffRoles = Arrays.asList("Administrator", "Discord Moderation", "Temp Mod");
    private List<String> staffRoles = Arrays.asList("Administrator", "Discord Moderation", "Medical Professional", "Temp Mod", "Trusted User");

    private static Bot bot = null;

    public static void main(String[] args) {
        bot = new Bot();
        bot.start();
    }

    public List<String> getPrivilegedStaffRoles() {
        return privilegedStaffRoles;
    }

    public List<String> getStaffRoles() {
        return staffRoles;
    }

    public static Bot getBot() {
        return bot;
    }

    public Bot() {
        //
    }

    public void start() {
        registerCommand(new AlertCommand(this));
        registerCommand(new HelpCommand(this));
        //registerCommand(new MigrateRolesCommand(this));
        /*registerCommand(new MoveConspiracyCommand(this));
        registerCommand(new MoveJokeCommand(this));
        registerCommand(new MoveMarketCommand(this));
        registerCommand(new MovePoliticsCommand(this));*/
        registerCommand(new SuspendCommand(this));
        registerCommand(new UnsuspendCommand(this));
        try {
            jda = new JDABuilder(Configuration.BOT_TOKEN)
                    .setActivity(Activity.watching("Corona Virus | >help"))
                    .setAutoReconnect(true)
                    .addEventListeners(new CommandListener()).build();
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }

    public String hash(String string) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if (messageDigest == null) {
            return "";
        }
        messageDigest.reset();
        messageDigest.update(string.getBytes(StandardCharsets.UTF_8));
        final byte[] resultByte = messageDigest.digest();
        return new String(Hex.encodeHex(resultByte));
    }

    public JDA getJda() {
        return jda;
    }

    public Map<String, Command> getCommandMap() {
        return commandMap;
    }

    public void registerCommand(Command command) {
        if (!commandMap.containsKey(command.getName())) {
            commandMap.put(command.getName(), command);
            for (String alias : command.getAliases()) {
                commandMap.put(alias, command);
            }
        }
    }

    public EmbedBuilder createEmbedBuilder(String title, String description, String footer) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.decode("#FCBA03"));
        if (title != null) {
            embedBuilder.setTitle(title);
        }
        if (description != null) {
            embedBuilder.setDescription(description);
        }
        if (footer != null) {
            embedBuilder.setFooter(footer);
        }
        return embedBuilder;
    }

    public String sendGet(String url) {
        HttpGet get = new HttpGet(url);
        try (CloseableHttpClient httpClient = HttpClients.createDefault(); CloseableHttpResponse response = httpClient.execute(get)) {
            return EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public String sendPost(String url, List<NameValuePair> urlParameters) {
        HttpPost post = new HttpPost(url);
        try {
            post.setEntity(new UrlEncodedFormEntity(urlParameters));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try (CloseableHttpClient httpClient = HttpClients.createDefault(); CloseableHttpResponse response = httpClient.execute(post)) {
            return EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
