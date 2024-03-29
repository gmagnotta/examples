package org.gmagnotta.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.gmagnotta.model.LabelledFile;
import org.jboss.logging.Logger;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.AmazonRekognitionException;
import com.amazonaws.services.rekognition.model.DetectLabelsRequest;
import com.amazonaws.services.rekognition.model.DetectLabelsResult;
import com.amazonaws.services.rekognition.model.DetectTextRequest;
import com.amazonaws.services.rekognition.model.DetectTextResult;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.Label;
import com.amazonaws.services.rekognition.model.S3Object;
import com.amazonaws.services.rekognition.model.TextDetection;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;

@RequestScoped
public class AWSRekognitionService {

    @Inject
    Logger logger;

    @ConfigProperty(name = "dummy_mode")
    boolean dummyMode;

    @RestClient
    BrokerService brokerService;

    @Inject
    ObjectMapper mapper;

    // see
    // https://docs.aws.amazon.com/rekognition/latest/dg/labels-detect-labels-image.html
    public void analyzeImageLabelsFromS3Bucket(String bucket, String objectKey) throws Exception {

        if (bucket == null || bucket.length() == 0 || objectKey == null || objectKey.length() == 0) {
            throw new Exception("Invalid parameters");
        }

        List<String> fileLabels = new ArrayList<>();

        try {

            // Dummy mode?
            if (dummyMode) {

                fileLabels.add("dummy1");
                fileLabels.add("dummy2");

            } else {

                // credentials and region are read from environment variable
                // see EnvironmentVariableCredentialsProvider and
                AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder
                        .standard()
                        .build();

                DetectLabelsRequest request = new DetectLabelsRequest()
                        .withImage(new Image()
                                .withS3Object(new S3Object().withName(objectKey).withBucket(bucket)))
                        .withMaxLabels(10).withMinConfidence(75F);

                DetectLabelsResult result = rekognitionClient.detectLabels(request);
                List<Label> labels = result.getLabels();

                logger.info("Detected labels for " + objectKey);
                for (Label label : labels) {
                    logger.info("Label: " + label.getName());
                    logger.info("Confidence: " + label.getConfidence().toString());

                    fileLabels.add(label.getName());

                    /*
                     * List<Instance> instances = label.getInstances();
                     * System.out.println("Instances of " + label.getName());
                     * if (instances.isEmpty()) {
                     * System.out.println("  " + "None");
                     * } else {
                     * for (Instance instance : instances) {
                     * System.out.println("  Confidence: " + instance.getConfidence().toString());
                     * System.out.println("  Bounding box: " +
                     * instance.getBoundingBox().toString());
                     * }
                     * }
                     * System.out.println("Parent labels for " + label.getName() + ":");
                     * List<Parent> parents = label.getParents();
                     * if (parents.isEmpty()) {
                     * System.out.println("  None");
                     * } else {
                     * for (Parent parent : parents) {
                     * System.out.println("  " + parent.getName());
                     * }
                     * }
                     */

                }

            }

            if (fileLabels.size() > 0) {

                LabelledFile labelledFile = new LabelledFile();
                labelledFile.name = objectKey;
                labelledFile.labels = fileLabels;

                // Build cloud Event
                CloudEvent event = CloudEventBuilder.v1()
                        .withId(UUID.randomUUID().toString())
                        .withType("com.gmagnotta.events/filelabel")
                        .withSource(URI.create("http://cloud-event-labeller"))
                        .withData("application/json", mapper.writeValueAsBytes(labelledFile))
                        .build();

                // Send
                logger.info("Sending CloudEvent " + mapper.writeValueAsString(labelledFile));
                Response response = brokerService.send(event);
                
                if (response.getStatus() != Response.Status.ACCEPTED.getStatusCode()) {
                    
                    logger.error("Received status " + response.getStatus());

                    throw new Exception("Received status " + response.getStatus());
                }

            } else {

                logger.info("No labels detected!");
                
            }

        } catch (AmazonRekognitionException e) {

            logger.error(e);

            throw new Exception(e);

        }

    }

    public void analyzeImageTextFromS3Bucket(String bucket, String objectKey) throws Exception {

        if (bucket == null || bucket.length() == 0 || objectKey == null || objectKey.length() == 0) {
            throw new Exception("Invalid parameters");
        }

        List<String> fileText = new ArrayList<>();

        try {

            // Dummy mode?
            if (dummyMode) {

                fileText.add("dummy text");

            } else {

                // credentials and region are read from environment variable
                // see EnvironmentVariableCredentialsProvider and
                AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder
                        .standard()
                        .build();

                DetectTextRequest request = new DetectTextRequest()
                        .withImage(new Image()
                                .withS3Object(new S3Object().withName(objectKey).withBucket(bucket)));

                DetectTextResult result = rekognitionClient.detectText(request);
                List<TextDetection> textDetections = result.getTextDetections();

                logger.info("Detected texts for " + objectKey);
                for (TextDetection text : textDetections) {
                    logger.info("Text: " + text.getDetectedText());
                    logger.info("Confidence: " + text.getConfidence().toString());

                    if (text.getParentId() == null) {
                        // add only element with parent id with null (a complete line)
                        fileText.add(text.getDetectedText());
                    }

                }

            }

            // Send text only if there are elements
            if (fileText.size() > 0) {

                LabelledFile labelledFile = new LabelledFile();
                labelledFile.name = objectKey;
                labelledFile.labels = fileText;

                // Build cloud Event
                CloudEvent event = CloudEventBuilder.v1()
                        .withId(UUID.randomUUID().toString())
                        .withType("com.gmagnotta.events/filelabel")
                        .withSource(URI.create("http://cloud-event-labeller"))
                        .withData("application/json", mapper.writeValueAsBytes(labelledFile))
                        .build();

                // Send
                logger.info("Sending CloudEvent " + mapper.writeValueAsString(labelledFile));
                Response response = brokerService.send(event);
                
                if (response.getStatus() != Response.Status.ACCEPTED.getStatusCode()) {
                    
                    logger.error("Received status " + response.getStatus());

                    throw new Exception("Received status " + response.getStatus());
                }

            } else {
                logger.info("No text detected!");
            }

        } catch (AmazonRekognitionException e) {

            logger.error(e);

            throw new Exception(e);

        }

    }
}
