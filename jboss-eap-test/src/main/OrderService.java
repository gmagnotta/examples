package com.mycompany.app;


import java.util.List;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.mycompany.model.LineItem;
import com.mycompany.model.Order;

import jakarta.inject.Inject;

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
    
    @Resource(mappedName = "java:/jms/queue/TodoListQueue")
    private Queue queue;
    
    @Inject
    private JMSContext context;
    
    @GET
    public List<Order> getOrders() {
    	Query query = entityManager.createQuery("SELECT DISTINCT o FROM Order o INNER JOIN FETCH o.lineItems");
    	
    	return query.getResultList();
    }
    
    public void createOrder(Order order) {
    	
    	entityManager.persist(order);
    	
    	for (LineItem i : order.getLineItems()) {
    	
    		entityManager.persist(i);
    		
    	}
    	
    }
    
    @GET
    @Path("{id}")
    public List<Order> getOrders(@PathParam("id") int shop) {
    	
    	JMSProducer producer = context.createProducer();
    	
    	TextMessage message = context.createTextMessage("Hello");
    	
    	producer.send(queue, message);
    	
    	Query query = entityManager.createQuery("SELECT DISTINCT o FROM Order o INNER JOIN FETCH o.lineItems WHERE o.shop.id = :id");
    	
    	query.setParameter("id", shop);
    	
    	return query.getResultList();
    }
}
