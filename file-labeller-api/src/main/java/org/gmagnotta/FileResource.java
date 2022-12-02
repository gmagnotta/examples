package org.gmagnotta;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.logging.Logger;

@Path("/file")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FileResource {

    @Inject
    Logger logger;

    @GET
    @Path("/{name}")
    public org.gmagnotta.model.LabelledFile getFileByName(@PathParam("name") String name) {

        logger.info("Searching " + name);

        org.gmagnotta.persistence.LabelledFile file = org.gmagnotta.persistence.LabelledFile.findByName(name);

        if (file == null) {
            throw new NotFoundException("Unknown file: " + name);
        }

        org.gmagnotta.model.LabelledFile retValue = new org.gmagnotta.model.LabelledFile();
        retValue.name = file.getName();
        retValue.labels = file.getLabels();

        return retValue;

    }
    
}
