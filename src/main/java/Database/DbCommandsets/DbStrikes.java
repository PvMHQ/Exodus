package Database.DbCommandsets;

import Database.DbController;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DbStrikes {
    private Connection connection =  DbController.connection;

    public DbStrikes() {

    }

    public boolean addStrike(String serverid, String userid, String message) throws SQLException {
        String sql = "INSERT INTO strikelog(serverid, userid, strike) " +
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
    public boolean removeStrike(String serverid, String userid, String message) throws SQLException{
        String sql ="DELETE FROM strikelog " +
                "WHERE ((serverid ="+ serverid+") " +
                "AND (userid ="+userid+"))" +
                "AND(strike = '"+message+"');";
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
    public ArrayList checkStrikes(String serverid, String userid) throws SQLException {

        String sql = "SELECT strike FROM strikelog " +
                "WHERE (serverid ="+serverid+") " +
                "AND (userid ="+userid+");";

        Statement s = connection.createStatement();
        ResultSet messages = s.executeQuery(sql);
        ArrayList<String> resultset = new ArrayList();
        while (messages.next()){
            resultset.add(messages.getString("strike"));
        }
        return resultset;
    }
}
