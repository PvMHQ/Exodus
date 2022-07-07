package Model.Commandsets;

import Database.DbCommandsets.DbLearnerApp;
import Database.DbCommandsets.DbRolesettings;
import Model.Functionality.EmbedGenerator;
import Model.Functionality.Functions;
import Model.Functionality.SubmissionStatus;

import Model.Interfaces.Command;
import Model.Interfaces.CommandSet;
import Model.Main;
import com.fasterxml.jackson.databind.ObjectMapper;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.event.domain.message.ReactionRemoveEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.GuildChannel;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.rest.util.Color;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.*;

public class ExodusFunctions implements CommandSet {

    //testserver
/*
    private final static String guildid ="842452698984874014";
    private String applicantRoleid ="849695311248228383";
    private String memberRoleid ="849695338339893348";
    private String recruitRoleid ="849695359755092008";
    private String queueChannelId = "842454213728665630";
    private String lastmsgId ="850835883754127450";
    private String applicationChannel = "850832063308759041";
    private String msgid = "856187134763466792";
    private String learnerUpdateChannel ="850832063308759041";
    private String coxLearner ="849695359755092008";
    private String tobLearner = "849695359755092008";
    private final String botid = "699734751505809549";
*/

    //exodus
    public final static String guildid ="780579110333841408";
    private String applicantRoleid ="780682225649057805";
    private String memberRoleid ="780633736315535411";
    private String queueChannelId = "780818808872894555";
    private String lastmsgId ="836565865255403520";
    private String applicationChannel = "780601461905948733";
    private String msgid = "782898867833274378";
    private String learnerUpdateChannel ="780602793571516446";
    private String coxLearner ="780824955306508329";
    private String tobLearner = "780814536318910474";
    private final String botid = "699734751505809549";
    private static SubmissionStatus submissionStatus = SubmissionStatus.EVENTSETUP;
    private static String eventParticipantChannel = "905055857149112370";

    private String recruitRoleid ="846220583720189992";
    private String corporalRoleid = "780601682957565962";
    private String sergeantRoleid = "780601656895209514";
    private String officerRoleid = "780601630134370304";
    private String commanderRoleid = "780601699726786570";
    private String colonelRoleid = "846219391842254879";
    private String brigadierRoleid = "846219569588076564";
    private String admiralRoleid = "847622091037736971";
    private String staffRoleId = "780809394111381544";
    private String proselyteRoleId = "846217831020494878";
    private String starRoleId = "780809294139359263";



    private ResourceBundle s = ResourceBundle.getBundle("ResourceBundles.ExodusResourceBundle");
    private DbRolesettings db;
    private static final Map<String, Command> set = new HashMap<>();
    private static Functions functions = Main.functions;
    private String prefix = Main.prefix;
    private ResourceBundle txt = Main.txt;
    private DbLearnerApp dblearnerApp = new DbLearnerApp();



    @Override
    public Map<String, Command> getCommandset(Map<String, Command> commands) {
        commands.putAll(set);
        return commands;
    }

    public ExodusFunctions() {
        this.db = Main.db.serversettings();
        set.put("accept", this::forceapplicationAccept);
        set.put("regtobembed",this::regtobembed);
        set.put("regcoxembed",this::regcoxembed);
        set.put("HMtobembed",this::HMtobembed);
        set.put("hexodus",this::commandHelp);
        set.put("deny",this::applicationDeny);
        set.put("promoteMember",this::promoteMember);
        set.put("rw",this::runewatch);
    }

    private void runewatch(MessageCreateEvent event) {


        try {
            List<String> command = Arrays.asList(event.getMessage().getContent().split(" "));

            String rsn=command.get(1);
            int size = command.size();
            for (int i = 2 ; i < size ; i++){
                rsn = rsn  + " "+command.get(i);
            }
            System.out.println(rsn);
            System.out.println("https://runewatch.com/api/cases/"+rsn);
            URL url = new URL("https://runewatch.com/api/cases/"+rsn);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("accept", "application/json");
            InputStream responseStream = connection.getInputStream();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            responseStream));

            StringBuilder response = new StringBuilder();
            String currentLine;

            while ((currentLine = in.readLine()) != null)
                response.append(currentLine);

            in.close();
            String res = response.toString();
           System.out.println(response.toString());
            if (res.startsWith("{\"error\"")){
                event.getMessage().getChannel().block().createMessage("Runewatch result: Player not found!").block();
            } else {
                event.getMessage().getChannel().block().createMessage("Runewatch result: Player accusation found!").block();
            }





        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void applicationDeny(MessageCreateEvent event) {
        final MessageChannel channel = event.getMessage().getChannel().block();
        final Guild g = event.getGuild().block();
       Boolean ismod = functions.isMod(functions.getModRoles(event),event.getMessage().getAuthorAsMember().block().getRoleIds());
        if(ismod && g.getId().asString().equals(guildid) && channel.getId().asString().equals(applicationChannel)){
            Message msg = channel.getLastMessage().block();
            while (msg.getId().asString().equals(lastmsgId) == false || msg.isPinned() == false){
                String msgid = msg.getId().asString();
                try {
                msg.delete().block();
                } catch (Exception e){}
                msg = channel.getMessagesBefore(Snowflake.of(msgid)).blockFirst();
                if (msg.isPinned() ||msg.getId().asString().equals(lastmsgId)) break;
            }
        } else if ( !channel.getId().asString().equals(applicationChannel)){
            EmbedGenerator.errorEmbed(event.getMessage().getAuthor().get().getPrivateChannel().block(),"Error","You used this command in the wrong channel. please try again in #application");
            event.getMessage().delete().block();
        }
    }


