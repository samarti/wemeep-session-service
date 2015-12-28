package controller;

import model.Position;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.Calendar;

/**
 * Created by santiagomarti on 12/15/15.
 */
public class SessionController {

    /**
     * Generates a token, provided:
     * 1. There's no current token
     * 2. The current token is invalid
     * @param username
     * @param password
     * @return
     */
    public static String getToken(String username, String password, String userId, String deviceId ){
        DBController contr = new DBController();
        SecureRandom random = new SecureRandom();
        if(tokenExists(username) && isTokenValid(contr.getUserToken(username))){
            return contr.getUserToken(username);
        }
        String ret =  new BigInteger(130, random).toString(32);
        Calendar cal = Calendar.getInstance(); // will be equal to now
        cal.add(Calendar.DAY_OF_YEAR, 40 * 7);
        contr.insertToken(ret, username, userId, deviceId, new Timestamp(cal.getTimeInMillis()));
        return ret;
    }

    public static boolean tokenExists(String token){
        DBController contr = new DBController();
        return contr.tokenExists(token);

    }

    /**
     * Checks if a token is valid, following these conditions:
     * 1. The token must exist in the database
     * 2. The token expiration date must not be due
     * @param token
     * @return
     */
    public static boolean isTokenValid(String token){
        DBController contr = new DBController();
        Timestamp expire = contr.getTokenExpiration(token);
        if(expire == null)
            return false;
        return expire.after(new Timestamp(new java.util.Date().getTime()));
    }

    public static Position getPosition(String username){
        DBController contr = new DBController();
        return contr.getPosition(username);
    }

    public static boolean updatePosition(String username, double lat, double longi){
        DBController contr = new DBController();
        return contr.updatePosition(username, lat, longi);
    }
}
