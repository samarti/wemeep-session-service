package controller;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.*;
import java.text.SimpleDateFormat;


/**
 * Created by santiagomarti on 12/15/15.
 */
public class DBController {

    private static Connection c;
    public void init(){
        InetAddress dbAddr = null;
        try {
            dbAddr = InetAddress.getByName("dbsession");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        c = null;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://" + dbAddr.getHostAddress() + ":5432/postgres",
                            "postgres", "postgres");
            Statement stmt = c.createStatement();
            String sessionTable = "create table if not exists sessions (id SERIAL primary key, userId char(50) unique not null," +
                    " username char(20) unique not null, deviceId char(50) not null, token char(50) unique not null, tokenExpiration Timestamp not null)";
            stmt.execute(sessionTable);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
    }

    public boolean tokenExists(String tkn){
        try {
            Statement stmt = c.createStatement();
            String get = "select * from sessions where username = \'" + tkn + "\';";
            ResultSet set = stmt.executeQuery(get);
            return set.next();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
            return false;
        }
    }

    public String getUserToken(String username){
        try {
            Statement stmt = c.createStatement();
            String get = "select token from sessions where username = \'" + username + "\';";
            ResultSet set = stmt.executeQuery(get);
            set.next();
            return set.getString("token");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
            return "";
        }
    }

    public Timestamp getTokenExpiration(String token){
        try {
            Timestamp expire = null;
            Statement stmt = c.createStatement();
            String get = "select tokenExpiration from sessions where token = \'" + token + "\';";
            ResultSet set = stmt.executeQuery(get);
            while(set.next()){
                expire = set.getTimestamp("tokenExpiration");
                break;
            }
            return expire;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
            return null;
        }
    }

    public void insertToken(String token, String username, String userId, String deviceId, Timestamp expire){
        try {
            String s = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(expire);
            String insert = "";
            if(!userExistsInDB(username)){
                insert = String.format("insert into sessions (userId, username, deviceId, token, tokenExpiration) values ('%s','%s','%s','%s','%s')"
                        ,userId, username, deviceId, token, s);
            } else {
                insert = String.format("update sessions set token = '%s', tokenExpiration = '%s' where username = '%s'", token, s, username);
            }
            Statement stmt = c.createStatement();
            stmt.execute(insert);
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
    }

    public boolean userExistsInDB(String username){
        try {
            Statement stmt = c.createStatement();
            String get = "select * from sessions where username = \'" + username + "\';";
            ResultSet set = stmt.executeQuery(get);
            return set.next();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
            return false;
        }
    }
}
