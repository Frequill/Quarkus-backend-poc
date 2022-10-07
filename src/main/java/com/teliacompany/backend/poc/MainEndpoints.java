package com.teliacompany.backend.poc;

import com.teliacompany.backend.poc.entities.LoginToken;
import com.teliacompany.backend.poc.jsonbentities.UserEntity;
import com.teliacompany.backend.poc.jsonbentities.LoginEntity;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

@Path("/myPath")
public class MainEndpoints {

    @GET
    @Path("/hello")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        System.out.println("Hello message was sent! ;)");
        return "Hello from Julius Thomsen";
    }

    /**
     These two placeholders reset upon reload of code (kinda sucks)
     */
    public HashMap<String, UserEntity> allUsers = new HashMap<>(); // All created accounts
    public ArrayList<String> activeAccounts = new ArrayList<>(); // All accounts currently logged-in


    //Mini-constructor adds a "testUser" into the "allUsers" hashmap just so one is always there by default during development
    public MainEndpoints(){
        UserEntity testBoy = new UserEntity("testUser", "password", "test.user@gmail.com");
        allUsers.put(testBoy.getUsername(), testBoy);
    }


    /**
     * Correct login example call:
     curl -H "Content-Type: application/json" -H "Accept: application/json" -X POST -d '{"username":"testUser", "password":"password"}' "http://localhost:8082/myPath/login"

     * Incorrect login example call:
     curl -H "Content-Type: application/json" -H "Accept: application/json" -X POST -d '{"username":"jonas", "password":"WRONG-PASSWORD"}' "http://localhost:8082/myPath/login"
     */
    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public LoginEntity login(LoginEntity loginEntity){
        // Checks if a user with the inputted username exists and if that users password also matches inputted password
        if (Objects.equals(loginEntity.getJsonbPass(), allUsers.get(loginEntity.getJsonbUsername()).getPassword())){

            // The account exists - set status to 200
            loginEntity.setJsonbStatus("200");

            // Create fake web token and set the property of the JsonbLogin object to said token
            LoginToken token = new LoginToken(loginEntity.getJsonbUsername());
            loginEntity.setJsonbToken(token.getFakedToken());

            // Since this is a local application, the logged-in users are simply added to an ArrayList
            activeAccounts.add(token.getFakedToken());
            System.out.println("Active accounts: " + activeAccounts);

            //Return final Jsonb
            return loginEntity;

        } else {
            // Should account not match, return status 404
            loginEntity.setJsonbStatus("404 - User does not seem to exist...");
        }
        return loginEntity;
    }

    @POST
    @Path("/mkUser")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public UserEntity createUser(UserEntity userEntity){
        allUsers.put(userEntity.getUsername(), userEntity);
        System.out.println("allUsers: " + allUsers);
        return userEntity;
    }

    /**
     * Loggs out user if currently logged-in.
     * Logged-in users tokens are stored in arrayList "activeAccounts"

     * If you want to try this method, copy your login-token from backend-terminal after logged in (active accounts),
     then use it as your parameter curl http://localhost:8080/myPath/logout/**TOKEN HERE**
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

    @GET
    @Path("/getAllUsers")
    @Produces(MediaType.TEXT_PLAIN)
    public String getAllUsers(){
        System.out.println("All users = " + allUsers);
        return "All users = " + allUsers;
    }

}
