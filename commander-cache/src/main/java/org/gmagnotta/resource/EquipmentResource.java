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

@Path("/equipment")
public class EquipmentResource {

    @Inject
    Cache cache;

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public List<org.gmagnotta.dto.Equipment> getAll() {

        List<org.gmagnotta.dto.Equipment> retVal = new ArrayList<>();

        for (org.gmagnotta.model.Equipment equipment : cache.equipmentHashMap.values()) {

            org.gmagnotta.dto.Equipment dtoEquipment = ConversionUtils.toDTO(equipment);

            retVal.add(dtoEquipment);
        }

        return retVal;
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public org.gmagnotta.dto.Equipment getById(@PathParam(value = "id") long id) {
        org.gmagnotta.model.Equipment equipment = cache.equipmentHashMap.get(Long.valueOf(id));

        if (equipment != null) {
            return ConversionUtils.toDTO(equipment);
        }

        return null;
    }
}
