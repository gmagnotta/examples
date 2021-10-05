package org.gmagnotta.app.processor;

import java.io.StringWriter;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.TextMessage;

import org.apache.camel.Exchange;
import org.gmagnotta.jaxb.Aggregateditemtype;
import org.gmagnotta.jaxb.Aggregatedordertype;
import org.gmagnotta.jaxb.Aggregationtype;
import org.gmagnotta.jaxb.ObjectFactory;
import org.gmagnotta.jaxb.OrderCommandRequest;
import org.gmagnotta.jaxb.OrderCommandRequestEnum;
import org.gmagnotta.jaxb.OrderCommandResponse;
import org.gmagnotta.jaxb.TopValue;
import org.jboss.logging.Logger;


@ApplicationScoped
@Named("queryutils")
/**
 * This class contains some methods to query the database
 */
public class QueryUtils {
	
	private static final Logger LOGGER = Logger.getLogger(QueryUtils.class);
	
	private static final String COMMAND_QUEUE = "orderCommand";

	@Inject
    ConnectionFactory connectionFactory;
	
	JMSContext context;
    
    JMSConsumer consumer;
    
    @PostConstruct
	private void init() {
    	
    	context = connectionFactory.createContext();
    
    }
	
	/**
	 * Returns top 10 Orders with maximum items
	 */
    public void getTopOrders(Exchange exchange) throws Exception {
    	
    	Queue responseQueue = context.createTemporaryQueue();
    	Queue requestQueue = context.createQueue(COMMAND_QUEUE);
    	
    	OrderCommandRequest requestMessage = new OrderCommandRequest();
    	requestMessage.setOrderCommandEnum(OrderCommandRequestEnum.GET_TOP_ORDERS);
    	
    	StringWriter s = Utils.marshall(new ObjectFactory().createOrderCommandRequest(requestMessage));
    	
    	TextMessage textMessage = context.createTextMessage(s.toString());
    	textMessage.setJMSReplyTo(responseQueue);
    	textMessage.setJMSExpiration(10000L);
    	
    	LOGGER.info("Sending message " + textMessage);
    	
    	context.createProducer().send(requestQueue, textMessage);
    	
    	JMSConsumer consumer = context.createConsumer(responseQueue);
    	Message message = consumer.receive(10000L);
        if (message == null) {
        	LOGGER.warn("Received null reply");
        	throw new TimeoutException();
        }
        
        if (message.getJMSCorrelationID().equals(textMessage.getJMSMessageID())) {
        	
        	String responseContent = message.getBody(String.class);
        	
        	OrderCommandResponse responseMessage = Utils.unmarshall(OrderCommandResponse.class, responseContent);
        	List<TopValue> result = responseMessage.getTopvalue();
        	
        	Aggregationtype aggregation = new Aggregationtype();
        	
    		Iterator<TopValue> iterator = result.iterator();
        	while (iterator.hasNext()) {
        		
        		TopValue temp = iterator.next();
        		
        		Aggregatedordertype order = new Aggregatedordertype();
        		
        		int items = temp.getId();
        		
        		int orderId = temp.getValue();
        		
        		order.setItems(BigInteger.valueOf(items));
        		order.setOrderid(String.valueOf(orderId));
        		
        		aggregation.getAggregatedorder().add(order);
        		
        	}
        	
        	exchange.getOut().setBody(aggregation);
        	
        	LOGGER.info("Received response message ");
        	
        } else {
        	
        	LOGGER.error("Unknown response message " + message);
        	
        	throw new Exception("Unknown response message");
        	
        }
    	
    }
	
