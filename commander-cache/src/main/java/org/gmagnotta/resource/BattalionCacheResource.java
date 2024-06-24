package org.gmagnotta.resource;

import java.util.ArrayList;
import java.util.List;

import org.gmagnotta.model.Equipment;
import org.gmagnotta.model.Member;
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
    public List<org.gmagnotta.model.relational.Battalion> getAll() {

        List<org.gmagnotta.model.relational.Battalion> retVal = new ArrayList<>();

        for (org.gmagnotta.model.Battalion battalion : cache.battalionHashMap.values()) {
            org.gmagnotta.model.relational.Battalion rBattalion = new org.gmagnotta.model.relational.Battalion();

            rBattalion.id = battalion.id;
            rBattalion.setName(battalion.getName());
            rBattalion.setDescription(battalion.getDescription());
            rBattalion.setStatus(battalion.getStatus());
            rBattalion.setAltitude(battalion.getAltitude());
            rBattalion.setLongitude(battalion.getLongitude());
            rBattalion.setLatitude(battalion.getLatitude());

            for (Member member : cache.memberHashMap.values()) {
                
                if (member.getBattalion().equals(rBattalion.id)) {

                    rBattalion.getMembers().add(member);

                }
            }

            for (Equipment equipment : cache.equipmentHashMap.values()) {

                if (equipment.getBattalion().equals(rBattalion.id)) {

                    rBattalion.getEquipments().add(equipment);
                }
            }

            retVal.add(rBattalion);
        }

        return retVal;
    }

}
