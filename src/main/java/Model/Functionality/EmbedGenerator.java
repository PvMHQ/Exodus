package Model.Functionality;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.rest.util.Color;

import java.time.Instant;
import java.util.ArrayList;

public class EmbedGenerator {
    public EmbedGenerator() {
    }

    public static void rankUpdateEmbed(Guild g , MessageChannel channel, String memberid , String beginRoleid, String endRoleid) {
        Member m = g.getMemberById( Snowflake.of(memberid)).block();
        String memberMention = m.getNicknameMention();
        String beginrole = g.getRoleById(Snowflake.of(beginRoleid)).block().getMention();
        String endrole = g.getRoleById(Snowflake.of(endRoleid)).block().getMention();
        channel.createEmbed(spec ->
                spec.setColor(Color.GREEN)

                        .setTitle("Rank update")

                        .setDescription("Rank of "+memberMention+" changed.\n From "+beginrole+" to "+endrole+"!")

                        .setTimestamp(Instant.now())).block();
    }



    public static void errorEmbed(MessageChannel channel, String title, String description) {
        channel.createEmbed(spec ->
                spec.setColor(Color.RED)

                        .setTitle(title)
                        .setDescription(description)).block();
    }
    public static void pointAddEmbed(MessageChannel channel, String title, String description) {
        channel.createEmbed(spec ->
                spec.setColor(Color.GREEN)

                        .setTitle(title)

                        .setDescription(description)

                        .setTimestamp(Instant.now())).block();
    }
    public static void infoEmbed(MessageChannel channel, String title, String description){
        channel.createEmbed(spec ->
                spec.setColor(Color.DISCORD_BLACK)

                        .setTitle(title)

                        .setDescription(description)

                        .setTimestamp(Instant.now())).block();
    }

    public static void listEmbed(MessageChannel channel, String title, ArrayList<String> description) {
        String d = new String();
        int i = 1;
        for (String s : description){
            d = d+ i+")   "+ s + "\n";
            i++;
        }
        String finalDescription = d;
        channel.createEmbed(spec ->
                spec.setColor(Color.SUMMER_SKY)
                        .addField(title,finalDescription, false)

                        .setTimestamp(Instant.now())).block();

    }
    public static void commandListEmbed(MessageChannel channel, String title,String description, ArrayList<String> command , ArrayList<String> howToUse) {
        channel.createEmbed(spec ->{
                spec.setColor(Color.DEEP_SEA);
                spec.setTitle(title);
                spec.setDescription(description);
                for(int i = 0 ; i < command.size();i++){
                    spec.addField(command.get(i), howToUse.get(i), false);
                }
                spec.setTimestamp(Instant.now());
                }
        ).block();

    }
    public static void urltitleembed(MessageChannel channel, String title, String URL){
        channel.createEmbed(spec ->
                spec.setColor(Color.ENDEAVOUR)

                        .setTitle(title)
                        .setUrl(URL)
        ).block();
    }

    public static void channelmsgEmbed(MessageChannel channel, String title, String description){
        channel.createEmbed(spec ->
                spec.setColor(Color.SEA_GREEN)

                        .setTitle(title)

                        .setDescription(description)

                        .setTimestamp(Instant.now())).block();
    }
}
