package org.gmagnotta.observer;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.gmagnotta.jaxb.Item;
import org.gmagnotta.jaxb.LineItem;
import org.gmagnotta.jaxb.ObjectFactory;
import org.gmagnotta.jaxb.OrderChangeEvent;
import org.gmagnotta.jaxb.OrderChangeEventEnum;
import org.gmagnotta.jaxb.OrderCommandRequest;
import org.gmagnotta.jaxb.OrderCommandRequestEnum;
import org.gmagnotta.jaxb.OrderCommandResponse;
import org.gmagnotta.jaxb.PersistedItem;
import org.gmagnotta.jaxb.PersistedLineItem;
import org.gmagnotta.jaxb.PersistedOrder;
import org.gmagnotta.jaxb.TopValue;
import org.gmagnotta.utils.Utils;
import org.jboss.logging.Logger;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
public class CommandObserver implements MessageListener {
	
	private static final Logger LOGGER = Logger.getLogger(CommandObserver.class);
	
	private static final String COMMAND_QUEUE = "orderCommand";
	
	private static final String ORDER_CREATED_QUEUE = "orderCreated";
	
	private static final String INVALID_MESSAGE_QUEUE = "invalidMessage";

    @Inject
    ConnectionFactory connectionFactory;
    
    @Inject
    EntityManager entityManager;
    
    JMSContext context;
    
    JMSConsumer consumer;
    
    Queue invalidMessageQueue;
    
	@PostConstruct
	private void init() {
		
		context = connectionFactory.createContext();
    	
     	Queue commandQueue = context.createQueue(COMMAND_QUEUE);
     	
     	consumer = context.createConsumer(commandQueue);
     	
     	invalidMessageQueue = context.createQueue(INVALID_MESSAGE_QUEUE);

	}
    
     void onStart(@Observes StartupEvent ev) throws JMSException {

     	consumer.setMessageListener(this);
     	
     	LOGGER.info("Consumer started");

    }

    void onStop(@Observes ShutdownEvent ev) throws JMSException {
    	
    	consumer.close();
    	
    	context.close();
    	
    	LOGGER.info("Consumer stopped");
    	
    }

	@Override
	@Transactional
	public void onMessage(Message message) {
		
		try {
			
            if (message == null || !(message instanceof TextMessage)) {
            	
            	LOGGER.warn("Received unexpected message");
            	
            	context.createProducer().send(invalidMessageQueue, message);
            	
            } else {
            	
            	TextMessage requestMessage = (TextMessage) message;
            	
//            	LOGGER.info("Received content " + requestMessage.getText());
            	
            	OrderCommandRequest orderCommandRequest = Utils.unmarshall(OrderCommandRequest.class, requestMessage.getText());
            	
	            LOGGER.info("Received message id " + message.getJMSMessageID());
	            
	            if (orderCommandRequest.getOrderCommandEnum().equals(OrderCommandRequestEnum.ORDER_RECEIVED)) {
	            	
	            	org.gmagnotta.jaxb.Order jaxbOrder = orderCommandRequest.getOrder();
	            	
	            	org.gmagnotta.model.Order jpaOrder = convertoToJpaOrder(jaxbOrder);
	            	
	            	entityManager.persist(jpaOrder);
	                for (org.gmagnotta.model.LineItem l : jpaOrder.getLineItems()) {
	                    entityManager.persist(l);
	                }
	                
	                LOGGER.info("Persisted order " + jpaOrder);
	                
	                OrderChangeEvent event = new OrderChangeEvent();
	                event.setOrder(convertoToJaxbOrder(jpaOrder));
	                event.setOrderChangeEventEnum(OrderChangeEventEnum.ORDER_CREATED);
	                
	                StringWriter s = Utils.marshall(new ObjectFactory().createOrderChangeEvent(event));
	                
	                TextMessage response = context.createTextMessage(s.toString());
	            	
	            	Queue orderCreatedQueue = context.createQueue(ORDER_CREATED_QUEUE);
	            	context.createProducer().send(orderCreatedQueue, response);
	            	
	            	LOGGER.info("Sent response message");
		    	
	            } else if (orderCommandRequest.getOrderCommandEnum().equals(OrderCommandRequestEnum.GET_TOP_ORDERS)) {
	            	
	            	Query query = entityManager.createNativeQuery("select sum(quantity) as items, orders from line_item group by orders order by items desc, orders asc");
	            	
	            	List<Object> queryResult = query.setMaxResults(10).getResultList();
	            	
	            	Iterator<Object> iterator = queryResult.iterator();
	            	
	            	List<TopValue> result = new ArrayList<TopValue>();
	            	
	            	while (iterator.hasNext()) {
	            		
	            		Object obj = iterator.next();
	            		
	            		Object[] cast = (Object[]) obj;
	            		
	            		TopValue top = new TopValue();
	            		BigInteger b = (BigInteger) cast[0];
	            		Integer i = (Integer) cast[1];
	            		
	            		top.setId(b.intValue());
	            		top.setValue(i);
	            		result.add(top);
	            	}
	            	
	            	OrderCommandResponse responseMessage = new OrderCommandResponse();
	            	responseMessage.setStatus("200");
	            	responseMessage.getTopvalue().addAll(result);
	            	
	            	StringWriter s = Utils.marshall(new ObjectFactory().createOrderCommandResponse(responseMessage));
	            	
	            	TextMessage response = context.createTextMessage(s.toString());
	            	response.setJMSCorrelationID(requestMessage.getJMSMessageID());
	            	
	            	Destination responseChannel = requestMessage.getJMSReplyTo();
	            	
	            	context.createProducer().send(responseChannel, response);
	            	
	            	LOGGER.info("Sent response");
	            	
	            } else if (orderCommandRequest.getOrderCommandEnum().equals(OrderCommandRequestEnum.GET_TOP_ITEMS)) {
	            	
	            	Query query = entityManager.createNativeQuery("select item, sum(quantity) from line_item group by item order by sum(quantity) desc");
	            	
	            	List<Object> queryResult = query.setMaxResults(10).getResultList();
	            	
	            	Iterator<Object> iterator = queryResult.iterator();
	            	
	            	List<TopValue> result = new ArrayList<TopValue>();
	            	
	            	while (iterator.hasNext()) {
	            		
	            		Object obj = iterator.next();
	            		
	            		Object[] cast = (Object[]) obj;
	            		
	            		TopValue top = new TopValue();
	            		Integer b = (Integer) cast[0];
	            		BigInteger i = (BigInteger) cast[1];
	            		
	            		top.setId(b.intValue());
	            		top.setValue(i.intValue());
	            		result.add(top);
	            	}
	            	
	            	OrderCommandResponse responseMessage = new OrderCommandResponse();
	            	responseMessage.setStatus("200");
	            	responseMessage.getTopvalue().addAll(result);
	            	
	            	StringWriter s = Utils.marshall(new ObjectFactory().createOrderCommandResponse(responseMessage));
	            	
	            	TextMessage response = context.createTextMessage(s.toString());
	            	response.setJMSCorrelationID(requestMessage.getJMSMessageID());
	            	
	            	Destination responseChannel = requestMessage.getJMSReplyTo();
	            	
	            	context.createProducer().send(responseChannel, response);
	            	
	            	LOGGER.info("Sent response");
	            	
	            } else if (orderCommandRequest.getOrderCommandEnum().equals(OrderCommandRequestEnum.RESET)) {
	            	
	            	Query query = entityManager.createNativeQuery("delete from line_item; delete from orders;");
	            	
	            	query.executeUpdate();
	            	
	            	OrderCommandResponse responseMessage = new OrderCommandResponse();
	            	responseMessage.setStatus("200");
	            	
	            	StringWriter s = Utils.marshall(new ObjectFactory().createOrderCommandResponse(responseMessage));
	            	
	            	TextMessage response = context.createTextMessage(s.toString());
	            	response.setJMSCorrelationID(requestMessage.getJMSMessageID());
	            	
	            	Destination responseChannel = requestMessage.getJMSReplyTo();
	            	
	            	context.createProducer().send(responseChannel, response);
	            	
	            	LOGGER.info("Sent response");
	            	
	            } else {
	            	
	            	LOGGER.warn("Unknown operation");
	            	
	            	context.createProducer().send(invalidMessageQueue, message);
	            	
	            }
            
            }
            
        } catch (Exception e) {
        	
        	LOGGER.error("An exception occurred during message processing", e);
        	
        }
		
	}
	
