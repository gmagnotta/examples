package org.gmagnotta;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class CloudEventResourceTest {
    public void testHelloEndpoint() {

        String payload = "{ \"Records\":[ { \"eventVersion\":\"2.2\", \"eventSource\":\"aws:s3\", \"awsRegion\":\"us-west-2\", \"eventName\": \"mytest\" }, \"s3\":{ \"s3SchemaVersion\":\"1.0\", \"configurationId\":\"dummy\", \"bucket\":{ \"name\":\"peppesan\" }, \"object\":{ \"key\":\"test.jpg\", \"size\":1024 } } } ]}";
        
        given()
        .header("Content-Type", "application/json")
        .header("Ce-Id", "foo-1")
        .header("Ce-Specversion", "1.0")
        .header("Ce-Type", "com.gmagnotta.events/s3upload")
        .header("Ce-Source", "image-uploader")
        .body(payload)
          .when().post("/")
          .then()
             .statusCode(202);
    }

}