package Model.Commandsets;


import Database.DbCommandsets.DbTeamPoints;
import Model.Functionality.EmbedGenerator;
import Model.Functionality.Functions;
import Model.Interfaces.*;
import Model.Main;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Role;
import discord4j.core.object.entity.channel.MessageChannel;


import java.sql.ResultSet;
import java.util.*;

public class TeamPoints implements CommandSet {
    private DbTeamPoints db;
    private String prefix = Main.prefix;
    private static final Map<String, Command> set = new HashMap<>();
    private Functions functions = Main.functions;
    private ResourceBundle errors = ResourceBundle.getBundle("ResourceBundles.ErrorCodesResourceBundle");
    private ResourceBundle stringBundle = ResourceBundle.getBundle("ResourceBundles.StringResourceBundle");


    public Map<String, Command> getCommandset(Map<String, Command> commands) {
        commands.putAll(set);
        return commands;
    }

    public void commandHelp(MessageCreateEvent event) {
        final MessageChannel channel = event.getMessage().getChannel().block();
        ArrayList<String> titles =new ArrayList<>();
        ArrayList<String> htu = new ArrayList<>();
        titles.add(prefix+"hteams"); htu.add("shows the commands of this section \n HTU: "+prefix+"hteams");
        titles.add(prefix+"resetTeams"); htu.add("Removes all teams from the server \n HTU: "+prefix+"resetTeams");
        titles.add(prefix+"addteam"); htu.add("Adds a team to the server.\n HTU: "+prefix+"addteam 'name of team'");
        titles.add(prefix+"lbteams"); htu.add("Shows the leaderboard of all teams in this server\n HTU: "+prefix+"lbteams");
        titles.add(prefix+"removeteam"); htu.add("Removes a team from the server.\n HTU: "+prefix+"removeteam 'name of team'");
        titles.add(prefix+"team-members"); htu.add("Displays the members in a team\n HTU: "+prefix+"team-members 'name of team'");
        titles.add(prefix+"addteammember"); htu.add("Add a member to a team\n HTU: "+prefix+"addteammember @member 'name of team'");
        titles.add(prefix+"rmteammember"); htu.add("Removes member from a team\n HTU: "+prefix+"rmteammember @member 'name of team'");
        titles.add(prefix+"addteampoints"); htu.add("Adds points to the team of the targeted member\n HTU: "+prefix+"addteampoints @member amount");
        titles.add(prefix+"addteammember");htu.add("Adds the targeted player to the targeted team \n HTU: "+prefix+"addteammember @member 'name of team'");
        titles.add(prefix+"rmteampoints");htu.add("Removes points from the team of the targeted member\n HTU: "+prefix+"rmteampoints @member amount");
        titles.add(prefix+"setupTeams");htu.add("Resets all teams and create a given amount of teams (numbered)" +
                " with a custom 1 word prefix and a list of players" +
                " to divide over the teams\n HTU: "+prefix+"setupTeams amount prefix @member1 @member2 @...");
        titles.add(prefix+"TeamsSetup"); htu.add("Resets all teams and create a given amount of teams (numbered)" +
                " with a custom 1 word prefix and a role of which the members with the role will be divided over the teams" +
                        "\n HTU: "+prefix+"TeamsSetup amount prefix @role");
        EmbedGenerator.commandListEmbed(channel,"Teams Helper", "List of teams functions \n\t * optional field \n",titles,htu);
    }

    public TeamPoints() {
        this.db = Main.db.teamPoints();
        set.put("hteams", this::commandHelp);
        set.put("addteam", this::addTeam);
        set.put("lbteams", this::checkTeams);
        set.put("team-members", this::getTeamMembers);
        set.put("removeteam", this::removeTeam);
        set.put("resetTeams", this::resetTeams);
        set.put("addteampoints", this::addpointsToTeam);
        set.put("addteammember", this::addTeamMember);
        set.put("rmteampoints", this::removeTeamPoints);
        set.put("setupTeams", this::setupTeamsWithNames);
        set.put("TeamsSetup", this :: setupTeamsWithRoles);
        set.put("rmteammember",this::removeTeamMember);
    }

