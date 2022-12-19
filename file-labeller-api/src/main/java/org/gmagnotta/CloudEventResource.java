package org.gmagnotta;

import java.util.Set;

import javax.inject.Inject;
import javax.transaction.Transactional;
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

    @POST
    @Transactional
    public Response hello(CloudEvent cloudEvent) {

        Set<ConstraintViolation<CloudEvent>> violations = validator.validate(cloudEvent);
        if (!violations.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(violations).build();
        }

        String type = cloudEvent.getType();
        String source = cloudEvent.getSource().toString();

        CloudEventData data = cloudEvent.getData();

        org.gmagnotta.model.LabelledFile model;
        try {

            model = mapper.readValue(data.toBytes(), org.gmagnotta.model.LabelledFile.class);
            logger.info("Received " + type + "; " + source + "; " + model.name + "; " + model.labels);

            Set<ConstraintViolation<LabelledFile>> modelViolations = validator.validate(model);

            if (!modelViolations.isEmpty()) {
                throw new Exception("Invalid parameters");
            }

        } catch (Exception e) {

            logger.error(e);

            return Response.status(Response.Status.BAD_REQUEST).build();

        }


        org.gmagnotta.persistence.LabelledFile file = org.gmagnotta.persistence.LabelledFile.findByIdForUpdate(model.name);

        if (file == null) {
            file = new org.gmagnotta.persistence.LabelledFile();
            file.setName(model.name);
            file.setLabels(model.labels);
        } else {
            file.appendLabels(model.labels);
        }
        
        file.persistAndFlush();

        return Response.accepted().build();
    }
}