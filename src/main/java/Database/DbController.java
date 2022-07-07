package Database;

import Database.DbCommandsets.DbMerits;
import Database.DbCommandsets.DbRolesettings;
import Database.DbCommandsets.DbStrikes;
import Database.DbCommandsets.DbTeamPoints;
import discord4j.common.util.Snowflake;

import java.sql.*;
import java.util.ArrayList;


public class DbController {
   public static Connection connection;
   private final String connectionstring=  "jdbc:sqlite:serverdata.db";
   private DbStrikes dbStrikes;
   private DbMerits dbMerits;
   public DbRolesettings dbRolesettings;
   private DbTeamPoints dbTeamPoints;
   public void createDb() throws SQLException{

      String sql =


              "CREATE TABLE strikelog(\n" +
              "    serverid VARCHAR(200) NOT NULL ,\n" +
              "    userid VARCHAR(200) NOT NULL,\n" +
              "    strike VARCHAR(200)\n" +
              ");\n" +
              "\n" +

              "CREATE TABLE meritlog(\n" +
              "    serverid VARCHAR(200) NOT NULL ,\n" +
              "    userid VARCHAR(200) NOT NULL,\n" +
              "    merit VARCHAR(200)\n" +
              ");\n" +
              "\n" +
              "\n" +

              "CREATE TABLE teampointstable(\n" +
              "    serverid VARCHAR(200) NOT NULL ,\n" +
              "    teamid VARCHAR(200) NOT NULL ,\n" +
              "    points INTEGER\n" +
              ");\n" +

              "CREATE TABLE teammembertable(\n" +
              "    serverid VARCHAR(200) NOT NULL ,\n" +
              "    teamid VARCHAR(200) NOT NULL ,\n" +
              "    userid VARCHAR(200) NOT NULL\n" +
              ");\n" +
              "\n" +
              "\n" +

              "CREATE TABLE servers(\n" +
              "    serverid VARCHAR(200) NOT NULL ,\n" +
              "    role VARCHAR(200) ,\n" +
              "    roletype VARCHAR(200)\n" +
              "\n" +
              ");\n"+

               "CREATE TABLE applications(\n" +
               "    userid VARCHAR(200) NOT NULL,\n" +
               "    mode VARCHAR(4),\n"+
               "    guidecheck VARCHAR(4),\n"+
               "    reqs    VARCHAR(4),\n"+
               "    kc      INTEGER,\n"+
               "   verify   VARCHAR(4)\n"+
               ");";



       try {
           Statement s = connection.createStatement();
          s.executeUpdate(sql);
          connection.commit();
          s.close();
      }
      catch(Exception e){
           connection.rollback();
      }
   }

    public DbController() {
       try{
           if (connection == null) {
               connection = DriverManager.getConnection(connectionstring);
               connection.setAutoCommit(false);
               //allows DB reset (if needed)
             createDb();
           }
       }
       catch(Exception e){
           e.printStackTrace();
       }
        dbStrikes = new DbStrikes();
    }

    public DbTeamPoints teamPoints() {
        if (dbTeamPoints == null){
            dbTeamPoints = new DbTeamPoints();
            return dbTeamPoints;
        } else return dbTeamPoints;
    }

    public DbStrikes strikes() {
       if (dbStrikes == null){
           dbStrikes = new DbStrikes();
        return dbStrikes;
       } else return dbStrikes;
    }

    public DbMerits merits() {
       if(dbMerits == null) {
           dbMerits = new DbMerits();
           return dbMerits;
       } else return dbMerits;
    }
    public DbRolesettings serversettings(){
       if (dbRolesettings == null){
           dbRolesettings = new DbRolesettings();
           return dbRolesettings;
       } else return dbRolesettings;
    }



    public void removeGuild(String guildId) throws SQLException {
        String Sqlmeritlog = "DELETE FROM meritlog " +"WHERE (serverid ="+ guildId+") ;";
        String Sqlstrikelog = "DELETE FROM strikelog " +"WHERE (serverid ="+ guildId+") ;";
        String Sqlteampointstable = "DELETE FROM teampointstable " +"WHERE (serverid ="+ guildId+") ;";
        String Sqlteammembertable = "DELETE FROM teammembertable " +"WHERE (serverid ="+ guildId+") ;";
        String Sqlservers = "DELETE FROM servers " +"WHERE (serverid ="+ guildId+") ;";

        try{
            Statement s = connection.createStatement();
            s.execute(Sqlmeritlog);
            s.execute(Sqlstrikelog);
            s.execute(Sqlteampointstable);
            s.execute(Sqlteammembertable);
            s.execute(Sqlservers);
            connection.commit();
            s.close();
            return;
        } catch (Exception e) {
            connection.rollback();
            e.printStackTrace();
        }
        return;
    }

    public void newServerjoin(String guildid) throws SQLException{

        String sql ="INSERT INTO servers(serverid, role, roletype) " +
                "VALUES("+ guildid +","+"null"+",'null');";
        try{
            Statement s = connection.createStatement();
            s.execute(sql);
            connection.commit();
            s.close();
            return;
        } catch (Exception e) {
            connection.rollback();
            e.printStackTrace();
        }
        return;
    }

    public Boolean getServer(String guild) throws SQLException{
       String sql = "SELECT serverid FROM servers where (serverid == '"+guild+"');";

        try{
            Statement s = connection.createStatement();
           ResultSet res= s.executeQuery(sql);
            connection.commit();
            return res.next();

        } catch (Exception e) {
            connection.rollback();
            e.printStackTrace();
        }
        return null;
    }
}

