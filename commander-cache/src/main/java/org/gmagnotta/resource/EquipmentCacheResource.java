package org.gmagnotta.resource;

import java.util.ArrayList;
import java.util.List;

import org.gmagnotta.model.Equipment;
import org.gmagnotta.service.Cache;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/equipment")
public class EquipmentCacheResource {

    @Inject
    Cache cache;

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Equipment> getAll() {
        List<Equipment> retVal = new ArrayList<>();

        for (Equipment equipment : cache.equipmentHashMap.values()) {

            retVal.add(equipment);
        }

        return retVal;
    }
}