	/**
	 * Return most requested Items
	 */
    public void getTopItems(Exchange exchange) throws Exception {
    	
    	Queue responseQueue = context.createTemporaryQueue();
    	Queue requestQueue = context.createQueue(COMMAND_QUEUE);
    	
    	OrderCommandRequest requestMessage = new OrderCommandRequest();
    	requestMessage.setOrderCommandEnum(OrderCommandRequestEnum.GET_TOP_ITEMS);
    	
    	StringWriter s = Utils.marshall(new ObjectFactory().createOrderCommandRequest(requestMessage));
    	
    	TextMessage textMessage = context.createTextMessage(s.toString());
    	textMessage.setJMSReplyTo(responseQueue);
    	textMessage.setJMSExpiration(10000L);
    	
    	LOGGER.info("Sending message " + textMessage);
    	
    	context.createProducer().send(requestQueue, textMessage);
    	
    	JMSConsumer consumer = context.createConsumer(responseQueue);
    	Message message = consumer.receive(10000L);
        if (message == null) {
        	LOGGER.warn("Received null reply");
        	throw new TimeoutException();
        }
        
        if (message.getJMSCorrelationID().equals(textMessage.getJMSMessageID())) {
        	
        	String responseContent = message.getBody(String.class);
        	
        	OrderCommandResponse responseMessage = Utils.unmarshall(OrderCommandResponse.class, responseContent);
        	
        	List<TopValue> result = responseMessage.getTopvalue();
        	
        	Aggregationtype aggregation = new Aggregationtype();
        	
        	Iterator<TopValue> iterator = result.iterator();
        	while (iterator.hasNext()) {
        		
        		TopValue temp = iterator.next();
        		
        		Aggregateditemtype order = new Aggregateditemtype();
        		
        		int items = temp.getId();
        		
        		int qty = temp.getValue();
        		
        		order.setItem(String.valueOf(items));
        		order.setQuantity(BigInteger.valueOf(qty));
        		
        		aggregation.getAggregateditem().add(order);
        		
        	}
        	
        	exchange.getOut().setBody(aggregation);
        	
        	LOGGER.info("Received response message ");
        	
        } else {
        	
        	LOGGER.error("Unknown response message " + message);
        	
        	throw new Exception("Unknown response message");
        	
        }
    	
    }
	
	public void reset(Exchange exchange) throws Exception {

    	Queue responseQueue = context.createTemporaryQueue();
    	Queue requestQueue = context.createQueue(COMMAND_QUEUE);
    	
    	OrderCommandRequest requestMessage = new OrderCommandRequest();
    	requestMessage.setOrderCommandEnum(OrderCommandRequestEnum.RESET);
    	
    	StringWriter s = Utils.marshall(new ObjectFactory().createOrderCommandRequest(requestMessage));
    	
    	TextMessage textMessage = context.createTextMessage(s.toString());
    	textMessage.setJMSReplyTo(responseQueue);
    	textMessage.setJMSExpiration(10000L);
    	
    	LOGGER.info("Sending message " + textMessage);
    	
    	context.createProducer().send(requestQueue, textMessage);
    	
    	JMSConsumer consumer = context.createConsumer(responseQueue);
    	Message message = consumer.receive(10000L);
        if (message == null) {
        	LOGGER.warn("Received null reply");
        	throw new TimeoutException();
        }
        
        if (message.getJMSCorrelationID().equals(textMessage.getJMSMessageID())) {
        	
        	String responseContent = message.getBody(String.class);
        	
        	OrderCommandResponse responseMessage = Utils.unmarshall(OrderCommandResponse.class, responseContent);
        	
        	if (!"200".equalsIgnoreCase(responseMessage.getStatus())) {
        		throw new Exception("Error");
        	}
        		
        	
        } else {
        	
        	LOGGER.error("Unknown response message " + message);
        	
        	throw new Exception("Unknown response message");
        	
        }
	}
	
	public void rebuild(Exchange exchange) throws Exception {

    	Queue responseQueue = context.createTemporaryQueue();
    	Queue requestQueue = context.createQueue(COMMAND_QUEUE);
    	
    	OrderCommandRequest requestMessage = new OrderCommandRequest();
    	requestMessage.setOrderCommandEnum(OrderCommandRequestEnum.REBUILD);
    	
    	StringWriter s = Utils.marshall(new ObjectFactory().createOrderCommandRequest(requestMessage));
    	
    	TextMessage textMessage = context.createTextMessage(s.toString());
    	textMessage.setJMSReplyTo(responseQueue);
    	textMessage.setJMSExpiration(10000L);
    	
    	LOGGER.info("Sending message " + textMessage);
    	
    	context.createProducer().send(requestQueue, textMessage);
    	
    	JMSConsumer consumer = context.createConsumer(responseQueue);
    	Message message = consumer.receive(10000L);
        if (message == null) {
        	LOGGER.warn("Received null reply");
        	throw new TimeoutException();
        }
        
        if (message.getJMSCorrelationID().equals(textMessage.getJMSMessageID())) {
        	
        	String responseContent = message.getBody(String.class);
        	
        	OrderCommandResponse responseMessage = Utils.unmarshall(OrderCommandResponse.class, responseContent);
        	
        	if (!"200".equalsIgnoreCase(responseMessage.getStatus())) {
        		throw new Exception("Error");
        	}
        		
        	
        } else {
        	
        	LOGGER.error("Unknown response message " + message);
        	
        	throw new Exception("Unknown response message");
        	
        }
	}

}
