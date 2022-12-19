package org.gmagnotta.service;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import io.cloudevents.CloudEvent;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

import javax.inject.Inject;
import javax.ws.rs.core.Response;



@QuarkusTest
public class AWSRekognitionServiceTest {

    @InjectMock
    @RestClient
    BrokerService brokerService;

    @Inject
    AWSRekognitionService awsService;

    @Test
    public void analyzeImageLabelsFromS3BucketTest() throws Exception {

        Mockito.when(brokerService.send(any())).thenReturn(Response.accepted().build());
        ArgumentCaptor<CloudEvent> argument = ArgumentCaptor.forClass(CloudEvent.class);
        awsService.analyzeImageLabelsFromS3Bucket("test", "test");
        verify(brokerService).send(argument.capture());
        
        assertArrayEquals("{\"name\":\"test\",\"labels\":[\"dummy1\",\"dummy2\"]}".getBytes(), argument.getValue().getData().toBytes());
    }

    @Test
    public void analyzeImageLabelsFromS3BucketTestWithError() throws Exception {

        Mockito.when(brokerService.send(any())).thenReturn(Response.serverError().build());
        try {
            awsService.analyzeImageLabelsFromS3Bucket("test", "test");
            fail("Expected exception");
        } catch (Exception ex) {
            assertTrue(true);
        }
    }

    
}
