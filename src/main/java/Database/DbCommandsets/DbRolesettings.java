package Database.DbCommandsets;

import Database.DbController;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DbRolesettings {
    private Connection connection =  DbController.connection;

    public DbRolesettings() {
    }

    public ArrayList<String> getAdminsOfServer(String serverid) throws SQLException {
        String sql = "SELECT role FROM servers WHERE (serverid ="+serverid+") AND (roletype = 'admin');";
        Statement s = connection.createStatement();
        ResultSet messages = s.executeQuery(sql);

        ArrayList<String> resultset = new ArrayList();
        while (messages.next()){
            resultset.add(messages.getString("role"));
        }
        s.close();
        return resultset;
    }

    public boolean addAdmin(String serverid, String role) throws SQLException {
        String sql ="INSERT INTO servers(serverid, role, roletype) " +
                "VALUES("+ serverid +","+role+",'admin');";
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
    public void removeAdmin(String serverid, String role) throws SQLException {
        String sql = "DELETE FROM servers WHERE ((serverid ="+serverid+") AND (roletype = 'admin')) AND (role = '"+role+"');";
        Statement s = connection.createStatement();
        s.execute(sql);
        s.close();
    }

    public ArrayList<String> getModeratorsOfServer(String serverid) throws SQLException {
        String sql = "SELECT role FROM servers WHERE (serverid ="+serverid+") AND (roletype = 'moderator');";
        Statement s = connection.createStatement();
        ResultSet messages = s.executeQuery(sql);

        ArrayList<String> resultset = new ArrayList();
        resultset.addAll(getAdminsOfServer(serverid));
        while (messages.next()){
            resultset.add(messages.getString("role"));
        }
        //admins have higher permissions than mods so they should count as a mod aswell
        s.close();
        return resultset;
    }

    public boolean addModerator(String serverid, String role)throws SQLException {
        String sql ="INSERT INTO servers(serverid, role, roletype) " +
                "VALUES("+ serverid +","+role+",'moderator');";
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
    public void removeModerator(String serverid, String role) throws SQLException {
        String sql = "DELETE FROM servers WHERE ((serverid ="+serverid+") AND (roletype = 'moderator')) AND (role = '"+role+"');";
        Statement s = connection.createStatement();
        s.execute(sql);
        s.close();
    }
}
