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

@Path("/battalion")
public class BattalionResource {

    @Inject
    Cache cache;

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public List<org.gmagnotta.dto.Battalion> getAll() {

        List<org.gmagnotta.dto.Battalion> retVal = new ArrayList<>();

        for (org.gmagnotta.model.Battalion battalion : cache.battalionHashMap.values()) {

            org.gmagnotta.dto.Battalion dtoBattalion = ConversionUtils.toDTO(battalion);

            retVal.add(ConversionUtils.updateBattalionFromCache(dtoBattalion, cache.equipmentHashMap, cache.memberHashMap));

        }

        return retVal;
    }


    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public org.gmagnotta.dto.Battalion getById(@PathParam(value = "id") long id) {
        org.gmagnotta.model.Battalion battalion = cache.battalionHashMap.get(Long.valueOf(id));

        if (battalion != null) {

            org.gmagnotta.dto.Battalion dtoBattalion =  ConversionUtils.toDTO(battalion);

            return ConversionUtils.updateBattalionFromCache(dtoBattalion, cache.equipmentHashMap, cache.memberHashMap);
        }

        return null;
    }

}
