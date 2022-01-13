package com.mycompany.app.service;


import java.util.List;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSDestinationDefinition;
import javax.jms.JMSDestinationDefinitions;
import javax.jms.Queue;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.mycompany.model.LineItem;
import com.mycompany.model.Order;

@JMSDestinationDefinitions(
    value = {
        @JMSDestinationDefinition(
            name = "java:/queue/HELLOWORLDMDBQueue",
            interfaceName = "javax.jms.Queue",
            destinationName = "HelloWorldMDBQueue"
        ),
    }
)

/**
 * Hello world!
 *
 */
@Stateless
@Path("order")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class OrderService 
{
    @PersistenceContext(unitName = "store")
	private EntityManager entityManager;

    @Inject
    private JMSContext context;

    @Resource(lookup = "java:/queue/HELLOWORLDMDBQueue")
    private Queue queue;
    
    @GET
    public List<Order> getOrders() {

    	Query query = entityManager.createNamedQuery("getAllOrders", Order.class);
    	
    	return query.getResultList();
    }
    
    @Transactional
    public void createOrder(Order order) {
    	
    	entityManager.persist(order);
    	
    	for (LineItem i : order.getLineItems()) {
    	
    		entityManager.persist(i);
    		
    	}
    	
    }

    public void notifyOrder(Order order) {

        String text = "Order created with id: " + order.getId();
        context.createProducer().send(queue, text);

    }
    
}
