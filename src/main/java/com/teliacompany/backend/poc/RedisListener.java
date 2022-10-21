package com.teliacompany.backend.poc;

import com.teliacompany.backend.poc.entities.LoginToken;
import com.teliacompany.backend.poc.entities.ResponseEntity;
import com.teliacompany.backend.poc.jsonbentities.LoginEntity;
import com.teliacompany.backend.poc.jsonbentities.RequestEntity;
import com.teliacompany.backend.poc.jsonbentities.UserEntity;
import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.redis.datasource.list.KeyValue;
import io.quarkus.redis.datasource.list.ReactiveListCommands;
import io.quarkus.runtime.Startup;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.Multi;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@Startup
@ApplicationScoped
public class RedisListener {

    /**
     Mini-constructor just exists to place testUser into allUsers by default. This way testing is easier :)
     */
    public RedisListener() {
        UserEntity testBoy = new UserEntity("testUser", "password", "test.user@gmail.com");
        MainEndpoints.allUsers.put(testBoy.getUsername(), testBoy);
    }



    private static final Logger LOG = Logger.getLogger("ListenerBean");

    @Inject
    ReactiveRedisDataSource redis;

    ReactiveListCommands<String, RequestEntity> requests;

    ReactiveListCommands<String, ResponseEntity> responses;



    /**
     This method launches on startup and reads the redis list "requests". When something appears in said list, this
     method will take it, check what TYPE of request it has gotten based on its name (login, logout etc...), preform
     the subsequent calculations based on what the request wants to save or remove from dataBase (currently emulated)
     and finally place a ResponseEntity back into redis to a new list with the SAME NAME as the initial requestId

     This way a Quarkus frontend will ALWAYS KNOW what it can listen for; a list in redis with the SAME NAME as the
     request that IT just sent (Frontend)
     */

    public void listenToRedis(@Observes StartupEvent event) {
        requests = redis.list(RequestEntity.class);
        responses = redis.list(ResponseEntity.class);

        LOG.info("Listening to redis... ");

        try {
            Multi.createBy().repeating().uni(AtomicInteger::new, (x) -> {
                        try {
                            LOG.info("Five second rule");
                            return requests.blpop(Duration.ofSeconds(5), "requests");
                        } catch (Exception e) {
                            return null;
                        }
                    }).indefinitely() // Do this forever
                    .map(KeyValue::value) // This turns the result into a RequestEntity!
                    .flatMap((request) -> {
                        LOG.info("Got request: requestId=" + request.getRequestId() + ", name=" + request.getName()
                                + ", inputString=" + request.getInputString());

                        // IF this is a sayHello request:
                        if (Objects.equals(request.getName(), "sayHello")){
                            LOG.info("This is a ''sayHello'' request");
                            ResponseEntity response = sayHello(request);

                            // The final login ResponseEntity is returned here! ***********
                            return responses.rpush(request.getRequestId(), response).toMulti();
                        }

                        // IF this is a login request:
                        else if (Objects.equals(request.getName(), "login")) {
                            LOG.info("This is a login request");
                            ResponseEntity response = login(request);

                            // The final login ResponseEntity is returned here! ***********
                            return responses.rpush(request.getRequestId(), response).toMulti();
                        }

                        // IF this is a logout request:
                        else if (Objects.equals(request.getName(), "logout")) {
                            LOG.info("This is a logout request");
                            ResponseEntity response = logout(request);

                            // The final logout ResponseEntity is returned here! ***********
                            return responses.rpush(request.getRequestId(), response).toMulti();
                        }

                        else if (Objects.equals(request.getName(), "createAccount")) {
                            LOG.info("This is a request to create a new account");
                            ResponseEntity response = createAccount(request);

                            // The final logout ResponseEntity is returned here! ***********
                            return responses.rpush(request.getRequestId(), response).toMulti();
                        }

                        else if (Objects.equals(request.getName(), "getAllUsers")) {
                            LOG.info("This is a request to get all users");
                            ResponseEntity response = getAllUsers(request);

                            // The final logout ResponseEntity is returned here! ***********
                            return responses.rpush(request.getRequestId(), response).toMulti();
                        }

                        // If error = "X returned null... you're probably here!"
                        return null;


                    }).subscribe().with(input -> {
                        // Do we need something here??????
                    }, fail -> {
                        LOG.info("Everything went wrong! Got exception from stream: " + fail.getMessage());
                    });

        } catch (Exception e) {
            LOG.info("SOME EXCEPTION: " + e);
        }
    }



    /**
     In true neighborly spirit this method simply returns a ResponseEntity containing a little friendly greeting

     (The most important method!)
     */
    public ResponseEntity sayHello(RequestEntity request){
        return new ResponseEntity(request.getRequestId(), 200, "Connection functional!",
                "Hello from Julius Thomsen! :) (:");
    }



