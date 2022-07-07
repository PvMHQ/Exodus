package Database.DbCommandsets;
import Database.DbController;
import Model.Functionality.EmbedGenerator;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Locale;

public class DbTeamPoints {
    private Connection connection =  DbController.connection;

    public DbTeamPoints() {
    }

    /**
     * checks if a team name is in use for a discord server
     * returns true if it is in use
     * @author Gmoley
     * @since 25/april/2021
     * @version 1.0
     * @param serverId the guild of which the team belongs to
     * @param teamName the name of the team
     * @return Boolean -> is the name already in use for the guild
     * @throws SQLException incorrect Query
     */
    public Boolean isTeamName(String serverId, String teamName) throws SQLException {
        boolean status = true;
        String sql ="SELECT teamid FROM teampointstable " +
                    "WHERE (serverid = '"+serverId+"') AND (teamid ='"+teamName+"')";
        try {
            Statement s = connection.createStatement();
            ResultSet result = s.executeQuery(sql);
            ArrayList<String> teams = new ArrayList();
            while (result.next()){
                teams.add(result.getString("teamid"));
            }
            if (teams.size() == 0){
                status = false;
            }
            connection.commit();
            s.close();

        } catch (Exception e){
            e.printStackTrace();
            connection.rollback();
        }
        return status;
    }

    /**
     * creates a team (teamName) and assigns it to a server (guildid)
     * @author Gmoley
     * @since 25/april/2021
     * @version 1.0
     * @param guildid the guild of which the team will belongs to
     * @param teamName the name for the team
     * @throws SQLException incorrect Query
     */
    public void createTeam(String guildid, String teamName) throws SQLException{
        String sql = "INSERT INTO teampointstable(serverid,teamid,points)"+
                      "VALUES('"+ guildid +"','"+teamName+"','"+ 0 +"');";
        try{
        Statement s = connection.createStatement();
        s.execute(sql);
        connection.commit();
        s.close();
        } catch (Exception e){
            e.printStackTrace();
            connection.rollback();
            return;
        }
    }

    /**
     * gives all teams in a server
     * @author Gmoley
     * @since 25/april/2021
     * @version 1.0
     * @param guildid discord server id
     * @return gives back a resultset of all teams in the guild
     * @throws SQLException incorrect Query
     */
    public ResultSet getTeams(String guildid) throws SQLException {
        String sql = "SELECT teamid,points FROM teampointstable" +
                     " WHERE (serverid = '" +guildid+ "') ORDER BY points DESC;";
        try{
            Statement s = connection.createStatement();
            ResultSet result = s.executeQuery(sql);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            connection.rollback();
            return null;
        }
    }

    /**
     * removes all teams of a discord server
     * @author Gmoley
     * @since 25/april/2021
     * @version 1.0
     * @param guildid discord server id
     * @throws SQLException incorrect Query
     */
    public void removeAllTeams(String guildid) throws SQLException{
        String teampointstableSql ="DELETE FROM teampointstable " +
                "WHERE (serverid ='"+ guildid+"');" ;
        String teammembertableSql ="DELETE FROM teammembertable " +
                "WHERE (serverid ='"+ guildid+"');" ;
        try{
            Statement s = connection.createStatement();
            s.execute(teampointstableSql);
            s.execute(teammembertableSql);
            connection.commit();
            s.close();
            return ;
        } catch (Exception e) {
            connection.rollback();
            e.printStackTrace();
        }
    }

    /**
     * removes a single team (of which the name is given) from a discord server
     * @author Gmoley
     * @since 25/april/2021
     * @version 1.0
     * @param guildid discord server id
     * @param teamName the name of the team
     * @throws SQLException incorrect Query
     */
    public void removeTeam(String guildid, String teamName) throws SQLException {
        String teampointstableSql ="DELETE FROM teampointstable " +
                "WHERE ((serverid ='"+ guildid+"') " +
                "AND (teamid ='"+teamName+"'));" ;
        String teammembertableSql = "DELETE FROM teammembertable " +
                "WHERE ((serverid ='"+ guildid+"') " +
                "AND (teamid ='"+teamName+"'));";

        try{
            Statement s = connection.createStatement();
            s.execute(teampointstableSql);
            s.execute(teammembertableSql);
            connection.commit();
            s.close();
            return ;
        } catch (Exception e) {
            connection.rollback();
            e.printStackTrace();
        }

    }

    /**
     * get the team of a member within the server
     * @author Gmoley
     * @since 23/april/2021
     * @version 1.0
     * @param guildid discord server id
     * @param memberid userid of a discord user
     * @return the name of a team within the server
     * @throws SQLException incorrect Query
     */
    public String getTeamOfMember(String guildid, String memberid)throws SQLException{
        String sql = "SELECT teamid FROM teammembertable WHERE" +
                "((serverid = '"+guildid+"')AND(userid = '"+memberid+"'));";
        String team = null;
        try{
            Statement s = connection.createStatement();
            ResultSet result = s.executeQuery(sql);
            while(result.next()){
                team = result.getString("teamid");
            }

            return team;
        }catch (Exception e){
            e.printStackTrace();
            connection.rollback();
            return null;
        }

    }

