package org.acme;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

@Path("/myPath")
public class MainEndpoints {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello from RESTEasy Reactive";
    }

    public ArrayList<String> activeAccounts = new ArrayList<>();

    /**
     * Correct login example call:
     curl -H "Content-Type: application/json" -H "Accept: application/json" -X POST -d '{"username":"julius", "password":"foobar"}' "http://localhost:8080/myPath/login"

     * Incorrect login example call:
     curl -H "Content-Type: application/json" -H "Accept: application/json" -X POST -d '{"username":"jonas", "password":"WRONG-PASSWORD"}' "http://localhost:8080/myPath/login"
     */
    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public LoginEntity login(LoginEntity loginEntity){
        if (Objects.equals(loginEntity.getJsonbUsername(), "julius") && Objects.equals(loginEntity.getJsonbPass(), "foobar")){

            // The account exists - set status to 200
            loginEntity.setJsonbStatus("200");

            // Create fake web token and set the property of the JsonbLogin object to said token
            LoginTokenEntity token = new LoginTokenEntity(loginEntity.getJsonbUsername());
            loginEntity.setJsonbToken(token.getFakedToken());

            // Since this is a local application, the logged-in users are simply added to an ArrayList
            activeAccounts.add(token.getFakedToken());
            System.out.println("Active accounts: " + activeAccounts);

            //Return final Jsonb
            return loginEntity;

        } else {
            // Should account not match, return status 404
            loginEntity.setJsonbStatus("404");
        }
        return loginEntity;
    }

    /**
     * Loggs out user if currently logged-in.
     * Logged-in users tokens are stored in arrayList "activeAccounts"

     * If you want to try this method, copy your login-token from backend-terminal after logged in,
       then use it as your parameter
     */
    @GET
    @Path("/logout/{token}")
    @Produces(MediaType.TEXT_PLAIN)
    public String logout(String token) {
        for (int i = 0; i < activeAccounts.size(); i++){
            if (Objects.equals(token, activeAccounts.get(i))){
                activeAccounts.remove(activeAccounts.get(i));
                return "You have successfully been logged out";
            }
        }
        return "No such account is active in current session...";
    }

}