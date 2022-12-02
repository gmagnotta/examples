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

import org.gmagnotta.model.LabelledFile;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventData;

@Path("/dummy")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class DummyResource {

    @Inject
    Validator validator;

    @Inject
    Logger logger;

    @Inject
    ObjectMapper mapper;

    @POST
    public Response receiveCloudEvent(CloudEvent cloudEvent) {

        Set<ConstraintViolation<CloudEvent>> violations = validator.validate(cloudEvent);
        if (!violations.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(violations).build();
        }

        String type = cloudEvent.getType();
        String source = cloudEvent.getSource().toString();

        CloudEventData data = cloudEvent.getData();

        LabelledFile model;
        try {

            model = mapper.readValue(data.toBytes(), LabelledFile.class);
            logger.info("Received " + type + "; " + source + "; " + model.name + "; " + model.labels);

        } catch (Exception e) {

            logger.error(e);

            return Response.status(Response.Status.BAD_REQUEST).build();

        }

        return Response.accepted().build();
    }
}