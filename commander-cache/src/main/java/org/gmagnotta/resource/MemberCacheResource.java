package org.gmagnotta.resource;

import java.util.List;

import org.gmagnotta.model.Member;
import org.gmagnotta.service.Cache;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/member")
public class MemberCacheResource {

    @Inject
    Cache cache;

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Member> getAll() {

        return (List) List.of(cache.memberHashMap.values());
    }


}