    /**
     * get all the members of a team
     * @author Gmoley
     * @since 23/april/2021
     * @version 1.0
     * @param guildid discord server id
     * @param teamName name of the targeted team
     * @return get all the members of a team within the server in a resultset
     * @throws SQLException incorrect Query
     */
    public ResultSet getTeamMembers(String guildid, String teamName) throws SQLException {
        String sql = "SELECT userid FROM teammembertable WHERE" +
                "((serverid = '"+guildid+"')AND(teamid = '"+teamName+"'));";
        try{
            Statement s = connection.createStatement();
            ResultSet result = s.executeQuery(sql);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            connection.rollback();
            return null;
        }
    }

    /**
     * adds a member to a team
     * @author Gmoley
     * @since 23/april/2021
     * @version 1.0
     * @param guildid discord server
     * @param teamName team to which the user is added
     * @param targetedMember the user who needs to be added to a team
     * @throws SQLException incorrect Query
     */
    public void addUserToTeam(String guildid, String teamName, String targetedMember) throws SQLException {
        String sql = "INSERT INTO teammembertable(serverid,teamid,userid)"+
                "VALUES('"+ guildid +"','"+teamName+"','"+ targetedMember +"');";
        try{
            Statement s = connection.createStatement();
            s.execute(sql);
            connection.commit();
            s.close();
        } catch (Exception e){
            e.printStackTrace();
            connection.rollback();
            return;
        }
    }

    /**
     * removes points from a team
     * @author Gmoley
     * @since 13/may/2021
     * @version 1.0
     * @param guildid discord server that the team belongs to
     * @param teamName name of the team which contains the user
     * @param targetedMember the tag of the user to be removes
     * @throws SQLException incorrect Query
     */
    public void removeUserFromTeam(String guildid, String teamName, String targetedMember) throws SQLException {
        String sql = "DELETE FROM teammembertable WHERE ((serverid = '"+guildid+ "') AND (teamid = '"+teamName+"')) AND (userid = '"+targetedMember+"');";
        try{
            Statement s = connection.createStatement();
            s.execute(sql);
            connection.commit();
            s.close();
        } catch (Exception e){
            e.printStackTrace();
            connection.rollback();
            throw new SQLException();

        }
    }

    /**
     * adds points to a team
     * @author Gmoley
     * @since 23/april/2021
     * @version 1.0
     * @param guildid discord server that the team belongs to
     * @param teamName name of the team which the points should be added to
     * @param amount number of points to be added
     * @throws SQLException incorrect Query
     */
    public void addPointsToTeam(String guildid, String teamName, int amount) throws SQLException {
        String selectSql = "SELECT points FROM teampointstable WHERE" +
                "((serverid = '"+guildid+"')AND(teamid = '"+teamName+"'));";
        int teampoints =0;
        try{
            Statement s = connection.createStatement();
            ResultSet result = s.executeQuery(selectSql);
            while(result.next()){
                teampoints = result.getInt("points");
            }
            teampoints = teampoints + amount;
            System.out.println(teampoints);
            String exeSql = "UPDATE teampointstable SET points = '"+teampoints+"' WHERE"+
                    "((serverid = '"+guildid+"')AND(teamid = '"+teamName+"'));";
            s.execute(exeSql);
            connection.commit();
            return ;
        }catch (Exception e){
            e.printStackTrace();
            connection.rollback();
            return ;
        }
    }

    /**
     * removes points from a team
     * @author Gmoley
     * @since 23/april/2021
     * @version 1.0
     * @param guildid discord server that the team belongs to
     * @param teamName name of the team which the points should be removed from
     * @param amount number of points to be removed
     * @throws SQLException incorrect Query
     */
    public void removePointsFromTeam(String guildid, String teamName, int amount) throws SQLException {
        String selectSql = "SELECT points FROM teampointstable WHERE" +
                "((serverid = '"+guildid+"')AND(teamid = '"+teamName+"'));";
        int teampoints =0;
        try{
            Statement s = connection.createStatement();
            ResultSet result = s.executeQuery(selectSql);
            while(result.next()){
                teampoints = result.getInt("points");
            }
            teampoints = teampoints - amount;
            System.out.println(teampoints);
            String exeSql = "UPDATE teampointstable SET points = '"+teampoints+"' WHERE"+
                    "((serverid = '"+guildid+"')AND(teamid = '"+teamName+"'));";
            s.execute(exeSql);
            connection.commit();
            return ;
        }catch (Exception e){
            e.printStackTrace();
            connection.rollback();
            return ;
        }
    }


}
