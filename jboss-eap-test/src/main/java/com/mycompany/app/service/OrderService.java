package com.mycompany.app.service;


import java.util.List;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.JMSConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.ObjectMessage;
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
    
    // @Resource(mappedName = "java:/jms/queue/TodoListQueue")
    // private Queue queue;
    
    // @Inject
    // @JMSConnectionFactory("java:jboss/DefaultJMSConnectionFactory")
    // private JMSContext context;
    
    @GET
    public List<Order> getOrders() {

    	Query query = entityManager.createNamedQuery("getAllOrders", Order.class);
    	
    	return query.getResultList();
    }
    
    public void createOrder(Order order) {
    	
    	entityManager.persist(order);
    	
    	for (LineItem i : order.getLineItems()) {
    	
    		entityManager.persist(i);
    		
    	}
    	
    }
    
    public void createOrderOnJms(Order order) {
    	
    	// JMSProducer producer = context.createProducer();
    	
    	// ObjectMessage obj = context.createObjectMessage();
    	
    	// try {
		// 	obj.setObject(order);
		// 	obj.setObjectProperty("id", order.getId());
		// } catch (JMSException e) {
		// 	e.printStackTrace();
		// }
    	
    	// //TextMessage message = context.createTextMessage("Order created with id " + order.getId());
    	
    	// producer.send(queue, obj);
    }
    
    @GET
    @Path("{id}")
    public List<Order> getOrders(@PathParam("id") int shop) {
    	
//    	JMSProducer producer = context.createProducer();
//    	
//    	TextMessage message = context.createTextMessage("Order created with id " + shop);
//    	
//    	producer.send(queue, message);
//    	
    	Query query = entityManager.createNamedQuery("getOrderById");
    	
    	query.setParameter("id", shop);
    	
    	return query.getResultList();
    }
}
