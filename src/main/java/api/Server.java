package api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import controller.DBController;
import controller.SessionController;
import model.Position;
import model.Session;

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
            if(user != null)
                res.addProperty("token", SessionController.getToken(user, password, userid, deviceId));
            else
                res.addProperty("Error", "Missing fields");
            response.body(res.toString());
            return response.body();
        });

        get("/position", (request, response) -> {
            Session retMap = new Gson().fromJson(request.body(), Session.class);
            JsonObject res = new JsonObject();
            String username =  retMap.username;
            Position pos = SessionController.getPosition(username);
            if(pos != null){
                res.addProperty("latitude", pos.lat);
                res.addProperty("longitude", pos.longi);
            } else
                res.addProperty("Error", "Invalid username?");
            response.body(res.toString());
            return response.body();
        });

        put("/position", (request, response) -> {
            Session retMap = new Gson().fromJson(request.body(), Session.class);
            JsonObject res = new JsonObject();
            String username = retMap.username;
            Double lat = retMap.latitude;
            Double longi = retMap.longitude;
            res.addProperty("Result", SessionController.updatePosition(username, lat, longi));
            response.body(res.toString());
            return response.body();
        });
    }

}
