package org.gmagnotta;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

import static io.restassured.RestAssured.given;

import java.util.ArrayList;
import java.util.List;

@QuarkusTest
public class CloudEventResourceTest {


    @Test
    public void testInvalidPayload() {
        String payload = "{ \"dummy\": \"aaa\" }";

        given()
        .header("Content-Type", "application/json")
        .header("Ce-Id", "foo-1")
        .header("Ce-Specversion", "1.0")
        .header("Ce-Type", "com.gmagnotta.events/s3upload")
        .header("Ce-Source", "image-uploader")
        .body(payload)
          .when().post("/")
          .then()
             .statusCode(400);

    }

    @Test
    public void test() {
        org.gmagnotta.persistence.LabelledFile file = org.gmagnotta.persistence.LabelledFile.findById("test");
        Assertions.assertNull(file);

        String payload = "{ \"name\": \"test\", \"labels\": [\"test\"] }";

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

        List<String> labels = new ArrayList<>();
        labels.add("test");

        file = org.gmagnotta.persistence.LabelledFile.findById("test");

        Assertions.assertNotNull(file);
        Assertions.assertEquals(file.getName(), "test");
        Assertions.assertArrayEquals(file.getLabels().toArray(), labels.toArray());

    }
}