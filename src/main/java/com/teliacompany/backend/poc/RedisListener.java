package com.teliacompany.backend.poc;

import com.teliacompany.backend.poc.entities.ResponseEntity;
import com.teliacompany.backend.poc.jsonbentities.RequestEntity;
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
import java.util.concurrent.atomic.AtomicInteger;

@Startup
@ApplicationScoped
public class RedisListener {

    private static final Logger LOG = Logger.getLogger("ListenerBean");

    @Inject
    ReactiveRedisDataSource redis;

    ReactiveListCommands<String, RequestEntity> requests;

    ReactiveListCommands<String, ResponseEntity> responses;

    /**
     This method launches on startup and reads the redis list "requests". When something appears in said list, this
     method will take it, create a response based on the information in the request and send said response back
     to redis, placing it in a NEW LIST that shares a name with the requestId.

     This way a Quarkus frontend will ALWAYS KNOW what it can listen for; a list in redis with the SAME NAME as the
     request that IT just sent (Frontend)
     */
    public void listenToRedis(@Observes StartupEvent event) {
        // Method needs either to while and repeat OR to constantly repeat by itself using some probable quarkus-magic

        requests = redis.list(RequestEntity.class);
        responses = redis.list(ResponseEntity.class);

        LOG.info("Listening to redis... ");

        try {
            Multi.createBy().repeating().uni(AtomicInteger::new, (x) -> {
                        try {
                            return requests.blpop(Duration.ofSeconds(5), "requests");
                        } catch (Exception e) {
                            return null;
                        }
                    }).indefinitely() // Do this forever
                    .map(KeyValue::value) // This turns the result into a RequestEntity!
                    .flatMap((request) -> {
                        LOG.info("Got request! requestId=" + request.getRequestId() + ", name=" + request.getName()
                                + ", specialAttack=" + request.getSpecialAttack());

                        // A response is created as a pojo with basic constructor
                        ResponseEntity response = new ResponseEntity(request.getRequestId(), 0, "Worked! :)",
                                "This is a respons based on the query " + request.getRequestId());

                        // Here is where this is now pushed to a BRAND-NEW list with the name of the REQUEST ID from frontend!
                        return responses.rpush(request.getRequestId(), response).toMulti();
                    }).subscribe().with(input -> {
                                // I don't know if we need stuff here??
                            },
                            fail -> {
                                LOG.info("Everything went wrong! Got exception from stream: " + fail.getMessage());
                            });


        } catch (Exception e) {
            LOG.info("Queue listener has died. EXCEPTION HAPPENED!");
        }
    }

}