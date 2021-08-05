package org.gmagnotta.observer;

import java.io.StringWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
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
import javax.transaction.Transactional;

import org.gmagnotta.jaxb.ObjectFactory;
import org.gmagnotta.jaxb.OrderCommandRequest;
import org.gmagnotta.jaxb.OrderCommandRequestEnum;
import org.gmagnotta.jaxb.OrderCommandResponse;
import org.gmagnotta.jaxb.TopValue;
import org.gmagnotta.model.DenormalizedLineItem;
import org.gmagnotta.model.Order;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.Search;
import org.infinispan.query.dsl.QueryFactory;
import org.infinispan.query.dsl.QueryResult;
import org.jboss.logging.Logger;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;

public class CommandObserver implements MessageListener {
	
	private static final Logger LOGGER = Logger.getLogger(OrderCreatedEventObserver.class);
	
	private static final String COMMAND_QUEUE = "cacheCommand";
	
	private static final String INVALID_MESSAGE_QUEUE = "invalidMessage";

	@Inject
	RemoteCacheManager remoteCacheManager;

    @Inject
    ConnectionFactory connectionFactory;
    
    JMSContext context;
    
    JMSConsumer consumer;
    
    
    RemoteCache<String, DenormalizedLineItem> lineItemsCache;
    
    RemoteCache<String, Order> orderCache;
    
    Queue invalidMessageQueue;
    
	@PostConstruct
	private void init() {
		
		orderCache = remoteCacheManager.getCache("orders");
		
		lineItemsCache = remoteCacheManager.getCache("lineitems");
		
		context = connectionFactory.createContext();
    	
     	Queue cacheCommandQueue = context.createQueue(COMMAND_QUEUE);
     	
     	consumer = context.createConsumer(cacheCommandQueue);
     	
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
	public void onMessage(Message message) {
		
		try {
			
            if (message == null || !(message instanceof TextMessage)) {
            	
            	LOGGER.warn("Received unexpected message");
            	
            	context.createProducer().send(invalidMessageQueue, message);
            	
            } else {
            	
            	TextMessage requestMessage = (TextMessage) message;
            	
            	OrderCommandRequest orderCommandRequest = Utils.unmarshall(OrderCommandRequest.class, requestMessage.getText());
            	
	            LOGGER.info("Received message id " + message.getJMSMessageID());
	            
	            if (orderCommandRequest.getOrderCommandEnum().equals(OrderCommandRequestEnum.GET_TOP_ITEMS)) {
	            	
	            	QueryFactory qf = Search.getQueryFactory(lineItemsCache);
	            	
	            	org.infinispan.query.dsl.Query<Object[][]> q = qf.create("SELECT itemId, sum(quantity) FROM library.DenormalizedLineItem GROUP BY itemId ORDER by sum(quantity) desc");
	            	
	            	QueryResult<Object[][]> result = q.maxResults(10).execute();
	            	
	        		List<Object[][]> list = result.list();
	        		
	        		Iterator<Object[][]> iterator = list.iterator();
	        		
	        		List<TopValue> topValues = new ArrayList<TopValue>();
	            	
	            	while (iterator.hasNext()) {
	            		
	            		Object obj = iterator.next();
	            		
	            		Object[] cast = (Object[]) obj;
	            		
	            		TopValue top = new TopValue();
	            		Integer b = (Integer) cast[0];
	            		Long i = (Long) cast[1];
	            		
	            		top.setId(b.intValue());
	            		top.setValue(i.intValue());
	            		topValues.add(top);
	            	}
	        		
	            	OrderCommandResponse responseMessage = new OrderCommandResponse();
	            	responseMessage.setStatus("200");
	            	responseMessage.getTopvalue().addAll(topValues);
	            	
	            	StringWriter s = Utils.marshall(new ObjectFactory().createOrderCommandResponse(responseMessage));
	            	
	            	TextMessage response = context.createTextMessage(s.toString());
	            	response.setJMSCorrelationID(requestMessage.getJMSMessageID());
	            	
	            	Destination responseChannel = requestMessage.getJMSReplyTo();
	            	
	            	context.createProducer().send(responseChannel, response);
	            	
	            	LOGGER.info("Sent response");
		    	
	            } else if (orderCommandRequest.getOrderCommandEnum().equals(OrderCommandRequestEnum.GET_TOP_ORDERS)) {
	            	
	            	QueryFactory qf = Search.getQueryFactory(lineItemsCache);
	            	
	            	org.infinispan.query.dsl.Query<Object[][]> q = qf.create("SELECT sum(quantity), orderid FROM library.DenormalizedLineItem GROUP BY orderid ORDER by sum(quantity) desc, orderid asc");
	            	
	            	QueryResult<Object[][]> result = q.maxResults(10).execute();
	            	
	        		List<Object[][]> list = result.list();
	        		
	        		Iterator<Object[][]> iterator = list.iterator();
	            	
	            	List<TopValue> topValues = new ArrayList<TopValue>();
	            	
	            	while (iterator.hasNext()) {
	            		
	            		Object obj = iterator.next();
	            		
	            		Object[] cast = (Object[]) obj;
	            		
	            		TopValue top = new TopValue();
	            		Long b = (Long) cast[0];
	            		Integer i = (Integer) cast[1];
	            		
	            		top.setId(b.intValue());
	            		top.setValue(i.intValue());
	            		topValues.add(top);
	            	}
	            	
	            	OrderCommandResponse responseMessage = new OrderCommandResponse();
	            	responseMessage.setStatus("200");
	            	responseMessage.getTopvalue().addAll(topValues);
	            	
	            	StringWriter s = Utils.marshall(new ObjectFactory().createOrderCommandResponse(responseMessage));
	            	
	            	TextMessage response = context.createTextMessage(s.toString());
	            	response.setJMSCorrelationID(requestMessage.getJMSMessageID());
	            	
	            	Destination responseChannel = requestMessage.getJMSReplyTo();
	            	
	            	context.createProducer().send(responseChannel, response);
	            	
	            	LOGGER.info("Sent response");
		    	
	            } else if (orderCommandRequest.getOrderCommandEnum().equals(OrderCommandRequestEnum.RESET)) {
	            	
	            	lineItemsCache.clear();
	            	
	            	orderCache.clear();
	            	
	            	LOGGER.info("Cache cleared");
	            	
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
	
}