	private org.gmagnotta.model.Order convertoToJpaOrder(org.gmagnotta.jaxb.Order order) {
		
		long sum = 0;
		
		org.gmagnotta.model.Order jpaOrder = new org.gmagnotta.model.Order();
		
		jpaOrder.setCreationDate(order.getCreationDate().toGregorianCalendar().getTime());
		jpaOrder.setExternalOrderId(order.getExternalOrderId());
		
		List<LineItem> lineItems = order.getLineItem();
		
		for (LineItem l : lineItems) {
            
    		Item i = l.getItem();
            
        	Query q = entityManager.createNamedQuery("getItemById", org.gmagnotta.model.Item.class);
        	q.setParameter("id", i.getId());
        	
        	org.gmagnotta.model.Item jpaItem = (org.gmagnotta.model.Item) q.getSingleResult();

        	org.gmagnotta.model.LineItem jpaLineItem = new org.gmagnotta.model.LineItem();
        	jpaLineItem.setItem(jpaItem);
        	jpaLineItem.setOrder(jpaOrder);
        	jpaLineItem.setQuantity(l.getQuantity());
        	jpaLineItem.setPrice(jpaItem.getPrice());
        	
        	jpaOrder.addLineItem(jpaLineItem);
            
        	sum += jpaItem.getPrice().multiply(BigDecimal.valueOf(jpaLineItem.getQuantity())).longValue();
        }
    	
		jpaOrder.setAmount(BigDecimal.valueOf(sum));
    	return jpaOrder;
		
	}
	
	private static org.gmagnotta.jaxb.PersistedOrder convertoToJaxbOrder(org.gmagnotta.model.Order order) throws Exception {
		
		org.gmagnotta.jaxb.PersistedOrder persistedOrder = new PersistedOrder();
		
		persistedOrder.setId(order.getId());
		persistedOrder.setAmount(order.getAmount());
		
		GregorianCalendar c = new GregorianCalendar();
        c.setTime(order.getCreationDate());
        XMLGregorianCalendar date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
		persistedOrder.setCreationDate(date2);
		
		persistedOrder.setExternalOrderId(order.getExternalOrderId());
		
		List<org.gmagnotta.model.LineItem> lineItems = order.getLineItems();
		
		for (org.gmagnotta.model.LineItem l : lineItems) {
			
			PersistedLineItem persistedLineItem = new PersistedLineItem();
			
			persistedLineItem.setId(l.getId());
			persistedLineItem.setPrice(l.getPrice());
			persistedLineItem.setQuantity(l.getQuantity());
			
			org.gmagnotta.model.Item item = l.getItem();
			
			PersistedItem persistedItem = new PersistedItem();
			persistedItem.setId(item.getId());
			persistedItem.setDescription(item.getDescription());
			persistedItem.setPrice(item.getPrice());
			
			persistedLineItem.setItem(persistedItem);
			
			persistedOrder.getLineItem().add(persistedLineItem);
			
		}
		
		return persistedOrder;
		
	}
	
}
