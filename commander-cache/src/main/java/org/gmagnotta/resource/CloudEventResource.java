package org.gmagnotta.resource;

import org.gmagnotta.service.Cache;
import org.jboss.logging.Logger;
import org.json.JSONObject;

import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MediaType;

@Path("/")
@Consumes("application/cloudevents+json")
@Produces(MediaType.APPLICATION_JSON)
public class CloudEventResource {

    @Inject
    Logger logger;

    @Inject
    Cache cache;

    @POST
    public Response receiveEvent(String cloudEvent) throws Exception {

        try {
            logger.info("Read payload " + cloudEvent);


            JSONObject json = new JSONObject(cloudEvent);

            String id = json.getString("id");
            String table = json.getString("iodebeziumtable");
    
            JSONObject object = json.getJSONObject("data");
            JSONObject payload = object.getJSONObject("payload");
            JSONObject content = payload.getJSONObject("after");
    
            Jsonb jsonb = JsonbBuilder.create();
            
            // battalion, equipment, member
            if ("battalion".equals(table)) {
    
                org.gmagnotta.model.Battalion battalion = jsonb.fromJson(content.toString(), org.gmagnotta.model.Battalion.class);
                logger.info("Received battalion " + battalion);

                cache.battalionHashMap.put(battalion.getId(), battalion);
            
            } else if ("equipment".equals(table)) {
            
                org.gmagnotta.model.Equipment equipment = jsonb.fromJson(content.toString(), org.gmagnotta.model.Equipment.class);
                logger.info("Received equipment " + equipment);

                cache.equipmentHashMap.put(equipment.getId(), equipment);

            } else if ("member".equals(table)) {
            
                org.gmagnotta.model.Member member = jsonb.fromJson(content.toString(), org.gmagnotta.model.Member.class);
                logger.info("Received member " + member);

                cache.memberHashMap.put(member.getId(), member);
            }
    
    
        } catch (Exception e) {

            logger.error(e);

            return Response.status(Response.Status.BAD_REQUEST).build();

        }

        return Response.accepted().build();
    }
}