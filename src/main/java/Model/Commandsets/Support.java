package Model.Commandsets;

import Database.DbCommandsets.DbRolesettings;
import Model.Functionality.EmbedGenerator;
import Model.Functionality.Functions;
import Model.Interfaces.Command;
import Model.Interfaces.CommandSet;
import Model.Main;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.channel.GuildChannel;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.entity.channel.TextChannel;

import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;

public class Support implements CommandSet {
    private DbRolesettings db;
    private static final Map<String, Command> set = new HashMap<>();
    private String prefix = Main.prefix;
    private ResourceBundle bundle = ResourceBundle.getBundle("ResourceBundles.StringResourceBundle");
    private Functions functions = new Functions();
    private String guildid = "833091112528642068";
    private String userbugrep ="833094161975607368";
    private String devbugrep = "833094056761622559";
    private String devfeedback = "837436876603916348";
    private String devsuggestions = "833093642213785621";
    private String devsupport = "837436726938435614";

    @Override
    public Map<String, Command> getCommandset(Map<String, Command> commands) {
        commands.putAll(set);
        return commands;
    }

    @Override
    public void commandHelp(MessageCreateEvent event) {
        final MessageChannel channel = event.getMessage().getChannel().block();
        ArrayList<String> titles =new ArrayList<>();
        ArrayList<String> htu = new ArrayList<>();
        titles.add(prefix+"hteams"); htu.add("shows the commands of this section \n HTU: "+prefix+"hteams");



        EmbedGenerator.commandListEmbed(channel,"Teams Helper", "List of teams functions \n\t * optional field \n",titles,htu);
    }

    public Support() {
        this.db = Main.db.serversettings();
        set.put("bugreport",this::bugreport);
        set.put("feedback",this::feedbackReport);
        set.put("suggest", this::suggestionReport);
        set.put("support", this::supportnReport);
        set.put("channelmsg", this::createMessages);
    }

    private void createMessages(MessageCreateEvent event){
        event.getMessage().delete().block();
        final Guild guild = event.getGuild().block();
        ArrayList<String> AdminIds;
        Set<Snowflake> roles;
        try {
             AdminIds = db.getAdminsOfServer(guild.getId().asString());
             roles = event.getMessage().getAuthorAsMember().block().getRoleIds();

        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        if ((event.getGuild().block().getId().asString().equals(guildid) &&
                event.getMessage().getChannelId().asString().equals(userbugrep)) &&  Functions.isAdmin(AdminIds, roles)) {
            EmbedGenerator.channelmsgEmbed(event.getMessage().getChannel().block(), "Bugreport", bundle.getString("bugreport"));
            EmbedGenerator.channelmsgEmbed(event.getMessage().getChannel().block(), "Feedback", bundle.getString("feedback"));
            EmbedGenerator.channelmsgEmbed(event.getMessage().getChannel().block(), "Suggestions", bundle.getString("suggestions"));
            EmbedGenerator.channelmsgEmbed(event.getMessage().getChannel().block(), "Support", bundle.getString("support"));
        }
    }

    /**
     *  sends a bug report to the channel for devs
     * @author Gmoley
     * @param event gives detected discord msg
     * @since 9/may/2021
     * @version 1.0
     */
    private void bugreport(MessageCreateEvent event) {
        final List<String> command = Arrays.asList(event.getMessage().getContent().split(" "));
        String author = event.getMessage().getAuthorAsMember().block().getNicknameMention();
        String msg = "";
        try{
            msg = command.get(1);
            if (!(command.size()<=2)){
            msg = msg +" "+ functions.getmsg(command);
            }

       } catch (Exception e){
           return;
       }

        if (event.getGuild().block().getId().asString().equals(guildid) &&
                event.getMessage().getChannelId().asString().equals(userbugrep)
        ){
            GuildChannel channel = event.getGuild().block().getChannelById(Snowflake.of(devbugrep)).block();
            TextChannel ch = (TextChannel) channel;
            event.getMessage().delete().block();
            ch.createMessage( "***by "+ author +":***\n"+ msg
            ).block();
        }
    }

    /**
     *  sends a feeedback report to the channel for devs
     * @author Gmoley
     * @param event gives detected discord msg
     * @since 9/may/2021
     * @version 1.0
     */
    private void feedbackReport(MessageCreateEvent event) {
        final List<String> command = Arrays.asList(event.getMessage().getContent().split(" "));
        String author = event.getMessage().getAuthorAsMember().block().getNicknameMention();
        String msg = "";
        try{
            msg = command.get(1);
            if (!(command.size()<=2)){
                msg = msg +" "+ functions.getmsg(command);
            }

        } catch (Exception e){
            return;
        }

        if (event.getGuild().block().getId().asString().equals(guildid) &&
                event.getMessage().getChannelId().asString().equals(userbugrep)
        ){
            GuildChannel channel = event.getGuild().block().getChannelById(Snowflake.of(devfeedback)).block();
            TextChannel ch = (TextChannel) channel;
            event.getMessage().delete().block();
            ch.createMessage( "***by "+ author +":***\n"+ msg
            ).block();
        }
    }

    /**
     *  sends a suggestion report to the channel for devs
     * @author Gmoley
     * @param event gives detected discord msg
     * @since 9/may/2021
     * @version 1.0
     */
    private void suggestionReport(MessageCreateEvent event) {
        final List<String> command = Arrays.asList(event.getMessage().getContent().split(" "));
        String author = event.getMessage().getAuthorAsMember().block().getNicknameMention();
        String msg = "";
        try{
            msg = command.get(1);
            if (!(command.size()<=2)){
                msg = msg +" "+ functions.getmsg(command);
            }

        } catch (Exception e){
            return;
        }
        if (event.getGuild().block().getId().asString().equals(guildid) &&
                event.getMessage().getChannelId().asString().equals(userbugrep)
        ){
            GuildChannel channel = event.getGuild().block().getChannelById(Snowflake.of(devsuggestions)).block();
            TextChannel ch = (TextChannel) channel;
            event.getMessage().delete().block();
            ch.createMessage( "***by "+ author +":***\n"+ msg
            ).block();
        }
    }

    /**
     *  sends a support ticket to the channel for mods
     * @author Gmoley
     * @param event gives detected discord msg
     * @since 9/may/2021
     * @version 1.0
     */
    private void supportnReport(MessageCreateEvent event) {
        System.out.println(event.getMessage().getChannelId().toString());

        final List<String> command = Arrays.asList(event.getMessage().getContent().split(" "));
        String author = event.getMessage().getAuthorAsMember().block().getNicknameMention();
        String msg = "";
        try{
            msg = command.get(1);
            if (!(command.size()<=2)){
                msg = msg +" "+ functions.getmsg(command);
            }

        } catch (Exception e){
            return;
        }
        if (event.getGuild().block().getId().asString().equals(guildid) &&
                event.getMessage().getChannelId().asString().equals(userbugrep)
        ){
            GuildChannel channel = event.getGuild().block().getChannelById(Snowflake.of(devsupport)).block();
            TextChannel ch = (TextChannel) channel;
            event.getMessage().delete().block();
            ch.createMessage( "***by "+ author +":***\n"+ msg
            ).block();
        }
    }



}
