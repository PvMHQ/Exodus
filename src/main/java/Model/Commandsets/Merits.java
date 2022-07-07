package Model.Commandsets;

import Database.DbCommandsets.DbMerits;
import Model.Functionality.*;
import Model.Interfaces.Command;
import Model.Interfaces.CommandSet;
import Model.Main;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.rest.util.Color;

import java.time.Instant;
import java.util.*;




public class Merits implements CommandSet {
    private static final Map<String, Command> set = new HashMap<>();
    private static Functions functions = Main.functions;
    private String prefix = Main.prefix;
    private ResourceBundle txt = Main.txt;
    private DbMerits db;
    private EmbedGenerator embedGenerator = Main.embedGenerator;
    public Map<String, Command> getCommandset(Map<String, Command> commands){
        commands.putAll(set);
        return commands;
    }
    
    public void commandHelp(MessageCreateEvent event) {
        final MessageChannel channel = event.getMessage().getChannel().block();
        ArrayList<String> titles =new ArrayList<>();
        ArrayList<String> htu = new ArrayList<>();
        titles.add(prefix+"merit"); htu.add("Add a merit to a user \n HTU: "+prefix+"merit @member message ");
        titles.add(prefix+"cmerit"); htu.add("Checks the merits of user \n HTU: "+prefix+"cmerit @member*");
        titles.add(prefix+"rmmerit"); htu.add("remove a merit to a user \n HTU: "+prefix+"rmmerit @member messageOfStrike ");
        EmbedGenerator.commandListEmbed(channel,"Merit Helper", "List of merit functions \n\t * optional field \n",titles,htu);
    }

    public Merits() {
        this.db = Main.db.merits();
        set.put("hmerit", this::commandHelp);
        set.put("merit", event ->{addMerit(event);});
        set.put("rmmerit", event -> {removeMerit(event);});
        set.put("cmerit", event -> {checkMerit(event);});
    }

    /**
     * Checks the merits for a user
     * if command only -> sends embed of Author merits
     * if target specified -> sends embed of Target merits
     * @author Gmoley
     * @param event gives detected discord msg
     * @since 14/April/2021
     * @version 1.0
     */
    public void checkMerit(MessageCreateEvent event){
        final MessageChannel channel = event.getMessage().getChannel().block();
        final List<String> command = Arrays.asList(event.getMessage().getContent().split(" "));
        String user;
        String serverid =  event.getGuild().block().getId().asString();
        ArrayList<String> strikes;
        try{
            if (command.size() == 1){
                user = event.getMessage().getAuthor().get().getId().asString();

                strikes = db.checkMerits(serverid , user);
            }else{
                user = command.get(1);
                strikes = db.checkMerits(serverid , functions.removeSymbols(user));
            }
            String description = new String();
            int i = 1;
            for (String s : strikes){
                description = description+ i+")   "+ s + "\n";
                i++;
            }
            if (description.equals("")){
                description = "This user has no merits :(";
            }
            String finalDescription = description;

            channel.createEmbed(spec ->
                    spec.setColor(Color.BLUE)


                            .addField("merits", finalDescription, false)

                            .setTimestamp(Instant.now())).block();

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * removes merit for a user
     * requires a target and the msg of a merit
     * this will remove the targeted merit from the Database
     * Feature only available to roles with permission!
     * @author Gmoley
     * @param event gives detected discord msg
     * @since 14/April/2021
     * @version 1.0
     */
    public void removeMerit(MessageCreateEvent event) {
        final MessageChannel channel = event.getMessage().getChannel().block();
        final List<String> command = Arrays.asList(event.getMessage().getContent().split(" "));
        final Guild g = event.getGuild().block();
        String serverid = g.getId().asString();
        String user;
        String message;
        try{
         user = command.get(1);
         message = functions.getmsg(command);
        } catch (ArrayIndexOutOfBoundsException e){
            embedGenerator.errorEmbed(channel, txt.getString("inv user title"), "Check arguments of the command");
            return;
        }
        boolean validTarget =  functions.validuser(event, user);
        boolean validOperator = functions.validOperator(event);
        try {
            if (validTarget && validOperator) {
                String result = meritEdit(user, message, true);
                if (db.removeMerit(serverid, functions.removeSymbols(user), message)) {
                    embedGenerator.pointAddEmbed(channel, "Merit removed", result);
                }
            } else if (!validOperator) {
                embedGenerator.errorEmbed(channel, "ERROR", "You don't have permission to use this command");
            } else if (!validTarget) {
                embedGenerator.errorEmbed(channel, "ERROR", "Invalid target");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Adds merit for a user
     * requires a target and the msg for the merit
     * this will add the merit to the Database
     * Feature only available to roles with permission!
     * @author Gmoley
     * @param event gives detected discord msg
     * @since 14/April/2021
     * @version 1.0
     */
    public void addMerit(MessageCreateEvent event) {
        final MessageChannel channel = event.getMessage().getChannel().block();
        final List<String> command = Arrays.asList(event.getMessage().getContent().split(" "));
        final Guild g = event.getGuild().block();

        String serverid = g.getId().asString();
        String user;
        String message;

        try{
            user = command.get(1);
            message =  functions.getmsg(command);
        } catch (ArrayIndexOutOfBoundsException e){
            embedGenerator.errorEmbed(channel, txt.getString("inv user title"), "Check arguments of the command");
            return;
        }
        boolean validTarget = functions.validuser(event, user);
        boolean validOperator = functions.validOperator(event);
        int targetSameAsOperator = functions.targetSameAsOperator(event, user);

        try {
            if (targetSameAsOperator == 1) {
                embedGenerator.errorEmbed(channel, "ERROR", "BITCH, Don't u add merits to yourself \n And they call me a venny\n might aswell go 'jajaja' now");
            } else if(targetSameAsOperator ==2){
                embedGenerator.errorEmbed(channel, "Invalid Input!", "check the target \n(it must be a ping of the target)");
            }
            else if (validTarget && validOperator) {
                String result = meritEdit(user, message, false);
                if (db.addMerit(serverid, functions.removeSymbols(user), message)) {
                    embedGenerator.pointAddEmbed(channel,  "Merit Added",  result);
                }
            } else if (!validOperator) {
                embedGenerator.errorEmbed(channel, "ERROR", "You don't have permission to use this command");

            } else if (!validTarget) {
                embedGenerator.errorEmbed(channel, "ERROR", "Invalid target");
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public static String meritEdit(String user, String amount, boolean remove){

        if (remove){
            return   "removed '"+ amount + "' from "+ user + "\n";
        }

        else{
            return "added '"+ amount + "' to "+ user + "\n";
        }
    }

}
