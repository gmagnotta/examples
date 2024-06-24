package org.gmagnotta.resource;

import java.util.List;

import org.gmagnotta.model.Battalion;
import org.gmagnotta.service.Cache;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/battalion")
public class BattalionCacheResource {

    @Inject
    Cache cache;

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Battalion> getAll() {
        return (List) List.of(cache.battalionHashMap.values());
    }

}
