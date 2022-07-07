package Model;

import Database.*;
import Model.Commandsets.*;
import Model.Functionality.*;
import Model.objects.Leaver;
import discord4j.common.util.Snowflake;
import discord4j.core.*;
import discord4j.core.event.domain.guild.*;
import discord4j.core.event.domain.message.*;
import discord4j.core.object.entity.channel.*;
import discord4j.core.object.presence.*;
import discord4j.rest.util.Color;
import Model.Interfaces.Command;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.time.*;
import java.util.*;


public class Main {
    //! TODO change b4 upload
    private static final String token = ""; //actual bot
    //private static final String token = ""; // testbot
    public static DbController db;

   public static  ResourceBundle txt = ResourceBundle.getBundle("ResourceBundles.StringResourceBundle");
   public static String prefix = txt.getString("prefix");
   private static RoleSettings roleSettings;
   private static ExodusFunctions exodusFunctions;

   private static String assignChannel = "905055775318233088";

   private static Merits merits;
   private static  Support support;
   private static Strikes strikes;
   private static TeamPoints teamPoints;
   private static final Map<String, Command> commands = new HashMap<>();
   public static EmbedGenerator embedGenerator;
   public static Functions functions;
    //displays all commands (except test) (to be updated)
   public static String systemMessages = "781201790838374440"; //real
  //  public static String systemMessages ="870225241593503774";





    static {
        commands.put("help", event -> {
                    final MessageChannel channel = event.getMessage().getChannel().block();

                    channel.createEmbed(spec ->
                            spec.setColor(Color.BISMARK)
                                    .setTitle("Helper")
                                    .setDescription("Please use **"+prefix+"h[topic]** to get the functions of a specific topic."+/*"\n\nthe **"+prefix+"invsupport** command gives a link to the support discord"+*/" \n \n the **"+prefix+"invite** command gives the invite link for the bot \n \nthe current list:")
                                    .addField("merit", prefix+"hmerit", true)
                                    .addField("strike", prefix+"hstrike", true)
                                    .addField("roles", prefix+"hroles", true)
                                    .addField("teams",prefix+"hteams",true)
                                    .addField("exodus",prefix+"hexodus",true)
                            .setTimestamp(Instant.now())
                    ).block();


                }

        );
        /*
        commands.put("msgclear", event -> {
            functions.clearmsg(event);
        });*/
        commands.put("sheettest", event -> {
           try{
               Sheets.readtest(event);
           } catch (Exception e){
               e.printStackTrace();
               EmbedGenerator.errorEmbed(event.getMessage().getAuthorAsMember().block().getPrivateChannel().block(),"Values not found","sheets down refresh tokens");
           }
        });

/*
        commands.put("invite", event -> {
            final MessageChannel channel = event.getMessage().getChannel().block();

            channel.createMessage("Before adding the bot check if the server has a text channel in which it can send a message" +
                    "\n\nhttps://discord.com/api/oauth2/authorize?client_id=831134005294006322&permissions=2214976576&scope=bot").block();
        });*/
    }



