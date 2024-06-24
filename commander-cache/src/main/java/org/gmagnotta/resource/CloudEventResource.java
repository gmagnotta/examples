package org.gmagnotta.resource;

import org.gmagnotta.model.Battalion;
import org.gmagnotta.model.Equipment;
import org.gmagnotta.model.Member;
import org.gmagnotta.service.Cache;
import org.jboss.logging.Logger;
import org.json.JSONObject;

import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.transaction.Transactional;
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
    @Transactional
    public Response receiveEvent(String cloudEvent) throws Exception {

        try {

            JSONObject json = new JSONObject(cloudEvent);

            String id = json.getString("id");
            String table = json.getString("iodebeziumtable");
    
            logger.info("Read id + " + id + ", table " + table);
    
            JSONObject object = json.getJSONObject("data");
            JSONObject payload = object.getJSONObject("payload");
            JSONObject content = payload.getJSONObject("after");
    
            Jsonb jsonb = JsonbBuilder.create();
            
            // battalion, equipment, member
            if ("battalion".equals(table)) {
    
                Battalion battalion = jsonb.fromJson(content.toString(), Battalion.class);
                logger.info("Read battalion " + battalion);

                cache.battalionHashMap.put(battalion.id, battalion);
            
            } else if ("equipment".equals(table)) {
            
                Equipment equipment = jsonb.fromJson(content.toString(), Equipment.class);
                logger.info("Read equipment " + equipment);

                cache.equipmentHashMap.put(equipment.id, equipment);
            
            } else if ("member".equals(table)) {
            
                Member member = jsonb.fromJson(content.toString(), Member.class);
                logger.info("Read member " + member);

                cache.memberHashMap.put(member.id, member);
            }
    
    
        } catch (Exception e) {

            logger.error(e);

            return Response.status(Response.Status.BAD_REQUEST).build();

        }


 

        return Response.accepted().build();
    }
}