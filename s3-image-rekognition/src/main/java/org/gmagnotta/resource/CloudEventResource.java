package org.gmagnotta.resource;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.gmagnotta.model.LabelledFile;
import org.gmagnotta.model.S3UploadedObject;
import org.gmagnotta.service.BrokerService;
import org.jboss.logging.Logger;

import com.amazonaws.services.rekognition.model.DetectLabelsRequest;
import com.amazonaws.services.rekognition.model.DetectLabelsResult;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.Label;
import com.amazonaws.services.rekognition.model.S3Object;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.AmazonRekognitionException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventData;
import io.cloudevents.core.builder.CloudEventBuilder;

@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CloudEventResource {

    @Inject
    Validator validator;

    @Inject
    Logger logger;

    @Inject
    ObjectMapper mapper;

    @ConfigProperty(name = "aws_access_key_id")
    String accessKeyId;

    @ConfigProperty(name = "aws_secret_access_key")
    String secretAccesSKey;

    @ConfigProperty(name = "aws_region")
    String awsRegion;

    @ConfigProperty(name = "dummy_mode")
    boolean dummyMode;

    @RestClient
    BrokerService brokerService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response receiveCloudEvent(CloudEvent cloudEvent) {

        Set<ConstraintViolation<CloudEvent>> violations = validator.validate(cloudEvent);
        if (!violations.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(violations).build();
        }

        String type = cloudEvent.getType();
        String source = cloudEvent.getSource().toString();

        CloudEventData data = cloudEvent.getData();

        S3UploadedObject model;
        try {

            model = mapper.readValue(data.toBytes(), S3UploadedObject.class);
            logger.info("Received " + type + "; " + source + "; " + model.bucket + "; " + model.objectKey);

        } catch (Exception e) {

            logger.error(e);

            return Response.status(Response.Status.BAD_REQUEST).build();

        }

        // see
        // https://docs.aws.amazon.com/rekognition/latest/dg/labels-detect-labels-image.html

        AWSCredentialsProvider credentialsProvider = new AWSCredentialsProvider() {

            @Override
            public AWSCredentials getCredentials() {

                return new AWSCredentials() {

                    @Override
                    public String getAWSAccessKeyId() {
                        return accessKeyId;
                    }

                    @Override
                    public String getAWSSecretKey() {
                        return secretAccesSKey;
                    }

                };
            }

            @Override
            public void refresh() {
            }

        };

        try {

            List<String> fileLabels = new ArrayList<>();

            // Dummy mode?
            if (dummyMode) {

                fileLabels.add("dummy1");
                fileLabels.add("dummy2");

            } else {

                try {

                    AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder
                            .standard()
                            .withCredentials(credentialsProvider)
                            .withRegion(awsRegion)
                            .build();

                    DetectLabelsRequest request = new DetectLabelsRequest()
                            .withImage(new Image()
                                    .withS3Object(new S3Object().withName(model.objectKey).withBucket(model.bucket)))
                            .withMaxLabels(10).withMinConfidence(75F);

                    DetectLabelsResult result = rekognitionClient.detectLabels(request);
                    List<Label> labels = result.getLabels();

                    logger.info("Detected labels for " + model.objectKey);
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

                } catch (AmazonRekognitionException e) {

                    logger.error(e);

                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();

                }
            }

            LabelledFile labelledFile = new LabelledFile();
            labelledFile.name = model.objectKey;
            labelledFile.labels = fileLabels;

            // Build cloud Event
            CloudEvent event = CloudEventBuilder.v1()
                    .withId("dummy")
                    .withType("com.gmagnotta.events/filelabel")
                    .withSource(URI.create("http://cloud-event-labeller"))
                    .withData("application/json", mapper.writeValueAsBytes(labelledFile))
                    .build();

            // Send
            logger.info("Sending CloudEvent " + mapper.writeValueAsString(labelledFile));
            brokerService.send(event);

        } catch (JsonProcessingException e) {

            logger.error(e);

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();

        }

        return Response.accepted().build();
    }
}