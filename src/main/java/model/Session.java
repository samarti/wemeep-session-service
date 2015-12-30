package model;

import java.sql.Timestamp;

/**
 * Created by santiagomarti on 12/14/15.
 */
public class Session {

    public String token, deviceid, id, username, gcmid;
    public Timestamp expirationDate;
    public double lat, longi;

}
