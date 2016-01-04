package controller;

import model.Position;
import model.Session;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.*;
import java.text.SimpleDateFormat;


/**
 * Created by santiagomarti on 12/15/15.
 */
public class DBController {

    private static Connection c;

    public void init() {

        try {
            Thread.sleep(10 * 1000);
        } catch(InterruptedException e){
            e.getMessage();
        }

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
                    //.getConnection("jdbc:postgresql://192.168.99.100:49162/postgres",
                            "postgres", "postgres");
            Statement stmt = c.createStatement();
            String sessionTable = "create table if not exists sessions (id SERIAL primary key, userId char(50) unique not null," +
                    " username char(20) unique not null, deviceId char(50), token char(50) unique, tokenExpiration Timestamp, latitude double precision, longitude double precision" +
                    ", gcmId char (257) unique)";
            stmt.execute(sessionTable);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public boolean tokenExists(String tkn) {
        try {
            Statement stmt = c.createStatement();
            String get = "select * from sessions where username = \'" + tkn + "\';";
            ResultSet set = stmt.executeQuery(get);
            return set.next();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return false;
        }
    }

    public String getUserToken(String username) {
        try {
            Statement stmt = c.createStatement();
            String get = "select token from sessions where username = \'" + username + "\';";
            ResultSet set = stmt.executeQuery(get);
            set.next();
            return set.getString("token").trim();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return "";
        }
    }

    public Timestamp getTokenExpiration(String token) {
        try {
            Timestamp expire = null;
            Statement stmt = c.createStatement();
            String get = "select tokenExpiration from sessions where token = \'" + token + "\';";
            ResultSet set = stmt.executeQuery(get);
            while (set.next()) {
                expire = set.getTimestamp("tokenExpiration");
                break;
            }
            return expire;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return null;
        }
    }

    public void insertToken(String token, String username, String userId, String deviceId, Timestamp expire, String gcmId) {
        try {
            String s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(expire);
            String insert = "";
            if (!userExistsInDB(userId)) {
                insert = String.format("insert into sessions (userId, username, deviceId, token, tokenExpiration, gcmId) values ('%s','%s','%s','%s','%s', '%s')"
                        , userId, username, deviceId, token, s, gcmId);
            } else {
                insert = String.format("update sessions set token = '%s', tokenExpiration = '%s', gcmId = '%s' where username = '%s'", token, s, gcmId, username);
            }
            Statement stmt = c.createStatement();
            stmt.execute(insert);
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public boolean userExistsInDB(String id) {
        try {
            Statement stmt = c.createStatement();
            String get = "select * from sessions where userId = \'" + id + "\';";
            ResultSet set = stmt.executeQuery(get);
            return set.next();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return false;
        }
    }

    public Position getPosition(String id){
        try {
            Statement stmt = c.createStatement();
            String get = "select latitude, longitude from sessions where userId = \'" + id + "\';";
            ResultSet set = stmt.executeQuery(get);
            set.next();
            return new Position(set.getDouble(1), set.getDouble(2));
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return null;
        }
    }

    public boolean updatePosition(String id, String username, double lat, double longi, String gcmId){
        if(userExistsInDB(id))
            try {
                String update = "update sessions set latitude = " + lat + ", longitude = " + longi + ", gcmId = '" + gcmId +  "', username = '" + username + "' where userId = '" + id + "'";
                Statement stmt = c.createStatement();
                stmt.execute(update);
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
                return false;
            }
        else {
            try {
                String insert = String.format("insert into sessions (userId, username, gcmId, latitude, longitude) values ('%s','%s','%s','%f','%f')"
                        , id, username, gcmId, lat, longi);
                Statement stmt = c.createStatement();
                stmt.execute(insert);
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
                return false;
            }
        }
    }

    public Session buildSession(String id){
        try {
            String select = "select deviceId, username, gcmId, latitude, longitude from sessions where userId = '" + id + "'";
            Statement stmt = c.createStatement();
            ResultSet set = stmt.executeQuery(select);
            set.next();
            return new Session(null, set.getString(1).trim(), id, set.getString(2).trim(), set.getString(3).trim(), null, set.getDouble(4), set.getDouble(5));
        } catch (SQLException e){
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return null;
        }
    }
}
