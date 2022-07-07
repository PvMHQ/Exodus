package Database.DbCommandsets;

import Database.DbController;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DbLearnerApp {
    private Connection connection = DbController.connection;

    public DbLearnerApp() {
    }

    public Boolean newApplicant(String userid, String mode) throws SQLException {

        String sql = "INSERT INTO applications(userid,mode,guidecheck,reqs, kc, verify) " +
                "VALUES(" + userid + ",'" + mode + "',null,null,null,null);";
        try {
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

    public Boolean updateGuideCheck(String userid, String guidecheck) throws SQLException {
        String sql = "";
        if (guidecheck.equals("Yes")) {
            sql = "UPDATE applications SET guidecheck = '" + guidecheck + "' WHERE " +
                    "(userid = '" + userid + "');";
        } else {
            sql = "DELETE FROM applications where userid = '" + userid + "';";
        }
        try {
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

    public boolean updateGearCheck(String userid) throws SQLException {
        String sql = "UPDATE applications SET reqs = 'Yes' WHERE " +
                "(userid = '" + userid + "');";

        try {
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

    public Boolean updateKc(String userid, Integer i) throws SQLException {
        String sql;
        sql = "UPDATE applications SET kc = " + i + " WHERE " +
                "(userid = '" + userid + "');";
        try {
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

    public Integer getkc(String userid) throws SQLException {
        String sql;
        sql = "SELECT kc FROM applications WHERE userid = '" + userid + "'";
        Statement s = connection.createStatement();
        ResultSet messages = s.executeQuery(sql);
        ArrayList<String> resultset = new ArrayList();
        while (messages.next()) {
            resultset.add(messages.getString("kc"));
        }
        return Integer.parseInt(resultset.get(0));
    }

    public String getMode(String userid) throws SQLException {

        String sql;
        sql = "SELECT mode FROM applications WHERE userid = '" + userid + "'";
        Statement s = connection.createStatement();
        ResultSet messages = s.executeQuery(sql);
        ArrayList<String> resultset = new ArrayList();
        while (messages.next()) {
            resultset.add(messages.getString("mode"));
        }

        return resultset.get(0);
    }

    public Boolean updateVerzikver(String userid, String verz) throws SQLException {
        String ver = "";
        if (verz.equals("Yes")) {
            ver = "x";
        }
        String sql;
        sql = "UPDATE applications SET verify = '" + ver + "' WHERE " +
                "(userid = '" + userid + "');";
        try {
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

    public void rwchecker(String userid) throws SQLException {
        String sql;
        if (getrwchecker().equals("")){
            System.out.println("test");
           sql = "INSERT INTO applications(userid,mode,guidecheck,reqs, kc, verify) " +
                    "VALUES(" + userid + ",'" + "rw" + "',null,null,null,null);";
        } else {
            sql = "UPDATE applications SET userid ='"+userid+"' WHERE mode = rw;";
        }

        Statement s = connection.createStatement();
        s.execute(sql);
        connection.commit();
        s.close();
    }

    public String verified(String userid) throws SQLException {
        String sql;
        sql = "SELECT verify FROM applications WHERE userid = '" + userid + "'";
        Statement s = connection.createStatement();
        ResultSet messages = s.executeQuery(sql);
        ArrayList<String> resultset = new ArrayList();
        while (messages.next()) {
            resultset.add(messages.getString("verify"));
        }

        return resultset.get(0);
    }

    public Boolean removeApplicant(String userid) throws SQLException {
        String sql = "DELETE FROM applications where userid = '" + userid + "';";
        try {
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

    public String getrwchecker() throws SQLException {
        String sql ="SELECT userid FROM applications WHERE mode = 'rw';";
        Statement s = connection.createStatement();
        ResultSet messages = s.executeQuery(sql);

        ArrayList<String> resultset = new ArrayList();

        while (messages.next()) {
            resultset.add(messages.getString("userid"));
        }
        if (resultset.isEmpty()){
            return "";
        }
        return resultset.get(0);
    }

    public boolean removerwcheck() throws SQLException {
        String sql = "DELETE FROM applications where mode = 'rw';";
        try {
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
}