    @Override
    public void commandHelp(MessageCreateEvent event) {
        final MessageChannel channel = event.getMessage().getChannel().block();
        ArrayList<String> titles =new ArrayList<>();
        ArrayList<String> htu = new ArrayList<>();
        titles.add(prefix+"accept"); htu.add("sets ranks to those of a fresh recruit \n HTU: "+prefix+"accept @member ");
        titles.add(prefix+"regtobembed");htu.add("Creates a navigation menu for regular TOB");
        titles.add(prefix+"HMtobembed");htu.add("Creates a navigation menu for Hard mode TOB");
        titles.add(prefix+"regcoxembed");htu.add("Creates a navigation menu for regular COX");
        titles.add(prefix+"promoteMember");htu.add("promotes a member to a higher rank \n HTU: "+prefix+"promoteMember @member1 *@member2 ....");
        EmbedGenerator.commandListEmbed(channel,"Exodus Helper", "List of functions specific for exodus pvm\n\t * optional field \n",titles,htu);
    }

    private void promoteMember(MessageCreateEvent event){
        final Guild g = event.getGuild().block();
        final List<String> command = Arrays.asList(event.getMessage().getContent().split(" "));
        ArrayList<String> members = new ArrayList<>();
        Set<Snowflake>   AuthorRoles = event.getMessage().getAuthor().get().asMember(Snowflake.of(guildid)).block().getRoleIds();
        if (AuthorRoles.contains(Snowflake.of(staffRoleId)) || AuthorRoles.contains(Snowflake.of(proselyteRoleId))){
            for (int i =1; i< command.size();i++){
                try{
               String member = functions.removeSymbols( command.get(i));
               //avoid duplicates
               if (!members.contains(member) || !member.isEmpty()){
               members.add(member);
               }
                } catch (Exception e){}
            }
            if (members.isEmpty()) {return;}
            for (String member : members){

                try{
                Member m = g.getMemberById(Snowflake.of(member)).block();

                //check if he is a clanmember
                    if (m.getRoleIds().contains(Snowflake.of(memberRoleid))){
                        String beginrole = "";
                        String endrole = "";
                        if (m.getRoleIds().contains(Snowflake.of(recruitRoleid))){
                            m.addRole(Snowflake.of(corporalRoleid)).block();
                            m.removeRole(Snowflake.of(recruitRoleid)).block();
                            beginrole =recruitRoleid;
                            endrole = corporalRoleid;
                        } else if (m.getRoleIds().contains(Snowflake.of(corporalRoleid))){
                            m.addRole(Snowflake.of(sergeantRoleid)).block();
                            m.removeRole(Snowflake.of(corporalRoleid)).block();
                            beginrole = corporalRoleid;
                            endrole = sergeantRoleid;
                        } else if (m.getRoleIds().contains(Snowflake.of(sergeantRoleid))){
                            m.addRole(Snowflake.of(officerRoleid)).block();
                            m.removeRole(Snowflake.of(sergeantRoleid)).block();
                            beginrole = sergeantRoleid;
                            endrole = officerRoleid;
                        } else if (m.getRoleIds().contains(Snowflake.of(officerRoleid))){
                            m.addRole(Snowflake.of(commanderRoleid)).block();
                            m.removeRole(Snowflake.of(officerRoleid)).block();
                            beginrole = officerRoleid;
                            endrole = commanderRoleid;
                        } else if (m.getRoleIds().contains(Snowflake.of(commanderRoleid))){
                            m.addRole(Snowflake.of(colonelRoleid)).block();
                            m.removeRole(Snowflake.of(commanderRoleid)).block();
                            beginrole = commanderRoleid;
                            endrole = colonelRoleid;
                        } else if (m.getRoleIds().contains(Snowflake.of(colonelRoleid))){
                            m.addRole(Snowflake.of(brigadierRoleid)).block();
                            m.removeRole(Snowflake.of(colonelRoleid)).block();
                            beginrole = colonelRoleid;
                            endrole = brigadierRoleid;
                        } else if (m.getRoleIds().contains(Snowflake.of(brigadierRoleid))){
                            m.addRole(Snowflake.of(admiralRoleid)).block();
                            m.removeRole(Snowflake.of(brigadierRoleid)).block();
                            beginrole = brigadierRoleid;
                            endrole = admiralRoleid;
                        }
                        if(!beginrole.isEmpty()){
                           EmbedGenerator.rankUpdateEmbed(g , event.getMessage().getChannel().block(), member , beginrole,endrole);

                        }

                }
                } catch (Exception e){
                    event.getMessage().getChannel().block().createMessage("Invalid input detected look if there are spaces between all names").block();
                    return;
                }
            }
        } else {
            event.getMessage().getChannel().block().createMessage("You don't have access to this command").block();
        }
    }