    /**
     * Resets all the teams in a guild
     * @author Gmoley
     * @since 20/April/2021
     * @param event discord msg info
     * @version 1.0
     */
    private void resetTeams(MessageCreateEvent event) {
        final String guildid = event.getGuild().block().getId().asString();
        final MessageChannel channel = event.getMessage().getChannel().block();
        Set<Snowflake> commandUserRoles = event.getMessage().getAuthorAsMember().block().getRoleIds();
       try{
        if (Functions.isAdmin(functions.getAdminRoles(event),commandUserRoles)){
            db.removeAllTeams(guildid);
            EmbedGenerator.pointAddEmbed(channel,"Teams removed", "All teams succesfully removed");
        }
        else {
            EmbedGenerator.errorEmbed(channel,"No Access", "You don't have access to this command");
        }
       } catch (Exception  e){
           EmbedGenerator.errorEmbed(channel, errors.getString("code"),errors.getString("TPS1"));
           e.printStackTrace();
       }
    }

    /**
     *  removes a team from the guild, should have the exact name as an argument
     * @author Gmoley
     * @since 20/April/2021
     * @param event discord msg info
     * @version 1.0
     */
    private void removeTeam(MessageCreateEvent event) {
        final String guildid = event.getGuild().block().getId().asString();
        final MessageChannel channel = event.getMessage().getChannel().block();
        final List<String> command = Arrays.asList(event.getMessage().getContent().split(" "));
        Set<Snowflake> commandUserRoles = event.getMessage().getAuthorAsMember().block().getRoleIds();
        String teamName = "";
        try {
            if (command.size() >2){

            teamName = command.get(1) + " " + functions.getmsg(command);
            }
            else teamName = command.get(1);
            if (teamName.equals(""))
                EmbedGenerator.infoEmbed(channel,"How to use",prefix+"removeteam 'name of team'");
        }
        catch (Exception e){
            e.printStackTrace();
            EmbedGenerator.errorEmbed(channel,errors.getString("code"),errors.getString("TPS5"));
            return;
        }
        try{
            //when the caller is an admin and the teamname isn't in use
            if (Functions.isAdmin(functions.getAdminRoles(event),commandUserRoles)){
                //create a new team (setup teampoints part)
                if (db.isTeamName(guildid,teamName)){
                    db.removeTeam(guildid,teamName);
                    EmbedGenerator.infoEmbed(channel,"Team removed", "Team with name "+ teamName+" succesfully removed");
                } else {
                    EmbedGenerator.errorEmbed(channel,"", "This teamname is not in use or it wasn't found");
                }
            }else{
                EmbedGenerator.errorEmbed(channel,"No Access", stringBundle.getString("no perm txt"));
                return;
            }

      }
        catch (Exception e){

            EmbedGenerator.errorEmbed(channel,"Invalid input", "Not all required fields were filled (correctly)");
            return;
        }

    }

    /**
     * Checks the teams in a guild
     * @author Gmoley
     * @since 20/April/2021
     * @param event discord msg info
     * @version 1.0
     */
    private void checkTeams(MessageCreateEvent event) {
        final String guildid = event.getGuild().block().getId().asString();
        final MessageChannel channel = event.getMessage().getChannel().block();
        ResultSet teams;
        try {
             teams = db.getTeams(guildid);
        } catch (Exception e){
            EmbedGenerator.errorEmbed(channel,errors.getString("code"),errors.getString("TPS6"));
            return;
        }
        ArrayList<String> teamset = new ArrayList<>();
        ArrayList<String> pointset = new ArrayList<>();

        try{
            int i =1;
            while (teams.next()){
               teamset.add(i+ ")  " +teams.getString("teamid"));
               pointset.add( "points: "+teams.getString("points"));
               i++;
            }
            if(teamset.size() == 0){
                EmbedGenerator.infoEmbed(channel, "No teams found", "This server has no teams available");
            }
            else {
            EmbedGenerator.commandListEmbed(channel, "Teams Leaderboard","" ,teamset, pointset);
        }
        } catch (Exception e){
            e.printStackTrace();
            return;
        }
    }

