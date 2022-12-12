package org.gmagnotta.resource;

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
import org.gmagnotta.model.S3EventNotification;
import org.gmagnotta.service.AWSRekognitionService;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventData;

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

    @Inject
    AWSRekognitionService AWSRekognitionService;

    @ConfigProperty(name = "detect_mode")
    String detectMode;

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

        S3EventNotification model;
        try {

            model = mapper.readValue(data.toBytes(), S3EventNotification.class);
            logger.info("Received type " + type + "; source " + source + "; model " + model);

        } catch (Exception e) {

            logger.error(e);

            return Response.status(Response.Status.BAD_REQUEST).build();

        }

        try {

            if ("label".equalsIgnoreCase(detectMode)) {
                AWSRekognitionService.analyzeImageLabelsFromS3Bucket(model.Records[0].s3.bucket.name, model.Records[0].s3.object.key);

            } else if ("text".equalsIgnoreCase(detectMode)) {

                AWSRekognitionService.analyzeImageTextFromS3Bucket(model.Records[0].s3.bucket.name, model.Records[0].s3.object.key);

            } else {

                throw new Exception("Unknown detect mode " + detectMode);

            }

        } catch (Exception e) {

            logger.error(e);

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();

        }

        return Response.accepted().build();
    }
}