    /**
     * creates a navigation menu for regular cox
     * @param event
     * @author Gmoley
     * @since 1/07/2021
     * @version 1.0
     */
    private void regcoxembed(MessageCreateEvent event) {
        final MessageChannel channel = event.getMessage().getChannel().block();
        try{
        event.getMessage().delete().block();
        } catch(Exception e){}
        channel.createEmbed(embedCreateSpec ->
                embedCreateSpec.setTitle("COX Navigation")
                        .setColor(Color.ENDEAVOUR)
                        .addField("Combat Rooms:",
                          s.getString("RegGuardians") +
                                s.getString("RegShamans")+
                                  s.getString("RegMuttadiles")+
                                  s.getString("RegMystics")+
                                  s.getString("RegTekton")+
                                  s.getString("RegVanguards")+
                                  s.getString("RegVasa")+
                                  s.getString("RegVespula")
                                ,false )
                        .addField("Puzzle Rooms:",
                                s.getString("RegCrabs")+
                                        s.getString("RegIceDemon")+
                                        s.getString("RegThieving")+
                                        s.getString("RegTightrope")
                                ,false )
                .addField("Olm and extra's:",
                        s.getString("RegOlm")+
                                s.getString("RegResources"),false)
        ).block();
    }

    /**
     * creates a navigation menu for regular tob
     * @param event
     * @author Gmoley
     * @since 1/07/2021
     * @version 1.0
     */
    private void regtobembed(MessageCreateEvent event) {
        final MessageChannel channel = event.getMessage().getChannel().block();
        try{
        event.getMessage().delete().block();
        } catch (Exception e){}
    channel.createEmbed(embedCreateSpec ->
            embedCreateSpec.setColor(Color.ENDEAVOUR)
                    .setTitle("Hardmode TOB Navigation")
                    .addField("Rooms:",
                        s.getString("RegMaiden")+
                                s.getString("RegBloat")+
                                s.getString("RegNylocas")+
                                s.getString("RegSotetseg")+
                                s.getString("RegXarpus")

                    ,false )
            .addField("Verzik:",
                    s.getString("RegP1")+
                            s.getString("RegP2")+
                            s.getString("RegP3")
                    ,false )
    ).block();
    }

    /**
     * creates a navigation menu for hard mode tob
     * @param event
     * @author Gmoley
     * @since 1/07/2021
     * @version 1.0
     */
    private void HMtobembed(MessageCreateEvent event) {
        final MessageChannel channel = event.getMessage().getChannel().block();
        try{
            event.getMessage().delete().block();
        } catch (Exception e){}
        channel.createEmbed(embedCreateSpec ->
                embedCreateSpec.setColor(Color.ENDEAVOUR)
                        .setTitle("TOB Navigation")
                        .addField("Rooms:",
                                s.getString("HMMaiden")+
                                        s.getString("HMBloat")+
                                        s.getString("HMNylocas")+
                                        s.getString("HMSotetseg")+
                                        s.getString("HMXarpus")

                                ,false )
                        .addField("Verzik:",
                                s.getString("HMP1")+
                                        s.getString("HMP2")+
                                        s.getString("HMP3")
                                ,false )
        ).block();
    }

    /**
     * help function for applicationaccept
     * @param author
     * @param targetMention
     * @author Gmoley
     * @since 1/07/2021
     * @version 1.0
     */
    private void applicationDm(Member author, String targetMention){
        author.getPrivateChannel().block().createMessage(
                s.getString("AppDmPart1")+targetMention+s.getString("AppDmPart2")).block();
    }