    /**
     * adds a teams with a given name to a guild
     * @author Gmoley
     * @since 20/April/2021
     * @param event discord msg info -> contains the name for the team
     * @version 1.0
     */
    public void addTeam(MessageCreateEvent event){
        final String guildid = event.getGuild().block().getId().asString();
        final MessageChannel channel = event.getMessage().getChannel().block();
        final List<String> command = Arrays.asList(event.getMessage().getContent().split(" "));
        Set<Snowflake> commandUserRoles = event.getMessage().getAuthorAsMember().block().getRoleIds();
        String teamName = "";
        try{
            if(command.size() > 2){
                teamName = command.get(1)+ " " + functions.getmsg(command);
            }else{
                teamName = command.get(1);
            }
            if (teamName.equals("")) {
                EmbedGenerator.errorEmbed(channel,errors.getString("code"),errors.getString("TPS5"));
                return;
            }
        //when the caller is an admin and the teamname isn't in use
        if (Functions.isAdmin(functions.getAdminRoles(event),commandUserRoles)){
           //create a new team (setup teampoints part)
            if (!db.isTeamName(guildid,teamName)){
                db.createTeam(guildid,teamName);
                EmbedGenerator.pointAddEmbed(channel,"Team created", "Team with name "+ teamName+" succesfully created");
            } else {
                EmbedGenerator.errorEmbed(channel,"", "This teamname is already in use");
                return;
            }
        }else{
            EmbedGenerator.errorEmbed(channel,"No Access", "You don't have access to this command");
            return;
        }
        }
        catch (Exception e){
            EmbedGenerator.errorEmbed(channel,"Invalid input", "Not all required fields were filled (correctly)");
            return;
        }
    }

