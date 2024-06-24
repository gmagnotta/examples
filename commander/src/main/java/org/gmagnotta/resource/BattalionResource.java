package org.gmagnotta.resource;

import java.util.List;

import org.gmagnotta.model.Battalion;
import org.gmagnotta.service.BattalionService;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/battalion")
public class BattalionResource {

    @Inject
    BattalionService battalionService;

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Battalion> getAll() {
        return battalionService.getAll();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Battalion getById(@PathParam(value = "id") long id) {
        return battalionService.getById(id);
    }

}
