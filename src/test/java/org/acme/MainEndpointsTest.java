package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import javax.json.bind.Jsonb;
import java.time.LocalTime;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@QuarkusTest
public class MainEndpointsTest {

    @Test
    public void testHelloEndpoint() {
        given()
                .when().get("/myPath")
                .then()
                .statusCode(200)
                .body(is("Hello from RESTEasy Reactive"));
    }

    /**
     Testklass måste göras om, hur exakt jämför man en JsonString? Tåls att googla...
     */
        /*
    @Test
    public void testLoginEndpoint() {
        given()
                .when().accept(ContentType.JSON).contentType(ContentType.JSON).body("{\"username\":\"julius\",\"password\":\"foobar\"}").post("/myPath/login")
                .then()
                .statusCode(200)
                // Test with substring so that EXACT currentTime is not needed
                .body(hasItems());
    }
        */



}