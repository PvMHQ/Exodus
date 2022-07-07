package Model.Functionality;

import Database.DbCommandsets.DbRolesettings;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class Functions {
    DbRolesettings serversettings = new DbRolesettings();
    public Functions() {
    }

    public static boolean isAdmin(ArrayList<String> currentroleIds, Set<Snowflake> roleIds) {
        boolean status;
        boolean finalstatus = false;

        for (String id : currentroleIds){

           status = roleIds.contains(Snowflake.of(id));
            if (status){
              finalstatus = true;
                break;
            }
        }
        return finalstatus;
    }

    public static boolean isMod(ArrayList<String> currentroleIds, Set<Snowflake> roleIds) {
        boolean status;
        boolean finalstatus = false;

        for (String id : currentroleIds){

            status = roleIds.contains(Snowflake.of(id));
            if (status){
                finalstatus = true;
                break;
            }
        }
        return finalstatus;
    }

    public int targetSameAsOperator(MessageCreateEvent event, String user) {
        final Guild g = event.getGuild().block();

        user = removeSymbols(user);
        Member author = event.getMessage().getAuthorAsMember().block();
        try{
         Member target = g.getMemberById(Snowflake.of(user)).block();
         if(author.equals(target)){
             return 1;
         }
        }
         catch (NumberFormatException e){

            return 2;
         }

        return 0;
    }
    public  boolean validuser(MessageCreateEvent event, String user) {
        boolean finalstatus = false;
        try {
            user = removeSymbols(user);
            final Guild g = event.getGuild().block();


            String test = g.getMemberById(Snowflake.of(user)).block().getDisplayName();
            if (!test.equals(""))
            {
                finalstatus = true;

            }

        } catch (NumberFormatException e){
            return false;
        }
        return finalstatus;
    }

    public boolean validOperator(MessageCreateEvent event) {
        boolean finalstatus = false;
        ArrayList<String> roles = getModRoles(event);
        if (roles.size() !=0) {
            for (String role : roles) {
                boolean status = event.getMessage().getAuthorAsMember().block().getRoleIds().contains(Snowflake.of(role));
                if (status) {
                    finalstatus = true;
                    break;
                }
            }
        }
        else {
            EmbedGenerator.errorEmbed(event.getMessage().getChannel().block(),"ERROR", "Couldn't find moderator roles check initial setup");
        }
        return finalstatus;
    }

    public String removeSymbols(String user) {
        String[] symbols =
                {"<",
                ">",
                "@",
                "&",
                "!"
                };
            for(String s : symbols){
                user = user.replaceAll(s,"");
            }
        return user;
    }


   public ArrayList<String> getAdminRoles(MessageCreateEvent event) {
        try{
        ArrayList<String> list = serversettings.getAdminsOfServer(event.getGuild().block().getId().asString());
        return list;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<String> getModRoles(MessageCreateEvent event) {
        try{
            ArrayList<String> list = serversettings.getModeratorsOfServer(event.getGuild().block().getId().asString());
            return list;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public String getmsg(List<String> command) {
        String reason = new String();
        for (int i = 2 ; i <= command.size()-1; i++){

            if (reason.equals("")){
                reason = command.get(i);
            }else{
                reason = reason + " "+ command.get(i);
            }
        }
        return reason;
    }

    public void clearmsg(MessageCreateEvent event){
        System.out.println("test1");
        final MessageChannel channel = event.getMessage().getChannel().block();
        final Guild g = event.getGuild().block();
        Member author = event.getMessage().getAuthorAsMember().block();
        final List<String> command = Arrays.asList(event.getMessage().getContent().split(" "));
        String targetedUser = null;
        String targetMention = null;
        String targetName = null;
        Boolean isadmin ;
            isadmin = isAdmin(getAdminRoles(event),event.getMessage().getAuthorAsMember().block().getRoleIds());
        System.out.println(isadmin);
            if(isadmin){
        Message msg = channel.getLastMessage().block();
        System.out.println("test");
        while ( msg.isPinned() == false){
            String msgid = msg.getId().asString();
            msg.delete().block();
            msg = channel.getMessagesBefore(Snowflake.of(msgid)).blockFirst();
            if (msg.isPinned()) break;
        }
    }
    }
}
