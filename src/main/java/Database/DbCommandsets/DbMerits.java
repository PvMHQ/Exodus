package Database.DbCommandsets;

import Database.DbController;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DbMerits {
    private Connection connection =  DbController.connection;

    public DbMerits() {

    }


    public boolean addMerit(String serverid, String userid, String message) throws SQLException {
        String sql = "INSERT INTO meritlog(serverid, userid, merit) " +
                     "VALUES("+ serverid +","+userid+",'"+message+"');";
        try{
            Statement s = connection.createStatement();
            s.execute(sql);
            connection.commit();
            s.close();
            return true;
        } catch (Exception e) {
            connection.rollback();
            e.printStackTrace();
        }
        return false;
    }

    public boolean removeMerit(String serverid, String userid, String message) throws SQLException{
        String sql ="DELETE FROM meritlog " +
                "WHERE ((serverid ="+ serverid+") " +
                "AND (userid ="+userid+"))" +
                "AND(merit = '"+message+"');";
        try{
            Statement s = connection.createStatement();
            s.execute(sql);
            connection.commit();
            s.close();
            return true;
        } catch (Exception e) {
            connection.rollback();
            e.printStackTrace();
        }
        return false;
    }
    public ArrayList checkMerits(String serverid, String userid) throws SQLException {

        String sql = "SELECT merit FROM meritlog " +
                "WHERE (serverid ="+serverid+") " +
                "AND (userid ="+userid+");";
        Statement s = connection.createStatement();
        ResultSet messages = s.executeQuery(sql);
        ArrayList<String> resultset = new ArrayList();
        while (messages.next()){
            resultset.add(messages.getString("merit"));
        }
        return resultset;
    }
}
