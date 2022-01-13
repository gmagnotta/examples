package com.mycompany.app.service;


import java.io.StringWriter;
import java.math.BigInteger;
import java.util.Date;
import java.util.GregorianCalendar;
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
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.mycompany.model.LineItem;
import com.mycompany.model.Order;

import org.gmagnotta.jaxb.Lineitemtype;
import org.gmagnotta.jaxb.ObjectFactory;
import org.gmagnotta.jaxb.OrderCommandRequest;
import org.gmagnotta.jaxb.OrderCommandRequestEnum;
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

        JAXBContext jaxbContext = JAXBContext.newInstance(OrderCommandRequest.class);
        Marshaller mar = jaxbContext.createMarshaller();

        org.gmagnotta.jaxb.Order jaxbOrder = new org.gmagnotta.jaxb.Order();
        
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(new Date());
        XMLGregorianCalendar date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        jaxbOrder.setCreationDate(date2);
        jaxbOrder.setExternalOrderId(order.getId() + "");

        for (LineItem i : order.getLineItems()) {
        
            org.gmagnotta.jaxb.Item item = new org.gmagnotta.jaxb.Item();
			item.setId(Integer.valueOf(i.getItem().getId()));
            
            org.gmagnotta.jaxb.LineItem jaxbLineItem = new org.gmagnotta.jaxb.LineItem();
            jaxbLineItem.setItem(item);
            jaxbLineItem.setQuantity(i.getQuantity());

            jaxbOrder.getLineItem().add(jaxbLineItem);
        
        }

        OrderCommandRequest request = new OrderCommandRequest();
        request.setOrder(jaxbOrder);
        request.setOrderCommandEnum(OrderCommandRequestEnum.ORDER_RECEIVED);

        mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        StringWriter sw = new StringWriter();
        mar.marshal(new ObjectFactory().createOrderCommandRequest(request), sw);

        Destination destination = queue;
        //String text = "Order created with id " + order.getId();
        context.createProducer().send(destination, sw.toString());
    }
    
}
