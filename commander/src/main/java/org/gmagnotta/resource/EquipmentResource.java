package org.gmagnotta.resource;

import java.util.List;

import org.gmagnotta.model.Equipment;
import org.gmagnotta.service.EquipmentService;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/equipment")
public class EquipmentResource {

    @Inject
    EquipmentService equipmentService;

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Equipment> getAll() {
        return equipmentService.getAll();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Equipment getById(@PathParam(value = "id") long id) {
        return equipmentService.getById(id);
    }
}
