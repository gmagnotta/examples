package com.mycompany.app.service;


import java.io.StringWriter;
import java.math.BigInteger;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.jms.Queue;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import com.mycompany.model.LineItem;
import com.mycompany.model.Order;

import org.gmagnotta.jaxb.Lineitemtype;
import org.gmagnotta.jaxb.ObjectFactory;
import org.gmagnotta.jaxb.Ordertype;

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

    @Resource(lookup="java:global/remoteContext/HelloWorldMDBQueue")
    private Queue queue;
    
    @GET
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

        JAXBContext jaxbContext = JAXBContext.newInstance(Ordertype.class);
        Marshaller mar = jaxbContext.createMarshaller();

        Ordertype ordertype = new Ordertype();
        ordertype.setOrderid(order.getId() + "");

        for (LineItem i : order.getLineItems()) {
        
            Lineitemtype lineItemtype = new Lineitemtype();
            lineItemtype.setItemid(String.valueOf(i.getItem().getId()));
            lineItemtype.setNote("none");
            lineItemtype.setQuantity(BigInteger.valueOf(i.getQuantity()));

            ordertype.getLineitem().add(lineItemtype);
        
        }

        mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        StringWriter sw = new StringWriter();
        mar.marshal(new ObjectFactory().createOrder(ordertype), sw);

        Destination destination = queue;
        //String text = "Order created with id " + order.getId();
        context.createProducer().send(destination, sw.toString());
    }
    
}
