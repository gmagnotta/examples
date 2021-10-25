package com.magnotta;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.gmagnotta.jaxb.Aggregationtype;

@Path("/api")
public interface ServicesInterface {
    
    @GET
    @Path("/topOrders")
    @Produces({ MediaType.APPLICATION_XML })
    Aggregationtype getTopOrders();

    @GET
    @Path("/topItems")
    @Produces({ MediaType.APPLICATION_XML })
    Aggregationtype getTopItems();

}
