package edu.escuelaing.arep;

import com.google.gson.Gson;
import spark.staticfiles.StaticFilesConfiguration;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

/**
 * WebSpark Secure App 
 */
public class SparkWebApp
{
    public static void main(String[] args) {
        port(getPort());
        Map<String,String> usersMap=new HashMap<>();
        usersMap.put("d.mejia@gmail.com",Services.passwordToHash("123456"));
        secure("keystores/ecikeystore.p12", "123456", null, null);
        Gson gson = new Gson();
        HttpClient.init();

        before("auth/*", (req, response) ->{
            req.session(true);
            if(req.session().isNew()){
                req.session().attribute("Logged",false);
            }
            boolean auth=req.session().attribute("Logged");
            if(!auth){
                halt(401, "<h1>401 Unauthorized</h1>");
            }});

        before("/login.html",((req, response) ->{
            req.session(true);
            if(req.session().isNew()){
                req.session().attribute("Logged",false);
            }
            boolean auth=req.session().attribute("Logged");
            if(auth){
                response.redirect("auth/index.html");
            }}));

        StaticFilesConfiguration config = new StaticFilesConfiguration();
        config.configure("/views");
        before((request, response) ->
                config.consume(request.raw(), response.raw()));

        get("/",((request, response) -> {
            response.redirect("/login.html");
            return "";
        }));

        get("auth/user",((req, res) -> req.session().attribute("User")));

        post("/login",((request, response) -> {
            request.body();
            request.session(true);
            User user= gson.fromJson(request.body(),User.class);
            if(Services.passwordToHash(user.getPassword()).equals(usersMap.get(user.getEmail()))){
                request.session().attribute("User",user.getEmail());
                request.session().attribute("Logged",true);
            }
            else{
                return "username or password are incorrect";
            }
            return "";

        }));

        get("/auth/service",(request, response) -> HttpClient.getHttpService());

    }

    /**
     *
     * @return Retorna el puerto del entorno, por defecto es 4567
     */
    static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 4567;
    }
}
