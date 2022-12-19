package org.gmagnotta;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;

import org.gmagnotta.service.AWSRekognitionService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


import static io.restassured.RestAssured.given;
import static org.mockito.ArgumentMatchers.anyString;

@QuarkusTest
public class CloudEventResourceTest {

    @InjectMock
    AWSRekognitionService awsService;

    @Test
    public void testCloudEvent() throws Exception {

        String payload = "{  \r\n   \"Records\":[  \r\n      {  \r\n         \"eventVersion\":\"2.2\",\r\n         \"eventSource\":\"aws:s3\",\r\n         \"awsRegion\":\"us-west-2\",\r\n         \"eventName\":\"event-type\",\r\n         \"userIdentity\":{  \r\n            \"principalId\":\"Amazon-customer-ID-of-the-user-who-caused-the-event\"\r\n         },\r\n         \"requestParameters\":{  \r\n            \"sourceIPAddress\":\"ip-address-where-request-came-from\"\r\n         },\r\n         \"responseElements\":{  \r\n            \"x-amz-request-id\":\"Amazon S3 generated request ID\",\r\n            \"x-amz-id-2\":\"Amazon S3 host that processed the request\"\r\n         },\r\n         \"s3\":{  \r\n            \"s3SchemaVersion\":\"1.0\",\r\n            \"configurationId\":\"ID found in the bucket notification configuration\",\r\n            \"bucket\":{  \r\n               \"name\":\"peppesan\",\r\n               \"ownerIdentity\":{  \r\n                  \"principalId\":\"Amazon-customer-ID-of-the-bucket-owner\"\r\n               },\r\n               \"arn\":\"bucket-ARN\"\r\n            },\r\n            \"object\":{  \r\n               \"key\":\"IMG_3266.JPG\",\r\n               \"size\":2345,\r\n               \"eTag\":\"object eTag\",\r\n               \"versionId\":\"object version if bucket is versioning-enabled, otherwise null\",\r\n               \"sequencer\": \"a string representation of a hexadecimal value used to determine event sequence, only used with PUTs and DELETEs\"\r\n            }\r\n         },\r\n         \"glacierEventData\": {\r\n            \"restoreEventData\": {\r\n               \"lifecycleRestorationExpiryTime\": \"The time, in ISO-8601 format, for example, 1970-01-01T00:00:00.000Z, of Restore Expiry\",\r\n               \"lifecycleRestoreStorageClass\": \"Source storage class for restore\"\r\n            }\r\n         }\r\n      }\r\n   ]\r\n}";

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

        Mockito.verify(awsService).analyzeImageLabelsFromS3Bucket("peppesan", "IMG_3266.JPG");

    }

    @Test
    public void testCloudEventException() throws Exception {

        String payload = "{  \r\n   \"Records\":[  \r\n      {  \r\n         \"eventVersion\":\"2.2\",\r\n         \"eventSource\":\"aws:s3\",\r\n         \"awsRegion\":\"us-west-2\",\r\n         \"eventName\":\"event-type\",\r\n         \"userIdentity\":{  \r\n            \"principalId\":\"Amazon-customer-ID-of-the-user-who-caused-the-event\"\r\n         },\r\n         \"requestParameters\":{  \r\n            \"sourceIPAddress\":\"ip-address-where-request-came-from\"\r\n         },\r\n         \"responseElements\":{  \r\n            \"x-amz-request-id\":\"Amazon S3 generated request ID\",\r\n            \"x-amz-id-2\":\"Amazon S3 host that processed the request\"\r\n         },\r\n         \"s3\":{  \r\n            \"s3SchemaVersion\":\"1.0\",\r\n            \"configurationId\":\"ID found in the bucket notification configuration\",\r\n            \"bucket\":{  \r\n               \"name\":\"peppesan\",\r\n               \"ownerIdentity\":{  \r\n                  \"principalId\":\"Amazon-customer-ID-of-the-bucket-owner\"\r\n               },\r\n               \"arn\":\"bucket-ARN\"\r\n            },\r\n            \"object\":{  \r\n               \"key\":\"IMG_3266.JPG\",\r\n               \"size\":2345,\r\n               \"eTag\":\"object eTag\",\r\n               \"versionId\":\"object version if bucket is versioning-enabled, otherwise null\",\r\n               \"sequencer\": \"a string representation of a hexadecimal value used to determine event sequence, only used with PUTs and DELETEs\"\r\n            }\r\n         },\r\n         \"glacierEventData\": {\r\n            \"restoreEventData\": {\r\n               \"lifecycleRestorationExpiryTime\": \"The time, in ISO-8601 format, for example, 1970-01-01T00:00:00.000Z, of Restore Expiry\",\r\n               \"lifecycleRestoreStorageClass\": \"Source storage class for restore\"\r\n            }\r\n         }\r\n      }\r\n   ]\r\n}";

        Mockito.doThrow(new Exception("dummy")).when(awsService).analyzeImageLabelsFromS3Bucket(anyString(), anyString());

        given()
        .header("Content-Type", "application/json")
        .header("Ce-Id", "foo-1")
        .header("Ce-Specversion", "1.0")
        .header("Ce-Type", "com.gmagnotta.events/s3upload")
        .header("Ce-Source", "image-uploader")
        .body(payload)
          .when().post("/")
          .then()
             .statusCode(500);

        Mockito.verify(awsService).analyzeImageLabelsFromS3Bucket("peppesan", "IMG_3266.JPG");
        
    }
}