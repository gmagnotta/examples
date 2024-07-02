package org.gmagnotta.resource;

import java.util.ArrayList;
import java.util.List;

import org.gmagnotta.service.Cache;
import org.gmagnotta.utils.ConversionUtils;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/member")
public class MemberResource {

    @Inject
    Cache cache;

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public List<org.gmagnotta.dto.Member> getAll() {

        List<org.gmagnotta.dto.Member> retVal = new ArrayList<>();

        for (org.gmagnotta.model.Member member : cache.memberHashMap.values()) {

            org.gmagnotta.dto.Member dtoMember = ConversionUtils.toDTO(member);

            retVal.add(dtoMember);
        }

        return retVal;
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public org.gmagnotta.dto.Member getById(@PathParam(value = "id") long id) {
        org.gmagnotta.model.Member member = cache.memberHashMap.get(Long.valueOf(id));

        if (member != null) {
            return ConversionUtils.toDTO(member);
        }

        return null;
    }

}
