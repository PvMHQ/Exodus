package Model.Commandsets;

import Database.DbCommandsets.DbRolesettings;
import Model.Functionality.EmbedGenerator;
import Model.Functionality.Functions;
import Model.Interfaces.Command;
import Model.Interfaces.CommandSet;
import Model.Main;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.channel.MessageChannel;

import java.util.*;

public class RoleSettings implements CommandSet {
    private static final Map<String, Command> set = new HashMap<>();
    private Functions functions = Main.functions;
    private DbRolesettings db;
    private ResourceBundle txt = Main.txt;
    private String prefix = Main.prefix;
    public Map<String, Command> getCommandset(Map<String, Command> commands){
        commands.putAll(set);
        return commands;
    }

    public void commandHelp(MessageCreateEvent event) {
        final MessageChannel channel = event.getMessage().getChannel().block();
        ArrayList<String> titles =new ArrayList<>();
        ArrayList<String> htu = new ArrayList<>();
        titles.add(prefix+"add-admin"); htu.add("Gives a role admin bot permissions \n HTU: "+prefix+"add-admin @role");
        titles.add(prefix+"check-admin"); htu.add("Shows the roles which have at least admin bot permissions \n HTU: "+prefix+"check-admin");
        titles.add(prefix+"remove-admin"); htu.add("Removes admin bot permissions from role \n HTU: "+prefix+"remove-admin @role");
        titles.add(prefix+"add-mod"); htu.add("Gives a role moderator bot permissions \n HTU: "+prefix+"add-mod @role");
        titles.add(prefix+"check-mod"); htu.add("Shows the roles which have at least moderator bot permissions \n HTU: "+prefix+"check-mod");
        titles.add(prefix+"remove-mod"); htu.add("Removes moderator bot permissions from role \n HTU: "+prefix+"remove-admin @role");

        EmbedGenerator.commandListEmbed(channel,"Roles Helper", "List of role functions \n\t * optional field \n",titles,htu);

    }

    public RoleSettings() {
        this.db = Main.db.serversettings();
        set.put("hroles", event -> {commandHelp(event);});
        set.put("add-admin",event -> {addAdmin(event);});
        set.put("check-admin",event -> {checkAdmins(event);});
        set.put("remove-admin",event -> {removeAdmin(event);});
        set.put("add-mod",event -> {addModerators(event);});
        set.put("check-mod",event -> {checkModerators(event);});
        set.put("remove-mod",event -> {removeModerators(event);});;

    }

