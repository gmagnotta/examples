package org.gmagnotta;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.gmagnotta.model.Order;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.Search;
import org.infinispan.query.dsl.QueryFactory;
import org.jboss.logging.Logger;

import io.quarkus.infinispan.client.Remote;

@Path("/orders")
public class OrderResource {

    @Inject
	Logger logger;

    @Inject
	@Remote("orders")
    RemoteCache<String, Order> orderCache;

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Order getById(@PathParam("id") String id) {

        return orderCache.get(id);
        
    }

    @GET
    @Path("/user/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Order> getByUser(@PathParam("username") String username) {

        QueryFactory qf = Search.getQueryFactory(orderCache);
	            	
	    //org.infinispan.query.dsl.Query<Object[][]> q = qf.create("SELECT sum(quantity), orderid FROM library.DenormalizedLineItem GROUP BY orderid ORDER by sum(quantity) desc, orderid asc");
        org.infinispan.query.dsl.Query<Order> q = qf.create("FROM library.Order where user = :user");
        q.setParameter("user", username);

        return q.execute().list();

    }

}