    public static void main(String[] args){
        embedGenerator = new EmbedGenerator();
        db = new DbController();

        functions = new Functions();
        //Create instances of commandsets
        merits = new Merits();
        strikes = new Strikes();
        roleSettings = new RoleSettings();
        teamPoints = new TeamPoints();
        support = new Support();
        exodusFunctions = new ExodusFunctions();
        //Add commandsets to existing commands
        merits.getCommandset(commands);
        strikes.getCommandset(commands);
        teamPoints.getCommandset(commands);
        roleSettings.getCommandset(commands);
        support.getCommandset(commands);
        exodusFunctions.getCommandset(commands);

        //start discord bot + polling
        final GatewayDiscordClient client = DiscordClientBuilder.create(token)
                                                                .build()
                                                                .login()
                                                                .block();
        //Updates the status of the bot
        client.updatePresence(Presence.online(Activity.playing(prefix+"help"))).subscribe();
        //checks reaction added
       client.getEventDispatcher().on(ReactionRemoveEvent.class).subscribe(event ->{
           if (event.getGuild().block() != null ){
               try{
              exodusFunctions.learnerRemoveApp(event);
               } catch (Exception e){

               }
           }

       });
       client.getEventDispatcher().on(MemberLeaveEvent.class).subscribe(e ->{

           try {
               //!TODO reinstate
            if (e.getGuild().block().getId().asString().equals(ExodusFunctions.guildid)){
                   Sheets.addleaver(e);
               }

           } catch (Exception ex) {
               ex.printStackTrace();
           }
       });

       client.getEventDispatcher().on(MemberJoinEvent.class).subscribe(e ->{

           try {
                Leaver l = Sheets.userHasLeftBefore(functions.removeSymbols(e.getMember().getId().asString()));
                if (l != null){
                    int state = l.getTimes();
                    //!TODO reinstate
                    if (state >= 1 && e.getGuild().block().getId().asString().equals(ExodusFunctions.guildid)) {
                        //send the msg that this member left already
                        String role = Sheets.userHasLeftBeforeRole(functions.removeSymbols(e.getMember().getId().asString()));
                        EmbedGenerator.errorEmbed((MessageChannel) e.getGuild().block().getChannelById(Snowflake.of(systemMessages)).block(), "User on leavers list", "This user has left the discord: " + state + " times. \n" +
                                "Last found role:*** " + role + "***.");
                    }
                }
           } catch (GeneralSecurityException generalSecurityException) {
               generalSecurityException.printStackTrace();
           } catch (IOException ioException) {
               ioException.printStackTrace();
           }

       });



        client.getEventDispatcher().on(ReactionAddEvent.class).subscribe(event ->{

                if (event.getGuild().block() == null ){
                    if (!event.getUser().block().isBot()){
                        if(event.getMessage().block().getContent().startsWith("How did you find")){
                            exodusFunctions.applicationSetWay(event);
                        }else {
                            exodusFunctions.learnerappReaction(event);
                        }
                    }
                }else {

                        exodusFunctions.learnerReaction(event);



                }
        });

        client.getEventDispatcher().on(GuildDeleteEvent.class).subscribe(event->{
                try {
                    db.removeGuild(event.getGuildId().asString());
                } catch (SQLException e) {
                    e.printStackTrace();
                }

        });


        client.getEventDispatcher().on(GuildCreateEvent.class).subscribe(event->{
            //change this to how to set up the bot
            boolean r;

            for(GuildChannel e : event.getGuild().getChannels().toIterable()) {
                try {
                    r = db.getServer( event.getGuild().getId().asString());
                } catch (SQLException er) {
                    er.printStackTrace();
                    return;
                }
                if (r == false) {
                    try {

                        TextChannel txt = (TextChannel) e;
                        EmbedGenerator.channelmsgEmbed(txt, "Hello!", "Hi, Thanks for adding me to your server\n Let's get started with setting me up.\n The next few embeds will show you the steps to take to get me fully operational");
                        EmbedGenerator.channelmsgEmbed(txt, "Step 1:", "The first step is to add admin bot permissions to a role,\n" +
                                " roles with this permission are able to use all commands of the bot" +
                                " \n\n **How to use the command: \n " + prefix + "add-admin @role**" +
                                " \n once done this command will be locked behind this role so make sure you can access it");

                        EmbedGenerator.channelmsgEmbed(txt, "Step 2:", "The second step is similar to the first but for moderators of the bot" +
                                "the roles added to this permission will be able to add and remove points from users/teams" +
                                "in the functions like merits and strikes\n \n **How to use the command: \n " + prefix + "add-mod @role **" +
                                "\n note that only members with admin bot permissions can access this command.");

                        EmbedGenerator.channelmsgEmbed(txt, "Final notes",
                                " 1) Kicking and re-adding the bot will remove all data of the server stored in the bot's database(eg. what roles are assigned to perms and who has howmany points,...)" +
                                        "\n\n" +
                                        "2) you can add multiple roles to each of the permissions mentioned in the steps above by using the same command" +
                                        "\n\n" +
                                        "3) If you ever have difficulty using a command you can use **" + prefix + "help** to see what commands do and how to use them (HTU:) This command is also displayed as the status of the bot in case you forget it" +
                                        "\n\n" +
                                        "4) This bot may still contain bugs if you find any feel free to report it to devs so that they can be resolved");



                        db.newServerjoin(event.getGuild().getId().asString());
                       break;
                    } catch (Exception f) {
                    }
                }
            }
            });


        //detect messages;
        client.getEventDispatcher().on(MessageCreateEvent.class)
                // subscribe is like block, in that it will *request* for action
                // to be done, but instead of blocking the thread, waiting for it
                // to finish, it will just execute the results asynchronously.
                .subscribe(event -> {
                    // 3.1 Message.getContent() is a String
                    final String content = event.getMessage().getContent();
                    if(event.getMessage().getChannel().block().getLastMessage().block().getGuild().block() == null && !event.getMessage().getAuthor().get().isBot()){
                        //bot msg will not be used
                        event.getMessage().getAuthor().get().getPrivateChannel().block().type().block();
                        exodusFunctions.learnerapp(event);
                    }



                    for (final Map.Entry<String, Command> entry : commands.entrySet()) {
                        if (content.startsWith("." + entry.getKey())) {
                            entry.getValue().execute(event);
                            break;
                        }
                    }

                });


        client.onDisconnect()
                .block();
    }
}