    /**
     This method is called when a request from frontend placed into Redis has the name "login". It will take the
     RequestEntity and turn it into a ResponseEntity that can be returned to Redis as long as the account is verified
     and the password was correct.

     Should the password be incorrect or if the account itself does not exist in our "DB", return a ResponseEntity that
     contains the appropriate information to user, telling them what the problem was and how to correct it.
     */
    public ResponseEntity login(RequestEntity request) {
        LOG.info("ENTERED SEND LOGIN REQUEST!");

        // Create an array by splitting the "inputString" from the requestEntity. Index 1 and 3 will ALWAYS contain username
        // and password as long as they were correctly placed and ordered during initial call to POST method in Frontend
        String[] loginInformation = request.getInputString().split(",");
        LoginEntity loginEntity = new LoginEntity();

        // username and password are properly filled with correct information based on what was sent to Redis
        loginEntity.setJsonbUsername(loginInformation[1]);
        loginEntity.setJsonbPass(loginInformation[3]);


        // IF Account is already logged-in (exists in activeAccounts) then do not let user login a second time without logging out first
        if (new ArrayList<>(MainEndpoints.activeAccounts.values()).contains(loginEntity.getJsonbUsername())){
            loginEntity.setJsonbUsername("");
            loginEntity.setJsonbPass("");
            loginEntity.setJsonbStatus("500 : THIS ACCOUNT IS ALREADY LOGGED-IN");
            return new ResponseEntity(request.getRequestId(), 500, "Already logged-in account",
                    "This ACE-account is already active in current session!");
        }


        // Checks if a user with the inputted username exists and if that users password also matches inputted password
        if (Objects.equals(loginEntity.getJsonbPass(), MainEndpoints.allUsers.get(loginEntity.getJsonbUsername()).getPassword())){

            LOG.info("ACCOUNT SEEMS TO EXIST");

            // The account exists - set status to 200
            loginEntity.setJsonbStatus("200");

            // Generates a fake web token and adds it to the "LoginEntity" so it can be returned to Redis,
            // then adds it to a hashmap, so it can be stored, like in a DB
            LoginToken token = new LoginToken(loginEntity.getJsonbUsername());
            loginEntity.setJsonbLoginToken(token.getFakedToken());
            MainEndpoints.activeAccounts.put(token.getFakedToken(), loginEntity.getJsonbUsername());


            // TEST Want to see what the result looks like in "activeAccounts" hashmap
            System.out.println("Result in activeAccounts = " + token.getFakedToken() + " " + loginEntity.getJsonbUsername());

            // Finally return the account as a ResponseEntity, so it can be viewed in first Redis then in the Frontend
            return new ResponseEntity(request.getRequestId(), 200, "Login successful, token = "
                    + loginEntity.getJsonbLoginToken(),
                    "Welcome! Your account: " + loginEntity.getJsonbUsername() + " is now logged into our system!");

        } else {
            // Should account not match, return a ResponseEntity with status 404
            loginEntity.setJsonbStatus("404 - User does not seem to exist...");
            return new ResponseEntity(request.getRequestId(), 404, "Unknown account",
                    "This account does not seem to exist in our systems... " +
                            "Please make sure your account has successfully been created before logging in. " +
                            "Should problem persist, please contact customer support");
        }
    }



    /**
     This method is called when a request from frontend placed into Redis has the name "logout". It will take the
     RequestEntity and turn it into a ResponseEntity that can be returned to Redis as long as the account was active in
     the current session (aka: saved in the activeAccounts hashMap)

     If the loginToken does not match a token inside activeAccounts, a ResponseEntity containing an explanation of how
     this account is not currently logged into the service is returned
     */
    public ResponseEntity logout(RequestEntity request) {

        String[] inputStringSplit = request.getInputString().split(",");
        String loginToken = inputStringSplit[1];

        if (MainEndpoints.activeAccounts.containsKey(loginToken)) {
            MainEndpoints.activeAccounts.remove(loginToken);
            return new ResponseEntity(request.getRequestId(), 200, "Logout successful",
                    "Thank you for using our service, please come again! :)");
        }
        return new ResponseEntity(request.getRequestId(), 500, "Logout failed...",
                "This account is not logged-in to current session. Should problem persist, please contact customer support");
    }



    /**
     This method allows users to create new accounts! It takes any request containing the name "createAccount" and adds
     that user to our emulated database (a hashmap) provided that the account name isn't taken/doesn't already exist

     Should the account exists prior to creation attempt, this method will send back a ResponseEntity stating that the
     account creation failed, else it will state that all worked as intended.
     */
    public ResponseEntity createAccount(RequestEntity request) {
        // Creates and fills a UserEntity based on the information from redis in the RequestEntity
        UserEntity userEntity = new UserEntity();
        String[] inputString = request.getInputString().split(",");

        userEntity.setUsername(inputString[1]);
        userEntity.setPassword(inputString[3]);
        userEntity.setEmail(inputString[5]);

        // Blocks users from adding the same account twice
        if (MainEndpoints.allUsers.containsKey(userEntity.getUsername())){
            LOG.info("Nothing was added to allUsers: " + MainEndpoints.allUsers);

            // ResponseEntity is returned saying that a problem occured and that the account already exists
            return new ResponseEntity(request.getRequestId(), 404, "Duplicate account",
                    "This account already exists!");
        }
        else {
            // The account (a UserEntity) is placed into the "allUsers" hashMap and will, from now on, exist in the system
            MainEndpoints.allUsers.put(userEntity.getUsername(), userEntity);
            LOG.info("Successfully added to allUsers: " + MainEndpoints.allUsers);

            // ResponseEntity is returned saying that all went well
            return new ResponseEntity(request.getRequestId(), 200, "Successful creation!",
                    "Your account has been successfully added to our system!");
        }
    }


    /**
     Basic method simply returns the value of MainEndpoints.allUsers as a ResultEntity
     */
    public ResponseEntity getAllUsers(RequestEntity request) {
        // Simply returns the result of MainEndpoints.allUsers as a response
        return new ResponseEntity(request.getRequestId(), 200, "Successfully got all users",
                "Here is every single registered account:    " + MainEndpoints.allUsers);
    }



}