package org.gmagnotta.utils;

import java.io.StringWriter;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;

import org.gmagnotta.jaxb.ObjectFactory;
import org.gmagnotta.jaxb.OrderCommandRequest;
import org.gmagnotta.jaxb.OrderCommandRequestEnum;
import org.gmagnotta.jaxb.OrderCommandResponse;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.Test;

import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.jms.JMSConsumer;
import javax.jms.Message;

import io.quarkus.artemis.test.ArtemisTestResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@QuarkusTestResource(ArtemisTestResource.class)
public class TestObserver {

    private static final String COMMAND_QUEUE = "orderCommand";

    @Inject
	ConnectionFactory connectionFactory;

    JMSContext context;

    @Inject
	Logger logger;

    @Test
    public void test() throws Exception {

        context = connectionFactory.createContext();

        Queue responseQueue = context.createTemporaryQueue();
    	Queue requestQueue = context.createQueue(COMMAND_QUEUE);
    	
    	OrderCommandRequest requestMessage = new OrderCommandRequest();
    	requestMessage.setOrderCommandEnum(OrderCommandRequestEnum.GET_TOP_ORDERS);
    	
    	StringWriter s = Utils.marshall(new ObjectFactory().createOrderCommandRequest(requestMessage));
    	
    	TextMessage textMessage = context.createTextMessage(s.toString());
    	textMessage.setJMSReplyTo(responseQueue);
    	textMessage.setJMSExpiration(10000L);
    	
        logger.info("sending message");

    	context.createProducer().send(requestQueue, textMessage);
    	
    	JMSConsumer consumer = context.createConsumer(responseQueue);
    	Message message = consumer.receive(10000L);
        if (message == null) {
        	logger.warn("Received null reply");
        	throw new TimeoutException();
        }
        
        if (message.getJMSCorrelationID().equals(textMessage.getJMSMessageID())) {
        	
            String responseContent = message.getBody(String.class);
        	
            OrderCommandResponse responseMessage = Utils.unmarshall(OrderCommandResponse.class, responseContent);
            
            logger.warn("Received reply " + responseContent);
        	
        }
    }
    
}
