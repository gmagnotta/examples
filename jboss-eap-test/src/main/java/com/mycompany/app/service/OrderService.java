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

import com.mycompany.app.service.utils.OrderUtils;
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

    @Resource(lookup="java:global/remoteContext/HelloWorldMDBQueue")
    private Queue queue;
    
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

        String string = OrderUtils.marshallOrder(order);

        context.createProducer().send(queue, string);
    }
    
}
