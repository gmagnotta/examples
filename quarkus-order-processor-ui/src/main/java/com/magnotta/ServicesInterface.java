package com.magnotta;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.gmagnotta.jaxb.TopItemsResponse;
import org.gmagnotta.jaxb.TopOrdersResponse;

@Path("/api")
public interface ServicesInterface {
    
    @GET
    @Path("/topOrders")
    @Produces({ MediaType.APPLICATION_XML })
    TopOrdersResponse getTopOrders();

    @GET
    @Path("/topItems")
    @Produces({ MediaType.APPLICATION_XML })
    TopItemsResponse getTopItems();

    @POST
    @Path("/reset")
    @Produces({ MediaType.TEXT_PLAIN })
    String doReset();

    @POST
    @Path("/rebuild")
    @Produces({ MediaType.TEXT_PLAIN })
    String rebuild();

    @POST
    @Path("/order/generate")
    @Produces({ MediaType.TEXT_PLAIN })
    String generate(@QueryParam("amount") @DefaultValue("1") int amount );
}
