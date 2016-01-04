package model;

import java.sql.Timestamp;

/**
 * Created by santiagomarti on 12/14/15.
 */
public class Session {

    public String token, deviceid, id, username, gcmid;
    public Timestamp expirationDate;
    public double lat, longi;

    public Session(String token, String deviceid, String id, String username, String gcmid, Timestamp expirationDate, double lat, double longi) {
        this.token = token;
        this.deviceid = deviceid;
        this.id = id;
        this.username = username;
        this.gcmid = gcmid;
        this.expirationDate = expirationDate;
        this.lat = lat;
        this.longi = longi;
    }
}
