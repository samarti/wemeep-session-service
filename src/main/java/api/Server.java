package api;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import controller.DBController;
import controller.SessionController;
import controller.Utils;
import model.Position;
import model.Session;

import java.util.LinkedList;
import java.util.Map;

import static spark.Spark.*;

/**
 * Created by santiagomarti on 12/14/15.
 */
public class Server {

    public static void main(String[] args){

        DBController controller = new DBController();
        controller.init();


        get("/", (request, response) -> "WeMeep Session Service");

        /**
         * Validates a token. Must be called with every request.
         */
        post("/validatetoken", (request, response) -> {
            Session retMap = new Gson().fromJson(request.body(), Session.class);
            JsonObject res = new JsonObject();
            if(retMap.token != null) {
                boolean valid = SessionController.isTokenValid(retMap.token);
                res.addProperty("valid", valid);
            } else
                res.addProperty("Erorr", "Missing fields");
            response.body(res.toString());
            return response.body();
        });

        /**
         * Generates a token
         */
        post("/generatetoken", (request, response) -> {
            Session retMap = new Gson().fromJson(request.body(), Session.class);
            JsonObject res = new JsonObject();
            String user = retMap.username;
            String password = "";
            String userid =  retMap.id;
            String deviceId = retMap.deviceid;
            String gcmId = retMap.gcmid;
            if(user != null && gcmId != null && deviceId != null)
                res.addProperty("token", SessionController.getToken(user, password, userid, deviceId, gcmId));
            else
                res.addProperty("Error", "Missing fields. Req: username, deviceid and gcmid");
            response.body(res.toString());
            return response.body();
        });

        get("/position/:id", (request, response) -> {
            JsonObject res = new JsonObject();
            String id = request.params(":id");
            if(id != null) {
                Position pos = SessionController.getPosition(id);
                if (pos != null) {
                    res.addProperty("latitude", pos.lat);
                    res.addProperty("longitude", pos.longi);
                } else
                    res.addProperty("Error", "Invalid userId or position has not been set for that userId");
            } else
                res.addProperty("Error", "Missing id");
            response.body(res.toString());
            return response.body();
        });


        put("/position", (request, response) -> {
            Session retMap = new Gson().fromJson(request.body(), Session.class);
            JsonObject res = new JsonObject();
            if(retMap.deviceid != null && retMap.lat != 0.0 && retMap.longi != 0.0 && retMap.gcmid != null && retMap.id != null && retMap.username != null)
                res.addProperty("Success", SessionController.updatePosition(retMap.deviceid, retMap.id, retMap.username, retMap.lat, retMap.longi, retMap.gcmid));
            else
                res.addProperty("Error", "Missing fields. id, deviceId, username, lat, longi and gcmid required");
            response.body(res.toString());
            return response.body();
        });

        get("/session/:id", (request, response) -> {
            String id = request.params(":id");
            JsonArray arr = new JsonArray();
            LinkedList<Session> sesArr = SessionController.buildSession(id);
            for(Session ses : sesArr){
                JsonObject res = new JsonObject();
                res.addProperty("latitude", ses.lat);
                res.addProperty("longitude", ses.longi);
                res.addProperty("username", ses.username);
                res.addProperty("userId", ses.id);
                res.addProperty("gcmId", ses.gcmid);
                res.addProperty("deviceId", ses.deviceid);
                arr.add(res);
            }
            response.body(arr.toString());
            return response.body();
        });

        delete("/session/:deviceid", (request, response) -> {
            String deviceId = request.params(":deviceid");
            JsonObject res = new JsonObject();
            if (SessionController.deleteSession(deviceId))
                res.addProperty("Success", true);
            else
                res.addProperty("Success", false);
            response.body(res.toString());
            return response.body();
        });

        get("/closeusers", (request, response) -> {
            double lat, longi, radius;
            try {
                Map<String, String> data = Utils.splitQuery(request.queryString());
                lat = Double.parseDouble(data.get("lat"));
                longi = Double.parseDouble(data.get("longi"));
                radius = Double.parseDouble(data.get("radius"));
                if(lat == 0.0 || longi == 0.0 || radius > 100)
                    throw new Exception();
            } catch (Exception e){
                JsonObject red = new JsonObject();
                red.addProperty("Error", "Bad arguments. Please provide lat, longi and radius <= 100");
                response.body(red.toString() + "\n");
                return response.body();
            }
            LinkedList<Session> sessions = SessionController.buildSessions();
            JsonArray ret = new JsonArray();
            for(Session ses : sessions){
                if(Utils.haversine(lat, longi, ses.lat, ses.longi) < radius){
                    JsonObject aux = new JsonObject();
                    aux.addProperty("latitude", ses.lat);
                    aux.addProperty("longitude", ses.longi);
                    aux.addProperty("username", ses.username);
                    aux.addProperty("userId", ses.id);
                    aux.addProperty("gcmId", ses.gcmid);
                    aux.addProperty("deviceId", ses.deviceid);
                    ret.add(aux);
                }
            }
            response.body(ret.toString());
            return response.body();
        });
    }

}
