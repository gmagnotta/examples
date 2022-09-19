package org.gmagnotta;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.gmagnotta.jaxb.TopItemsResponse;
import org.gmagnotta.jaxb.TopOrdersResponse;
import org.gmagnotta.jaxb.TopValue;
import org.gmagnotta.model.Order;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.Search;
import org.infinispan.commons.util.CloseableIterator;
import org.infinispan.commons.util.CloseableIteratorSet;
import org.infinispan.query.dsl.QueryFactory;
import org.jboss.logging.Logger;

import io.quarkus.infinispan.client.Remote;

@Path("/api")
public class OrderResource {

    @Inject
    Logger logger;

    @Inject
    @Remote("orders")
    RemoteCache<String, Order> orderCache;

    @Inject
    @Remote("toporders")
    RemoteCache<Integer, Integer> topOrderCache;

    @Inject
    @Remote("topitems")
    RemoteCache<Integer, Integer> topItemsCache;

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

        // org.infinispan.query.dsl.Query<Object[][]> q = qf.create("SELECT
        // sum(quantity), orderid FROM library.DenormalizedLineItem GROUP BY orderid
        // ORDER by sum(quantity) desc, orderid asc");
        org.infinispan.query.dsl.Query<Order> q = qf.create("FROM library.Order where user = :user");
        q.setParameter("user", username);

        return q.execute().list();

    }

    @GET
    @Path("topItems")
    @Produces(MediaType.APPLICATION_XML)
    public TopItemsResponse getTopItems() {

        CloseableIteratorSet<Integer> keySet = topItemsCache.keySet();

        CloseableIterator<Integer> iterator = keySet.iterator();

        List<TopValue> result = new ArrayList<TopValue>();

        while (iterator.hasNext()) {
            Integer key = iterator.next();

            Integer el = topItemsCache.get(key);

            TopValue top = new TopValue();

            top.setId(key.intValue());
            top.setValue(el.intValue());
            result.add(top);
        }

        TopItemsResponse response = new TopItemsResponse();

        response.setStatus("DATAGRID");
        response.getTopvalue().addAll(result);

        return response;
    }

    @GET
    @Path("topOrders")
    @Produces(MediaType.APPLICATION_XML)
    public TopOrdersResponse getTopOrders() {

        CloseableIteratorSet<Integer> keySet = topOrderCache.keySet();

        CloseableIterator<Integer> iterator = keySet.iterator();

        List<TopValue> result = new ArrayList<TopValue>();

        while (iterator.hasNext()) {
            Integer key = iterator.next();

            Integer el = topOrderCache.get(key);

            TopValue top = new TopValue();

            top.setId(key.intValue());
            top.setValue(el.intValue());
            result.add(top);
        }

        TopOrdersResponse response = new TopOrdersResponse();

        response.setStatus("DATAGRID");
        response.getTopvalue().addAll(result);

        return response;

    }

}