package Model.Commandsets;

import Database.DbCommandsets.*;

import Model.*;
import Model.Functionality.EmbedGenerator;
import Model.Functionality.Functions;
import Model.Interfaces.Command;

import Model.Interfaces.CommandSet;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.rest.util.Color;

import java.time.Instant;
import java.util.*;

public class Strikes implements CommandSet {
    private static final Map<String, Command> set = new HashMap<>();
    private Functions functions = Main.functions;
    private String prefix = Main.prefix;
    private ResourceBundle txt = Main.txt;
    private DbStrikes db;
    private EmbedGenerator embedGenerator = Main.embedGenerator;

    public Map<String, Command> getCommandset(Map<String, Command> commands){
        commands.putAll(set);
        return commands;
    }

    public Strikes() {
        this.db = Main.db.strikes();
        set.put("hstrike", event -> { commandHelp(event);});
        set.put("strike", event ->{addStrike(event);});
        set.put("cstrike", event ->{checkStrike(event);});
        set.put("rmstrike", event -> {removeStrike(event);});
    }

    public void commandHelp(MessageCreateEvent event) {
        final MessageChannel channel = event.getMessage().getChannel().block();
        ArrayList<String> titles =new ArrayList<>();
        ArrayList<String> htu = new ArrayList<>();
        titles.add(prefix+"strike"); htu.add("Add a strike to a user \n HTU: "+prefix+"strike @member message");
        titles.add(prefix+"cstrike"); htu.add("Checks the strikes of user \n HTU: "+prefix+"cstrike @member*");
        titles.add(prefix+"rmstrike"); htu.add("remove a strike to a user \n HTU: "+prefix+"rmstrike @member messageOfStrike");

        EmbedGenerator.commandListEmbed(channel,"Strike Helper", "List of strike functions \n\t * optional field \n",titles,htu);


    }

    /**
     * Checks the strikes for a user
     * if command only -> sends embed of Author strikes
     * if target specified -> sends embed of Target strikes
     * @author Gmoley
     * @param event gives detected discord msg
     * @since 14/April/2021
     * @version 1.0
     */
    public void checkStrike(MessageCreateEvent event){
        final MessageChannel channel = event.getMessage().getChannel().block();
        final List<String> command = Arrays.asList(event.getMessage().getContent().split(" "));
        String user;
        String serverid =  event.getGuild().block().getId().asString();
        ArrayList<String> strikes;
        try{
        if (command.size() == 1){
            user = event.getMessage().getAuthor().get().getId().asString();

             strikes = db.checkStrikes(serverid , user);
        }else{
           user = command.get(1);
             strikes = db.checkStrikes(serverid , functions.removeSymbols(user));
        }
           String description = new String();
            int i = 1;
            for (String s : strikes){
                description = description+ i+")   "+ s + "\n";
                i++;
            }
            if (description.equals("")){
                description = "This user has no strikes";
            }
            String finalDescription = description;

            channel.createEmbed(spec ->
                    spec.setColor(Color.BLUE)


                            .addField("Strikes", finalDescription, false)

                            .setTimestamp(Instant.now())).block();

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * removes strike for a user
     * requires a target and the msg of a strike
     * this will remove the targeted strike from the Database
     * Feature only available to roles with permission!
     * @author Gmoley
     * @param event gives detected discord msg
     * @since 14/April/2021
     * @version 1.0
     */
    public void removeStrike(MessageCreateEvent event) {
        final MessageChannel channel = event.getMessage().getChannel().block();
        final List<String> command = Arrays.asList(event.getMessage().getContent().split(" "));
        String serverid =  event.getGuild().block().getId().asString();
        String user = command.get(1);
        String message = functions.getmsg(command);
        boolean validTarget =  functions.validuser(event, user);
        boolean validOperator = functions.validOperator(event);
        try {
            if (validTarget && validOperator) {

                String result = strikeEdit(user, message, true);

                if(db.removeStrike(serverid,functions.removeSymbols(user),message)){
                    embedGenerator.pointAddEmbed(channel,  "Strike removed",  result);
                }else{
                    embedGenerator.errorEmbed(channel,"ERROR","Error: Data didn't write to database \n try again later \n or contact the bot dev" );

                }
            } else if (!validOperator) {
                embedGenerator.errorEmbed(channel,txt.getString("inv user title"),txt.getString("no perm txt"));

            } else if (!validTarget) {
                embedGenerator.errorEmbed(channel,"ERROR","Invalid target");

            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Adds strike for a user
     * requires a target and the msg for the strike
     * this will add the strike to the Database
     * Feature only available to roles with permission!
     * @author Gmoley
     * @param event gives detected discord msg
     * @since 14/April/2021
     * @version 1.0
     */
    public void addStrike(MessageCreateEvent event) {
        final MessageChannel channel = event.getMessage().getChannel().block();
        final List<String> command = Arrays.asList(event.getMessage().getContent().split(" "));
        final Guild g = event.getGuild().block();
        String serverid =  g.getId().asString();
        String user = command.get(1);
        String message = functions.getmsg(command);
        boolean validTarget =  functions.validuser(event, user);
        boolean validOperator = functions.validOperator(event);
        int targetSameAsOperator = functions.targetSameAsOperator(event,user);

        try {
            if (targetSameAsOperator ==1) {
                embedGenerator.errorEmbed(channel,"ERROR","Don't u add strikes to yourself!!! \n And they call me a venny\n might aswell go 'jajaja' now" );

            } else if(targetSameAsOperator ==2){
                embedGenerator.errorEmbed(channel, "Invalid input", "check the target\n (it must be a ping of the target)");
            }

            else if (validTarget && validOperator) {

                String result = strikeEdit(user, message, false);
              if(db.addStrike(serverid, functions.removeSymbols(user), message)){
                  embedGenerator.pointAddEmbed(channel,  "Striked",  result);

              }else{
                  embedGenerator.errorEmbed(channel,"ERROR","Error: Data didn't write to database \n try again later \n or contact the bot dev" );

              }
            } else if (!validOperator) {
                embedGenerator.errorEmbed(channel,txt.getString("inv user title"),txt.getString("no perm txt"));
            } else if (!validTarget) {
                embedGenerator.errorEmbed(channel,"ERROR","Invalid target");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static String strikeEdit(String user, String amount, boolean remove){

        if (remove){
            return   "removed: "+ amount + " from "+ user + "\n";
        }

        else{
            return "added: "+ amount + " to "+ user + "\n";
        }
    }

}