    /**
     * exodus application handler. removes the applicant role and adds member and recruit.
     * this function can only be used by bot mods (bronze star+ in discord).
     * it will also put the applicant's name in #queue
     * the command user will receive a dm for all things that need to be added (lists/welcome/...)
     * @param event msg by one of the mods
     * @author Gmoley
     * @since 1/07/2021
     * @version 1.0
     */
    private void forceapplicationAccept(MessageCreateEvent event)  {
        final MessageChannel channel = event.getMessage().getChannel().block();
        final Guild g = event.getGuild().block();
        Member author = event.getMessage().getAuthorAsMember().block();
         List<String> command ;
        String targetedUser;
        String targetMention;
        String targetName;
        Boolean ismod;
        try{
            command = Arrays.asList(event.getMessage().getContent().split(" "));
            ismod = functions.isMod(functions.getModRoles(event),event.getMessage().getAuthorAsMember().block().getRoleIds());
            targetedUser = functions.removeSymbols(command.get(1));
            targetMention = g.getMemberById(Snowflake.of(targetedUser)).block().getNicknameMention();
            targetName =  g.getMemberById(Snowflake.of(targetedUser)).block().getDisplayName();
        }

        catch(Exception e){
            return;
        }
        if(ismod && g.getId().asString().equals(guildid) && channel.getId().asString().equals(applicationChannel)){
            try {
                dblearnerApp.removerwcheck();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            Member target =g.getMemberById(Snowflake.of(targetedUser)).block();
            target.removeRole(Snowflake.of(applicantRoleid)).block();
            target.addRole(Snowflake.of(memberRoleid)).block();
            target.addRole(Snowflake.of(recruitRoleid)).block();



            //acceptance messages
            GuildChannel txtchannel = event.getGuild().block().getChannelById(Snowflake.of(queueChannelId)).block();
            TextChannel ch = (TextChannel) txtchannel;
            ch.createMessage("*** "+targetMention+" got accepted to the clan!***").block();
            applicationDm(author,targetName);

            applicationDeny(event);

            //select way message

            EmbedGenerator.pointAddEmbed( target.getPrivateChannel().block(),"Application accepted!","Your application to **Exodus PVM** just got accepted\n" +
                    "One final request before you enjoy the server is to react on the message below\n" +
                    "With that said, we wish you a warm welcome to the clan\n\n The Exodus PVM Staff");
            Message message =   target.getPrivateChannel().block().createMessage("How did you find Exodus PVM?\n" +
                    "*react to the most relatable option* \n\n" +
                    "\uD83C\uDDE6: Friends\n" +
                    "\uD83C\uDDE7: Forums\n" +
                    "\uD83C\uDDE8: In Game\n" +
                    "\uD83C\uDDE9: Other").block();

            try{
                message.addReaction(ReactionEmoji.unicode("\uD83C\uDDE6")).block();
                message.addReaction(ReactionEmoji.unicode("\uD83C\uDDE7")).block();
                message.addReaction(ReactionEmoji.unicode("\uD83C\uDDE8")).block();
                message.addReaction(ReactionEmoji.unicode("\uD83C\uDDE9")).block();
                Sheets.addJoiner(target,"N/A");
            }catch (Exception e){

            }
        }
    }

    public void applicationSetWay(ReactionAddEvent event){
        if (event.getUser().block().isBot()){
            return;
        }
        if(event.getMessage().block().getContent().startsWith("How did you find")){
            String emote = event.getEmoji().asUnicodeEmoji().get().getRaw();
            String way = "N/A";
            if (emote.equals("\uD83C\uDDE6")){
                way = "Friends";
            }else if (emote.equals("\uD83C\uDDE7")){
                way = "Forums";
            }else if (emote.equals("\uD83C\uDDE8")){
                way = "In Game";
            }else if(emote.equals("\uD83C\uDDE9")){
                way = "Other";
            }

            String target = functions.removeSymbols(event.getUser().block().getId().toString());
            Guild  g = event.getUser().block().asMember(Snowflake.of(guildid)).block().getGuild().block();
            Sheets.updateJoiner(event.getUser().block().asMember(Snowflake.of(guildid)).block(),way);
            EmbedGenerator.pointAddEmbed(event.getChannel().block(),"Thanks for your submission!","");

            try{
                event.getMessage().block().delete().block();
            } catch (Exception e){}

        }


    }

    /**
     * removes the tob learner/cox learner role from the user "block" depending on input
     * @param block gives the user that triggered this
     * @param isTob is this cox or tob ?
     * @author Gmoley
     * @since 1/07/2021
     * @version 1.0
     */
    private void removereaction(User block,Boolean isTob) {
        Member m = block.asMember(Snowflake.of(guildid)).block();
        if(isTob){
            m.removeRole(Snowflake.of(tobLearner)).block();
        } else {
            m.removeRole(Snowflake.of(coxLearner)).block();
        }
        return;
    }

    /**
     *  Exodus feature: upon reacting for tob learner/cox learner dm the member that reacted with
     *  a request to give what mode of the desired content
     * @author Gmoley
     * @since 1/07/2021
     * @param event
     * @version 1.0
     */
    public void learnerReaction(ReactionAddEvent event) {
        String messageId = event.getMessage().block().getId().asString();
        String guildId = event.getGuild().block().getId().asString();
        if (guildId.equals(guildid) && messageId.equals(msgid)) {
            if (event.getEmoji().asUnicodeEmoji().isPresent()) { //if it is a basic emote
                //if spider web
                if (event.getEmoji().asUnicodeEmoji().get().getRaw().hashCode() == 55020692) { //tob learner

                    try {
                        dblearnerApp.removeApplicant(event.getUser().block().getId().asString());
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();

                    }
                    event.getUser().block().getPrivateChannel().block().createEmbed(spec -> {
                        spec.setColor(Color.RED)
                                .setTitle("You've requested the TOB Learner role").setUrl("https://discord.com/channels/780579110333841408/780602378989862952")
                                .setDescription("Please fill the form in this dm to complete your application");
                    }).block();
                    Message e = event.getUser().block().getPrivateChannel().block().createMessage("What TOB mode do you want to learn: \n" +
                            "\uD83D\uDD78 : Regular Tob \n" +
                            "\uD83E\uDD87 : Hard mode").block();
                    e.addReaction(ReactionEmoji.unicode("\uD83D\uDD78")).block(); //regular cox
                    e.addReaction(ReactionEmoji.unicode("\uD83E\uDD87")).block(); // challenge mode
                    return ;

                }
            } else { //custom emote
                String emote2 = event.getEmoji().asCustomEmoji().get().getId().asString();
                if (emote2.equals("782895667885375499")) {
                    try {
                        dblearnerApp.removeApplicant(event.getUser().block().getId().asString());
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();

                    }
                    event.getUser().block().getPrivateChannel().block().createEmbed(spec -> {
                        spec.setColor(Color.GREEN)
                                .setTitle("You've requested the the COX Learner role").setUrl("https://discord.com/channels/780579110333841408/780602378989862952")
                                .setDescription("Please fill the form in this dm to complete your application");
                    }).block();
                    Message e =  event.getUser().block().getPrivateChannel().block().createMessage("What COX mode do you want to learn: \n" +
                            "\uD83D\uDC09 : Regular Cox \n" +
                            "\uD83D\uDC32 : Challenge mode").block();
                    e.addReaction(ReactionEmoji.unicode("\uD83D\uDC09")).block(); //regular cox
                    e.addReaction(ReactionEmoji.unicode("\uD83D\uDC32")).block(); // challenge mode
                    return ;

                }
            }

        }
    }

    /**
     * asks the user what it's kc is in the previously selected content. if is tob and kc ==0 then it will ask if (s)he made it to verzik
     * @param event
     * @author Gmoley
     * @since 1/07/2021
     * @version 1.0
     */
    public void learnerapp(MessageCreateEvent event) {
        String botmessage  = event.getMessage().getChannel().block().getMessagesBefore(event.getMessage().getId()).blockFirst().getContent();
        String usermsg = event.getMessage().getContent();
        //initial bot msg
        //!todo [code 1.1] check if needed
        if (botmessage.equals("cox")){
            System.out.println("coxtest");
            Message e =  event.getMessage().getChannel().block().createMessage("What COX mode do you want to learn: \n" +
                  "\uD83D\uDC09 : Regular Cox \n" +
                  "\uD83D\uDC32 : Challenge mode").block();
          e.addReaction(ReactionEmoji.unicode("\uD83D\uDC09")).block(); //regular cox
          e.addReaction(ReactionEmoji.unicode("\uD83D\uDC32")).block(); // challenge mode
            return ;
        }
        //!todo [code 1.2] check if needed
        else if (botmessage.equals("tob")){
            System.out.println("tobtest");
            Message e =  event.getMessage().getChannel().block().createMessage("What TOB mode do you want to learn: \n" +
                    "\uD83D\uDD78 : Regular Tob \n" +
                    "\uD83E\uDD87 : Hard mode").block();
            e.addReaction(ReactionEmoji.unicode("\uD83D\uDD78")).block(); //regular cox
            e.addReaction(ReactionEmoji.unicode("\uD83E\uDD87")).block(); // challenge mode
            return ;
        }
        else if (botmessage.contains("What is your kc?")){
            try{
               Integer i = Integer.parseInt(usermsg);
               dblearnerApp.updateKc(functions.removeSymbols(event.getMessage().getAuthor().get().getId().asString()), i);
               String mode =  dblearnerApp.getMode(functions.removeSymbols(event.getMessage().getAuthor().get().getId().asString()));
              if(mode.equals("HM") || mode.equals("TOB")){
                  if (i >0){
                    dblearnerApp.updateVerzikver(functions.removeSymbols(event.getMessage().getAuthor().get().getId().asString()),"Yes");
                    pushapp(event.getMessage().getAuthor().get());
                  }else{

                      Message e  = event.getMessage().getChannel().block().createMessage("Have you made it to verzik in this mode?\n" +
                              "\uD83D\uDC4D : Yes \n" +
                              "\uD83D\uDC4E : No\n").block();

                      e.addReaction(ReactionEmoji.unicode("\uD83D\uDC4D")).block();
                      e.addReaction(ReactionEmoji.unicode("\uD83D\uDC4E")).block();

                      return ;
                  }
              }else {
                  pushapp(event.getMessage().getAuthor().get());

                  return ;
              }

            } catch (Exception e){
                e.printStackTrace();
                event.getMessage().getChannel().block().createMessage("Wrong input, Remember to ONLY put in a number").block();
                event.getMessage().getChannel().block().
            createMessage("What is your kc? ***ONLY*** put the number of the mode in.").block();
                return;
            }
        }
    }

    /**
     * push the application filled by previous function triggers in this class
     * this means:
     *      delete user from database
     *      add him to the mentor sheet
     *      add him to public sheet
     *      send confirm msg to user
     *      send application notice to mentors
     * @param member user that triggered function
     * @author Gmoley
     * @since 1/07/2021
     * @version 1.0
     */
    private void pushapp(User member) {
        //push the application
        String mode = "";
        String rsn = "";
        String verzikVer ="";
        Integer kc = 0;
        //gain the data

        try {
            mode = dblearnerApp.getMode(functions.removeSymbols(member.getId().asString()));
            rsn = member.asMember(Snowflake.of(guildid)).block().getDisplayName();
            kc = dblearnerApp.getkc(functions.removeSymbols(member.getId().asString()));
            verzikVer = dblearnerApp.verified(functions.removeSymbols(member.getId().asString()));

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return;
        }

        //set in sheet
        if (mode.equals("TOB")) {
            try {
                member.asMember(Snowflake.of(guildid)).block().addRole(Snowflake.of(tobLearner)).block();
                Sheets.setRegTob(rsn, kc.toString(),verzikVer);

            } catch (Exception e) {
                e.printStackTrace();
                return;

            }
        }else if(mode.equals("HM")){
            try {
                member.asMember(Snowflake.of(guildid)).block().addRole(Snowflake.of(tobLearner)).block();
                Sheets.setHmTob(rsn, kc.toString(),verzikVer);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }

        else if(mode.equals("COX")){
            try {
                member.asMember(Snowflake.of(guildid)).block().addRole(Snowflake.of(coxLearner)).block();
            Sheets.setRegCox(rsn, kc.toString());
        } catch (Exception e) {
            e.printStackTrace();
                return;

        }
        }
        else if(mode.equals("CM")){
            try {
                member.asMember(Snowflake.of(guildid)).block().addRole(Snowflake.of(coxLearner)).block();
                Sheets.setCmCox(rsn, kc.toString());
            } catch (Exception e) {
                e.printStackTrace();
                return;

            }
        }
        TextChannel ch = (TextChannel) member.asMember(Snowflake.of(guildid)).block().getGuild().block().getChannelById(Snowflake.of(learnerUpdateChannel)).block();
        ch.createMessage(member.asMember(Snowflake.of(guildid)).block().getNicknameMention()+" Applied to the "+ mode + " list!").block();
        //delete the data
        try {
            dblearnerApp.updateGuideCheck(functions.removeSymbols(member.getMention()), "No");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return;
        }
        //"Thanks for your submission.\n The mentors have received it correctly \n feel free to ask for raids in wanting to learn"
        member.getPrivateChannel().block().createEmbed(s ->{
            s.setTitle("Thanks for your submission!")
                    .setColor(Color.GREEN)

                    .setUrl("https://discord.com/channels/780579110333841408/780602378989862952")
                    .setDescription("The mentors have received it correctly \n Please remember to use wanting to learn to ask for mentor raids!");
        }).block();
    }

    /**
     * checks the msg that was reacted to, divides from there to a other function of this class.
     * the do you want to learn trigger however is in this function.
     * it will check what mode of the desired content was reacted to and prompt a guide msg for it.
     * this will create a new applicant in the database to track it's filled data.
     * @param event reaction by a user

     */
    public void learnerappReaction(ReactionAddEvent event){

        String raw = event.getEmoji().asUnicodeEmoji().get().getRaw();
        //if the question is on wanting to learn
            //what to learn
            if (event.getMessage().block().getContent().contains("do you want to learn:") ){
                if (raw.equals("\uD83D\uDC09")){
                    //regular cox
                    try{
                    dblearnerApp.newApplicant(functions.removeSymbols(event.getUser().block().getMention()),"COX");
                    coxApplicant(event, false);

                    } catch (Exception e){
                        return;
                    }
                }else if (raw.equals("\uD83D\uDC32")){
                    //challenge mode
                    try{
                        dblearnerApp.newApplicant(functions.removeSymbols(event.getUser().block().getMention()),"CM");
                        coxApplicant(event, true);

                    } catch (Exception e){
                        return;
                    }
                }
                else if (raw.equals("\uD83D\uDD78")){
                    //regular tob
                    try{
                        dblearnerApp.newApplicant(functions.removeSymbols(event.getUser().block().getMention()),"TOB");
                        tobApplicant(event, false);

                    } catch (Exception e){
                        return;
                    }
                }else if (raw.equals("\uD83E\uDD87")){
                    //Hard mode
                    try{
                        dblearnerApp.newApplicant(functions.removeSymbols(event.getUser().block().getMention()),"HM");
                        tobApplicant(event, true);

                    } catch (Exception e){
                        return;
                    }
                }
            }
            //guide check
            else if(event.getMessage().block().getContent().startsWith("Have you checked the")){
                guidecheck(event);

            }
            //gear check
            else if (event.getMessage().block().getContent().startsWith("Do you own all the required gear")){
                gearCheck(event);

            }
            //verzik verify
            else if (event.getMessage().block().getContent().contains("Have you made it to verzik")){
                if(event.getEmoji().asUnicodeEmoji().get().getRaw().equals("\uD83D\uDC4D")){
                    try {
                        dblearnerApp.updateVerzikver(functions.removeSymbols(event.getUser().block().getId().asString()), "Yes");

                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
                else {
                    try {
                        dblearnerApp.updateVerzikver(functions.removeSymbols(event.getUser().block().getId().asString()), "No");

                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
                pushapp(event.getUser().block());
            }
        try{
            event.getMessage().block().delete().block();
        } catch (Exception e){}


    }

    /**
     * removes role when reaction is deleted
     * @param event
     * @throws SQLException
     * @author Gmoley
     * @since 21/07/2021
     * @version 1.0
     */
    public void learnerRemoveApp(ReactionRemoveEvent event) throws SQLException {
        String messageId = event.getMessage().block().getId().asString();
        String guildId = event.getGuild().block().getId().asString();
        if (guildId.equals(guildid) && messageId.equals(msgid)) {
            //if spider web
            if (event.getEmoji().asUnicodeEmoji().get().getRaw().hashCode() == 55020692) { //tob learner
                dblearnerApp.removeApplicant(event.getUser().block().getId().asString());
                if (event.getUser().block().asMember(Snowflake.of(guildid)).block().getRoleIds().contains(Snowflake.of(tobLearner))) {
                    event.getUser().block().asMember(Snowflake.of(guildid)).block().removeRole(Snowflake.of(tobLearner)).block();
                    event.getUser().block().getPrivateChannel().block().createMessage("Tob learner role removed").block();
                }
            }
        }
        else { //custom emote
            //if dex
            String emote2 = event.getEmoji().asCustomEmoji().get().getId().asString();
            if (emote2.equals("782895667885375499")) {
                dblearnerApp.removeApplicant(event.getUser().block().getId().asString());
                if (event.getUser().block().asMember(Snowflake.of(guildid)).block().getRoleIds().contains(Snowflake.of(coxLearner))){
                    event.getUser().block().asMember(Snowflake.of(guildid)).block().removeRole(Snowflake.of(coxLearner)).block();
                    event.getUser().block().getPrivateChannel().block().createMessage("Cox learner role removed").block();
                }
            }
        }
    }

    /**
     * views reaction on guide check msg.
     * if yes continue to gear check else delete user from database and request a restart.
     * @param event user passed by learnerappreaction
     * @author Gmoley
     * @since 1/07/2021
     * @version 1.0
     */
    private void guidecheck(ReactionAddEvent event) {
        String check;
        String raw = event.getEmoji().asUnicodeEmoji().get().getRaw();
        if (event.getMessage().block().getAuthor().get().isBot()){
            if (raw.equals("\uD83D\uDC4D")){
                check= "Yes";
            }else {
                check = "No";
            }
        if(event.getMessage().block().getContent().contains("TOB Hard mode guide")){

            try{
                dblearnerApp.updateGuideCheck(functions.removeSymbols(event.getUser().block().getMention()),check);
            }catch (Exception e){
                return;
            }
            if (check.equals("No")){
                event.getChannel().block().createMessage("Please check the guides then reapply in reaction roles").block();
                removereaction(event.getUser().block(),true);
            }else{
                Message e =
                        event.getChannel().block().createMessage("Do you own all the required gear aswell as 94 Magic ?\n" +
                                "\uD83D\uDC4D : Yes \n" +
                                "\uD83D\uDC4E : No\n" ).block();
                e.addReaction(ReactionEmoji.unicode("\uD83D\uDC4D")).block();
                e.addReaction(ReactionEmoji.unicode("\uD83D\uDC4E")).block();
                return ;
            }
        }
        else if(event.getMessage().block().getContent().contains("TOB guide")){
            try{
                dblearnerApp.updateGuideCheck(functions.removeSymbols(event.getUser().block().getMention()),check);
            }catch (Exception e){
                return;
            }
            if (check.equals("No")){
                event.getChannel().block().createMessage("Please check the guides then reapply in reaction roles").block();
                removereaction(event.getUser().block(),true);
            }else{
                Message e = event.getChannel().block().createMessage("Do you own all the required gear aswell as 94 Magic ?\n" +
                        "\uD83D\uDC4D : Yes \n" +
                        "\uD83D\uDC4E : No\n" ).block();
                e.addReaction(ReactionEmoji.unicode("\uD83D\uDC4D")).block();
                e.addReaction(ReactionEmoji.unicode("\uD83D\uDC4E")).block();
                return ;
            }
        }
        else if(event.getMessage().block().getContent().contains("COX Challenge mode guide")){
            try{
                dblearnerApp.updateGuideCheck(functions.removeSymbols(event.getUser().block().getMention()),check);
            }catch (Exception e){
                return;
            }
            if (check.equals("No")){
                event.getChannel().block().createMessage("Please check the guides then reapply in reaction roles").block();
                removereaction(event.getUser().block(),false);
            }else{
                Message e =event.getChannel().block().createMessage("Do you own all the required gear aswell as 78 Herblore ?\n" +
                        "\uD83D\uDC4D : Yes \n" +
                        "\uD83D\uDC4E : No\n" ).block();
                e.addReaction(ReactionEmoji.unicode("\uD83D\uDC4D")).block();
                e.addReaction(ReactionEmoji.unicode("\uD83D\uDC4E")).block();
                return ;
            }

        }else if(event.getMessage().block().getContent().contains("COX guide")) {
            try {
                dblearnerApp.updateGuideCheck(functions.removeSymbols(event.getUser().block().getMention()), check);
            } catch (Exception e) {
                return;
            }
            if (check.equals("No")) {
                event.getChannel().block().createMessage("Please check the guides then reapply in reaction roles").block();
                removereaction(event.getUser().block(), false);
            } else {
                Message e = event.getChannel().block().createMessage("Do you own all the required gear aswell as 78 Herblore ?\n" +
                        "\uD83D\uDC4D : Yes \n" +
                        "\uD83D\uDC4E : No\n").block();
                e.addReaction(ReactionEmoji.unicode("\uD83D\uDC4D")).block();
                e.addReaction(ReactionEmoji.unicode("\uD83D\uDC4E")).block();
                return ;
            }
        }
        }
    }

    /**
     * prompt a guide check msg depending on the requested tob mode
     * @param event reaction by user
     * @param cm is it challenge mode (hard mode)
     * @author Gmoley
     * @since 1/07/2021
     * @version 1.0
     */
    private void tobApplicant(ReactionAddEvent event, boolean cm) {
        Message e;
        if (!cm){
        e = event.getChannel().block().createMessage("Have you checked the TOB guide?\n" +
                "\uD83D\uDC4D : Yes \n" +
                "\uD83D\uDC4E : No\n" +
                "this can be found here: https://discord.com/channels/780579110333841408/780671716316807188/780677895622230036").block();
        } else{
          e =  event.getChannel().block().createMessage("Have you checked the TOB Hard mode guide?\n" +
                    "\uD83D\uDC4D : Yes \n" +
                    "\uD83D\uDC4E : No" +
                  "\n this can be found here: https://discord.com/channels/780579110333841408/858448815715516447/858698744622219264").block();
        }
        e.addReaction(ReactionEmoji.unicode("\uD83D\uDC4D")).block();
        e.addReaction(ReactionEmoji.unicode("\uD83D\uDC4E")).block();
    }

    /**
     * prompt a guide check msg depending on the requested cox mode
     * @param event reaction by user
     * @param cm is it challenge mode (hard mode)
     * @author Gmoley
     * @since 1/07/2021
     * @version 1.0
     */
    private void coxApplicant(ReactionAddEvent event, boolean cm) {
        Message e;
        if (!cm){
          e =  event.getChannel().block().createMessage("Have you checked the COX guide?\n" +
                    "\uD83D\uDC4D : Yes \n" +
                    "\uD83D\uDC4E : No" +
                  "\n this can be found here: https://discord.com/channels/780579110333841408/780671690434412554/780677480620752916").block();
        } else{
           e = event.getChannel().block().createMessage("Have you checked the COX Challenge mode guide?\n" +
                    "\uD83D\uDC4D : Yes \n" +
                    "\uD83D\uDC4E : No" +
                   "\n this can be found here: https://discord.com/channels/780579110333841408/780681034177970247/839504445012574278").block();
        }
        e.addReaction(ReactionEmoji.unicode("\uD83D\uDC4D")).block();
        e.addReaction(ReactionEmoji.unicode("\uD83D\uDC4E")).block();
    }

    /**
     * views reaction on gearcheck will ask the kc if true,
     * else it'll delete the user from db and request a restart
     * @param event reaction by user
     * @author Gmoley
     * @since 1/07/2021
     * @version 1.0
     */
    private void gearCheck(ReactionAddEvent event)  {
        String raw = event.getEmoji().asUnicodeEmoji().get().getRaw();
        if (raw.equals("\uD83D\uDC4E")){

            try {
                dblearnerApp.updateGuideCheck(functions.removeSymbols(event.getUser().block().getMention()),"No");
                event.getChannel().block().createMessage("Please check the required gear then reapply in reaction roles").block();
                return;
            } catch (SQLException throwables) {
                return;
            }
        }
        try {
            dblearnerApp.updateGearCheck(functions.removeSymbols(event.getUser().block().getMention()));
        } catch (SQLException throwables) {
            return;
        }
        if(event.getMessage().block().getContent().contains("Do you own all the required gear aswell as 78 Herblore ?")){
          event.getChannel().block().createMessage("What is your kc? ***ONLY*** put the number of the mode in.").block();
        }
        else if (event.getMessage().block().getContent().contains("Do you own all the required gear aswell as 94 Magic ?")){
             event.getChannel().block().createMessage("What is your kc? ***ONLY*** put the number of the mode in.").block();
        }

    }

    /**
     *  Check if the given member has Star Permissions in the Exodus server
     * @param member
     * @return
     */
    public boolean hasStarPerms(Member member) {
        boolean status = member.getRoleIds().contains(Snowflake.of(starRoleId));
          return status;
    }


}
