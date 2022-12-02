package org.gmagnotta;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class CloudEventResourceTest {

    @Test
    public void testHelloEndpoint() {

        String payload = "{\"bucket\":\"peppesan\",\"object\":\"mtb-collection.jpeg\"}";
        
        given()
          .body(payload)
          .header("Content-Type", "application/json")
          .header("Ce-Id", "foo-1")
          .header("Ce-Specversion", "1.0")
          .header("Ce-Type", "com.gmagnotta.events/s3upload")
          .header("Ce-Source", "image-uploader")
          .when().post("/")
          .then()
             .statusCode(202);
    }

}