    /**
     *  checks the admins of a guild
     * @author Gmoley
     * @param event gives detected discord msg
     * @since 9/may/2021
     * @version 1.0
     */
    public void checkAdmins(MessageCreateEvent event){
        final MessageChannel channel = event.getMessage().getChannel().block();
        final Guild guild = event.getGuild().block();
        try {
            ArrayList<String> currentroleIds = db.getAdminsOfServer(guild.getId().asString());
            if (currentroleIds.size() == 0){
                EmbedGenerator.infoEmbed(channel,"No Admins Found", "This server has no roles assigned to admins");
            }else{
                int i = 0;
                for (String s : currentroleIds){
                    s = "<@&"+s+">";
                    currentroleIds.set(i,s );
                    i++;
                }

                EmbedGenerator.listEmbed( channel,  "List of admin roles",currentroleIds);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     *  adds a admin role to a guild
     * @author Gmoley
     * @param event gives detected discord msg
     * @since 9/may/2021
     * @version 1.0
     */
    public void addAdmin(MessageCreateEvent event){
        final MessageChannel channel = event.getMessage().getChannel().block();
        final Guild guild = event.getGuild().block();
        final List<String> command = Arrays.asList(event.getMessage().getContent().split(" "));
        String role;

        try{
            role = functions.removeSymbols(command.get(1));

        }catch (Exception e){
            EmbedGenerator.errorEmbed(channel, txt.getString("inv user title"), "Check arguments of the command");
            return;
        }
        try {
            boolean isAdmin;
            ArrayList<String> currentroleIds = db.getAdminsOfServer(guild.getId().asString());
            if (currentroleIds.contains(role)){
                EmbedGenerator.errorEmbed(channel, txt.getString("inv user title"), "This role is already assigned to this permission");
                return;
            }
            if (currentroleIds.size() != 0){
            //check if valid role and add it to admin roles
                 isAdmin = Functions.isAdmin( currentroleIds,event.getMessage().getAuthorAsMember().block().getRoleIds());

            } else {
                isAdmin = true;
            }
                //check  add
            if (isAdmin){
                db.addAdmin(guild.getId().asString(),role);
                EmbedGenerator.pointAddEmbed(channel,"Admin added","Added: "+ command.get(1) +" To the list of admin roles");
            } else {
                EmbedGenerator.errorEmbed(channel,txt.getString("inv user title"), txt.getString("no perm txt"));
            }

        } catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     *  removes an admin role from a guild
     * @author Gmoley
     * @param event gives detected discord msg
     * @since 9/may/2021
     * @version 1.0
     */
    private void removeAdmin(MessageCreateEvent event){
        final MessageChannel channel = event.getMessage().getChannel().block();
        final Guild guild = event.getGuild().block();
        final List<String> command = Arrays.asList(event.getMessage().getContent().split(" "));
        String role;

        try{
            role = functions.removeSymbols(command.get(1));

        }catch (Exception e){
            EmbedGenerator.errorEmbed(channel, txt.getString("inv user title"), "Check arguments of the command");
            return;
        }try {
            boolean isAdmin = false;
            ArrayList<String> currentroleIds = db.getAdminsOfServer(guild.getId().asString());
            if (!currentroleIds.contains(role)){
                EmbedGenerator.errorEmbed(channel, txt.getString("inv user title"), "This role isn't assigned to this permission");
                return;
            }
            if (currentroleIds.size() != 0){
                //check if valid role and add it to admin roles
                isAdmin = Functions.isAdmin( currentroleIds,event.getMessage().getAuthorAsMember().block().getRoleIds());

            }
            //check  add
            if (isAdmin){
                db.removeAdmin(guild.getId().asString(),role);
                EmbedGenerator.pointAddEmbed(channel,"Admin removed","removed: "+ command.get(1) +" From the list of admin roles");
            } else {
                EmbedGenerator.errorEmbed(channel,txt.getString("inv user title"), "You do not have permission to use this command");
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     *  checks the mods of a guild
     * @author Gmoley
     * @param event gives detected discord msg
     * @since 9/may/2021
     * @version 1.0
     */
    private void checkModerators(MessageCreateEvent event) {
        final MessageChannel channel = event.getMessage().getChannel().block();
        final Guild guild = event.getGuild().block();
        try {
            ArrayList<String> currentroleIds = db.getModeratorsOfServer(guild.getId().asString());
            if (currentroleIds.size() == 0){
                EmbedGenerator.infoEmbed(channel,"No Mods Found", "This server has no roles assigned to mods");
            }else{
                int i = 0;
                for (String s : currentroleIds){
                    s = "<@&"+s+">";
                    currentroleIds.set(i,s );
                    i++;
                }

                EmbedGenerator.listEmbed( channel,  "List of moderator roles",currentroleIds);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     *  adds a mod role to a guild
     * @author Gmoley
     * @param event gives detected discord msg
     * @since 9/may/2021
     * @version 1.0
     */
    public void addModerators(MessageCreateEvent event){
        final MessageChannel channel = event.getMessage().getChannel().block();
        final Guild guild = event.getGuild().block();
        final List<String> command = Arrays.asList(event.getMessage().getContent().split(" "));
        String role;

        try{
            role = functions.removeSymbols(command.get(1));

        }catch (Exception e){
            EmbedGenerator.errorEmbed(channel, txt.getString("inv user title"), "Check arguments of the command");
            return;
        }
        try {
            boolean isAdmin;
            ArrayList<String> AdminIds = db.getAdminsOfServer(guild.getId().asString());
            ArrayList<String> currentRoleIds = db.getModeratorsOfServer(guild.getId().asString());
            if (currentRoleIds.contains(role)){
                EmbedGenerator.errorEmbed(channel, txt.getString("inv user title"), "This role already has this permission");
                return;
            }
            if (currentRoleIds.size() != 0){
                //check if valid role and add it to admin roles
                isAdmin = Functions.isAdmin(AdminIds,event.getMessage().getAuthorAsMember().block().getRoleIds());

            } else {
                EmbedGenerator.errorEmbed(channel, txt.getString("inv user title"), "You are not registered as bot admin");
               return;
            }

            //check add
            if (isAdmin){
                db.addModerator(guild.getId().asString(),role);
                EmbedGenerator.pointAddEmbed(channel,"Mod added","added: "+ command.get(1) +" to the list of moderator");
            } else {
                EmbedGenerator.errorEmbed(channel,txt.getString("inv user title"), "You do not have permission to use this command");
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     *  removes a mod role from a guild
     * @author Gmoley
     * @param event gives detected discord msg
     * @since 9/may/2021
     * @version 1.0
     */
    public void removeModerators(MessageCreateEvent event){
        final MessageChannel channel = event.getMessage().getChannel().block();
        final Guild guild = event.getGuild().block();
        final List<String> command = Arrays.asList(event.getMessage().getContent().split(" "));
        String role;

        try{
            role = functions.removeSymbols(command.get(1));

        }catch (Exception e){
            EmbedGenerator.errorEmbed(channel, txt.getString("inv user title"), "Check arguments of the command");
            return;
        }
        try {
            boolean isAdmin;
            ArrayList<String> AdminIds = db.getAdminsOfServer(guild.getId().asString());
            ArrayList<String> currentRoleIds = db.getModeratorsOfServer(guild.getId().asString());
            if (!AdminIds.contains(role)){
                EmbedGenerator.errorEmbed(channel, txt.getString("inv user title"), "This role doesn't have this permission");
                return;
            }
            if (currentRoleIds.size() != 0){
                //check if valid role and add it to admin roles
                isAdmin = Functions.isAdmin(AdminIds,event.getMessage().getAuthorAsMember().block().getRoleIds());

            } else {
                EmbedGenerator.errorEmbed(channel, txt.getString("inv user title"), "You are not registered as bot admin");
                return;
            }

            //check add
            if (isAdmin){
                db.removeModerator(guild.getId().asString(),role);
                EmbedGenerator.pointAddEmbed(channel,"Mod removed","removed: "+ command.get(1) +" from the list of moderator");
            } else {
                EmbedGenerator.errorEmbed(channel,txt.getString("inv user title"), "You do not have permission to use this command");
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
