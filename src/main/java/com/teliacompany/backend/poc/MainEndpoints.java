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

    @GET
    @Path("/helloName/{name}")
    @Produces(MediaType.TEXT_PLAIN)
    public String helloName(@PathParam("name") String name) {
        System.out.println("Hello message was sent! ;)");
        return "Hello, " + name + " from Julius Thomsen!";
    }

    /**
     These two placeholders reset upon reload of code (kinda sucks)
     */
    public HashMap<String, UserEntity> allUsers = new HashMap<>(); // All created accounts

    //First String is a loginToken, second is a username
    public HashMap<String, String> activeAccounts = new HashMap<>(); // All accounts currently logged-in


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

        if (new ArrayList<>(activeAccounts.values()).contains(loginEntity.getJsonbUsername())){
            loginEntity.setJsonbUsername("");
            loginEntity.setJsonbPass("");
            loginEntity.setJsonbStatus("500 : THIS ACCOUNT IS ALREADY LOGGED-IN");
            return loginEntity;
        }

        // Checks if a user with the inputted username exists and if that users password also matches inputted password
        if (Objects.equals(loginEntity.getJsonbPass(), allUsers.get(loginEntity.getJsonbUsername()).getPassword())){

            // The account exists - set status to 200
            loginEntity.setJsonbStatus("200");

            // Generates a fake web token, adds it to the "LoginEntity" so it can be returned to frontend,
            // then adds it to a hashmap, so it can be stored, like in a DB
            LoginToken token = new LoginToken(loginEntity.getJsonbUsername());
            loginEntity.setJsonbLoginToken(token.getFakedToken());
            activeAccounts.put(token.getFakedToken(), loginEntity.getJsonbUsername());


            // TEST Want to see what the result looks like in "activeAccounts" hashmap
            System.out.println("Result in activeAccounts = " + token.getFakedToken() + " " + loginEntity.getJsonbUsername());

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
        // Just blocks you from adding the same user twice
        if (allUsers.containsKey(userEntity.getUsername())){
            userEntity.setEmail("");
            userEntity.setPassword("");
            userEntity.setUsername("ERROR: This user account already exists!");

            System.out.println("Nothing was added to allUsers: " + allUsers);
        }
        else {
            allUsers.put(userEntity.getUsername(), userEntity);
            System.out.println("Successfully added to allUsers: " + allUsers);
        }
        return userEntity;
    }

    /**
     * Loggs out user if currently logged-in.
     * Logged-in users tokens are stored in arrayList "activeAccounts"

     * If you want to try this method, copy your login-token from backend-terminal after logged in (active accounts),
     then use it as your parameter curl http://localhost:8082/myPath/logout/**TOKEN HERE**
     */

    @GET
    @Path("/logout/{token}")
    @Produces(MediaType.TEXT_PLAIN)
    public String logout(String token) {
        if (activeAccounts.containsKey(token)) {
            activeAccounts.remove(token);
            return "User successfully logged out";
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