    /**
     * adds a member to a teams
     * @author Gmoley
     * @since 20/April/2021
     * @param event discord msg info -> contains the user and teamname
     * @version 1.0
     */
    private void addTeamMember(MessageCreateEvent event) {
        final String guildid = event.getGuild().block().getId().asString();
        final MessageChannel channel = event.getMessage().getChannel().block();
        final List<String> command = Arrays.asList(event.getMessage().getContent().split(" "));
        boolean isMod;
        isMod = functions.isMod(functions.getModRoles(event),event.getMessage().getAuthorAsMember().block().getRoleIds());
        String targetedMember = "";
        String teamName = "";
        ArrayList<String> teamMembers= new ArrayList<>();
        try {
            targetedMember = functions.removeSymbols(command.get(1));
            teamName = functions.getmsg(command);
            if (teamName.equals("") ||!functions.validuser(event,targetedMember)) {
                EmbedGenerator.errorEmbed(channel, errors.getString("code"), errors.getString("TPS5"));
                return;
            }
        } catch (Exception e){
            e.printStackTrace();
            return;
        }
        if(isMod) {
            try {
                ResultSet membersOfTeam = db.getTeamMembers(guildid, teamName);
                while (membersOfTeam.next()) {
                    teamMembers.add(membersOfTeam.getString("userid"));
                }
                if (!teamMembers.contains(targetedMember) && db.isTeamName(guildid, teamName)) {
                    db.addUserToTeam(guildid, teamName, targetedMember);
                    EmbedGenerator.pointAddEmbed(channel, "User added", "Added " + command.get(1) + " to team with name: '" + teamName + "'.");
                } else if (!db.isTeamName(guildid, teamName)) {
                    EmbedGenerator.errorEmbed(channel, "Team not found", "No team found with this name, check your input or the teams of the server");
                } else {
                    EmbedGenerator.errorEmbed(channel, "Already in team", "This user is already in a team.");
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        } else {
            EmbedGenerator.errorEmbed(channel,"No Access","You need at least moderator bot permissions to use this command.");
        }


    }

    /**
     * get the members of a team in a guild
     * @author Gmoley
     * @since 20/April/2021
     * @param event discord msg info -> contains the teamname
     * @version 1.0
     */
    private void getTeamMembers(MessageCreateEvent event) {
        final String guildid = event.getGuild().block().getId().asString();
        final MessageChannel channel = event.getMessage().getChannel().block();
        final List<String> command = Arrays.asList(event.getMessage().getContent().split(" "));
        String teamName;
        ArrayList<String> teamMembers= new ArrayList<>();
        try{
            if(command.size() > 2) {
                teamName = command.get(1) + " " + functions.getmsg(command);
            }
            else {
                teamName = command.get(1);
            }
            if (teamName.equals("")) {
                EmbedGenerator.errorEmbed(channel, errors.getString("code"), errors.getString("TPS5"));
                return;
            }
            ResultSet membersOfTeam = db.getTeamMembers(guildid, teamName);
            while(membersOfTeam.next()){
                teamMembers.add("<@!"+ membersOfTeam.getString("userid")+">");
            }
            if (teamMembers.size() !=0) {
                EmbedGenerator.listEmbed(channel, "Members Of: " + teamName, teamMembers);
            }
            else {
                EmbedGenerator.infoEmbed(channel, "No Members found", "this team has no members");
            }
            } catch (Exception e){
            e.printStackTrace();
            EmbedGenerator.errorEmbed(channel,errors.getString("Code"),errors.getString("TPS7"));
        }
    }

    /**
     * removes a member from a team
     * @author Gmoley
     * @since 13/may/2021
     * @param event discord msg info -> contains the teamname
     * @version 1.0
     */
    private void removeTeamMember(MessageCreateEvent event){
        final String guildid = event.getGuild().block().getId().asString();
        final MessageChannel channel = event.getMessage().getChannel().block();
        final List<String> command = Arrays.asList(event.getMessage().getContent().split(" "));
        boolean isMod;
        isMod = functions.isMod(functions.getModRoles(event),event.getMessage().getAuthorAsMember().block().getRoleIds());
        String targetedMember = "";
        String teamName = "";
        ArrayList<String> teamMembers= new ArrayList<>();
        try {
            targetedMember = functions.removeSymbols(command.get(1));
            teamName = functions.getmsg(command);
            if (teamName.equals("") ||!functions.validuser(event,targetedMember)) {
                EmbedGenerator.errorEmbed(channel, errors.getString("code"), errors.getString("TPS5"));
                return;
            }
        } catch (Exception e){
            e.printStackTrace();
            return;
        }
        if(isMod) {
            try {
                ResultSet membersOfTeam = db.getTeamMembers(guildid, teamName);
                while (membersOfTeam.next()) {
                    teamMembers.add(membersOfTeam.getString("userid"));
                }
                if (teamMembers.contains(targetedMember) && db.isTeamName(guildid, teamName)) {
                    db.removeUserFromTeam(guildid, teamName, targetedMember);
                    EmbedGenerator.pointAddEmbed(channel, "User removed", "Removed " + command.get(1) + " from the team with name: '" + teamName + "'.");
                } else if (!db.isTeamName(guildid, teamName)) {
                    EmbedGenerator.errorEmbed(channel, "Team not found", "No team found with this name, check your input or the teams of the server");
                } else {
                    EmbedGenerator.errorEmbed(channel, "not in team", "This user is not in this team.");
                }

            }catch (Exception e){
                EmbedGenerator.errorEmbed(channel,errors.getString("code"),errors.getString("SQL1"));
                return;
            }
        } else {
            EmbedGenerator.errorEmbed(channel,"No Access","You need at least moderator bot permissions to use this command.");
        }


    }

    /**
     * adds points to a team by targeting a user
     * @author Gmoley
     * @since 20/April/2021
     * @param event discord msg info -> contains the user and the amount of points
     * @version 1.0
     */
    private void addpointsToTeam(MessageCreateEvent event){
        final String guildid = event.getGuild().block().getId().asString();
        final MessageChannel channel = event.getMessage().getChannel().block();
        final List<String> command = Arrays.asList(event.getMessage().getContent().split(" "));
        boolean isMod = functions.isMod(functions.getModRoles(event),event.getMessage().getAuthorAsMember().block().getRoleIds());
        String targetedMember;
        int amount;
        String teamName;
        try{
            targetedMember = functions.removeSymbols(command.get(1));
            amount =Integer.parseInt(command.get(2));
            teamName = db.getTeamOfMember(guildid,targetedMember);
            if(isMod && !teamName.equals(null)){
            db.addPointsToTeam(guildid,teamName,amount);
            EmbedGenerator.pointAddEmbed(channel,"Points added","Added "+amount+" points to "+teamName+".");
            } else if(teamName.equals(null)){
                EmbedGenerator.errorEmbed(channel,"Team not found", "Couldn't find the team of this user, please check if he is part of a team");
            }
            else{
                EmbedGenerator.errorEmbed(channel,"No Access", "You need at least moderator bot permissions to use this command.");
            }
        } catch (Exception e){
            EmbedGenerator.errorEmbed(channel,errors.getString("code"), errors.getString("TPS4"));
            e.printStackTrace();
            return;
        }
    }

    /**
     * removes points from a team by targeting a user
     * @author Gmoley
     * @since 20/April/2021
     * @param event discord msg info -> contains the user and the amount of points
     * @version 1.0
     */
    private void removeTeamPoints(MessageCreateEvent event) {
        final String guildid = event.getGuild().block().getId().asString();
        final MessageChannel channel = event.getMessage().getChannel().block();
        final List<String> command = Arrays.asList(event.getMessage().getContent().split(" "));
        boolean isMod = functions.isMod(functions.getModRoles(event),event.getMessage().getAuthorAsMember().block().getRoleIds());
        String targetedMember;
        int amount;
        String teamName;
        try{
            targetedMember = functions.removeSymbols(command.get(1));
            amount =Integer.parseInt(command.get(2));
            teamName = db.getTeamOfMember(guildid,targetedMember);
            if(isMod && !teamName.equals(null)){
                db.removePointsFromTeam(guildid,teamName,amount);
                EmbedGenerator.pointAddEmbed(channel,"Points removed","Removed "+amount+" points from "+teamName+".");
            } else if(teamName.equals(null)){
                EmbedGenerator.errorEmbed(channel,"Team not found", "Couldn't find the team of this user, please check if he is part of a team");
            }
            else{
                EmbedGenerator.errorEmbed(channel,"No Access", "You need at least moderator bot permissions to use this command.");
            }
        } catch (Exception e){
            EmbedGenerator.errorEmbed(channel,errors.getString("code"), errors.getString("TPS4"));
            e.printStackTrace();
        }
    }

    /**
     * resets all teams and creates X new ones with a given prefix
     * also a role is passed along and all members with this role will be devided over these teams
     * @author Gmoley
     * @since 29/April/2021
     * @version 1.0
     * @param event discord msg info -> contains the amount of teams the prefix and the role of which members should be used
     */
    private void setupTeamsWithRoles(MessageCreateEvent event){
        final String guildid = event.getGuild().block().getId().asString();
        final MessageChannel channel = event.getMessage().getChannel().block();
        Set<Snowflake> commandUserRoles = event.getMessage().getAuthorAsMember().block().getRoleIds();
        try{
            if (Functions.isAdmin(functions.getAdminRoles(event),commandUserRoles)){
                db.removeAllTeams(guildid);
                setupTeamsWithRolesHelper(event);
            }
            else {
                EmbedGenerator.errorEmbed(channel,"No Access", "You don't have access to this command");
            }
        } catch (Exception  e){
            EmbedGenerator.errorEmbed(channel, errors.getString("code"),errors.getString("TPS1"));
            e.printStackTrace();
        }
    }

    /**
     * helper function of setupTeamsWithRoles
     * @author Gmoley
     * @since 29/April/2021
     * @version 1.0
     * @param event passed message of setupTeamsWithRoles
     */
    private void setupTeamsWithRolesHelper(MessageCreateEvent event) {
        final Guild guild = event.getGuild().block();
        final MessageChannel channel = event.getMessage().getChannel().block();
        final List<String> command = Arrays.asList(event.getMessage().getContent().split(" "));
        int amountOfTeams = 0;
        String prefixOfTeamName = "";
        String roleName  = "";
        ArrayList<String> teamNames= new ArrayList<>();
        ArrayList<String> teamMembers = new ArrayList<>();
        try {
            amountOfTeams = Integer.parseInt(command.get(1));
            prefixOfTeamName = command.get(2);
            roleName = functions.removeSymbols(command.get(3));
        }catch (Exception e){

            EmbedGenerator.errorEmbed(channel, "Invalid Input","one or multiple arguments were wrong, check your input");
            return;
        }
            for (int i =1 ; i<=amountOfTeams;i++){
                teamNames.add(prefixOfTeamName+i);
            }
        try {

            for (String tname : teamNames) {
                db.createTeam(guild.getId().asString(), tname);
            }
        } catch (Exception e){
            EmbedGenerator.errorEmbed(channel, errors.getString("code"), errors.getString("TPS2"));
            return;
        }
            Role role =  guild.getRoleById(Snowflake.of(roleName)).block();
            for (Member m : guild.getMembers().toIterable()){

            if (m.getRoleIds().contains(role.getId())){
                teamMembers.add(functions.removeSymbols( m.getNicknameMention()));
                m.getNicknameMention();
            }
            }
            Collections.shuffle(teamMembers);
    try{
            int j = 0;
            for(int i = 0; i<teamMembers.size();i++){
                if (j >= teamNames.size()) j =0;
                db.addUserToTeam(guild.getId().asString(), teamNames.get(j), teamMembers.get(i));
                j++;
            }
    } catch (Exception e){
        EmbedGenerator.errorEmbed(channel, errors.getString("code"), errors.getString("TPS3"));
        return;
    }
    try{
            for (String team : teamNames){
                ArrayList<String> tmMembers = new ArrayList<>();
                ResultSet membersOfTeam = db.getTeamMembers(guild.getId().asString(), team);

                while(membersOfTeam.next()){
                    tmMembers.add("<@!"+ membersOfTeam.getString("userid")+">");
                }
                if (tmMembers.size() !=0) {
                    EmbedGenerator.listEmbed(channel, "Members Of: " + team, tmMembers);
                }
                else {
                    EmbedGenerator.infoEmbed(channel, "Team:  "+team, "this team has no members due to lacking list of members");
                }
            }

    }catch (Exception e){
            EmbedGenerator.errorEmbed(channel, errors.getString("code"), errors.getString("TPS3"));
            return;
        }
    }

    /**
     * resets all teams and creates X new ones with a given prefix
     * also a list of members is passed which will be divided over the teams in random order
     * @author Gmoley
     * @since 27/April/2021
     * @version 1.0
     * @param event discord msg info -> contains the amount of teams the prefix and which members should be added to them
     */
    private void setupTeamsWithNames(MessageCreateEvent event) {
        final String guildid = event.getGuild().block().getId().asString();
        final MessageChannel channel = event.getMessage().getChannel().block();
        Set<Snowflake> commandUserRoles = event.getMessage().getAuthorAsMember().block().getRoleIds();
        try{
            if (Functions.isAdmin(functions.getAdminRoles(event),commandUserRoles)){
                db.removeAllTeams(guildid);
                setupTeamsWithNamesHelper(event);
            }
            else {
                EmbedGenerator.errorEmbed(channel,"No Access", "You don't have access to this command");
            }
        } catch (Exception  e){
            EmbedGenerator.errorEmbed(channel,  errors.getString("code"),errors.getString("TPS1"));
            e.printStackTrace();
        }
    }

    /**
     * helper function of setupTeamsWithNames
     * @author Gmoley
     * @since 27/April/2021
     * @version 1.0
     * @param event passed message of setupTeamsWithRoles
     */
    private void setupTeamsWithNamesHelper(MessageCreateEvent event) {
        final String guildid = event.getGuild().block().getId().asString();
        final MessageChannel channel = event.getMessage().getChannel().block();
        final List<String> command = Arrays.asList(event.getMessage().getContent().split(" "));
        int amountOfTeams;
        String prefixOfTeamName;
        ArrayList<String> teamNames= new ArrayList<>();
        ArrayList<String> teamMembers = new ArrayList<>();

        try {
            amountOfTeams = Integer.parseInt(command.get(1));
            prefixOfTeamName = command.get(2);
            for (int i = 1; i <= amountOfTeams; i++) {
                teamNames.add(prefixOfTeamName + i);
            }
        } catch (Exception e){
            EmbedGenerator.errorEmbed(channel,errors.getString("code"),errors.getString("TPS5"));
            return;
        }
        try {
            for (int i = 3; i < command.size(); i++) {
                String member = functions.removeSymbols(command.get(i));
                if(!functions.validuser(event,member)){
                    EmbedGenerator.errorEmbed(channel,"Invalid user", "One of the members is invalid");
                    return;
                }
                if (!teamMembers.contains(member)) {
                    teamMembers.add(member);
                }
            }
            for (String tname : teamNames) {
                db.createTeam(guildid, tname);
            }
        } catch (Exception e){
           EmbedGenerator.errorEmbed(channel,errors.getString("code"),errors.getString("TPS2"));
            return;
       }
            Collections.shuffle(teamMembers);
        try{
            int j = 0;
            for (String teamMember : teamMembers) {
                if (j >= teamNames.size()) j = 0;
                db.addUserToTeam(guildid, teamNames.get(j), teamMember);
                j++;
            }

            for (String team : teamNames){
                ArrayList<String> tmMembers = new ArrayList<>();
                ResultSet membersOfTeam = db.getTeamMembers(guildid, team);

                while(membersOfTeam.next()){
                    tmMembers.add("<@!"+ membersOfTeam.getString("userid")+">");
                }
                if (tmMembers.size() !=0) {
                    EmbedGenerator.listEmbed(channel, "Members Of: " + team, tmMembers);
                }
                else {
                    EmbedGenerator.infoEmbed(channel, "Team:  "+team, "this team has no members due to lacking list of members");
                }
            }
        } catch (Exception e){
            e.printStackTrace();

        }
    }

}

