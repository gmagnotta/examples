package com.mycompany.app.service;

import java.util.List;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.Queue;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import javax.enterprise.event.Event;

import com.mycompany.event.ExportedEvent;
import com.mycompany.event.impl.OrderCreatedEvent;
import com.mycompany.model.LineItem;
import com.mycompany.model.Order;

/**
 * Hello world!
 *
 */
@Stateless
public class OrderService
{
    @PersistenceContext(unitName = "store")
	private EntityManager entityManager;

    @Inject
    private JMSContext context;

    @Resource(lookup="java:global/remoteContext/orderCommand")
    private Queue queue;

    @Inject
    Event<ExportedEvent> event;
    
    public List<Order> getOrders() {

    	Query query = entityManager.createNamedQuery("getAllOrders", Order.class);
    	
    	return query.getResultList();
    }
    
    @Transactional
    public void createOrder(Order order) throws Exception {
    	
    	entityManager.persist(order);
    	
    	for (LineItem i : order.getLineItems()) {
    	
    		entityManager.persist(i);
    		
    	}

        // fire order created event
        event.fire(OrderCreatedEvent.of(order));

    }
    
}
