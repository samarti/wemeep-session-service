package api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import controller.DBController;
import controller.SessionController;
import model.Session;

import static spark.Spark.get;
import static spark.Spark.post;

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
            boolean valid = SessionController.isTokenValid(retMap.sessionToken);
            JsonObject res = new JsonObject();
            res.addProperty("valid", valid);
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
            String userid =  retMap.userId;
            String deviceId = retMap.deviceId;
            res.addProperty("token", SessionController.getToken(user, password, userid, deviceId));
            response.body(res.toString());
            return response.body();
        });
    }

}
