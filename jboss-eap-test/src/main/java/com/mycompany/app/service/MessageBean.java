package com.mycompany.app.service;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mycompany.model.Order;

// @MessageDriven(
// 		activationConfig = {
// 				@ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "java:/jms/queue/TodoListQueue"),
// 				@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
// 		}
// )
public class MessageBean implements MessageListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(MessageBean.class);
	
	@Inject
	private OrderService orderService;
	
	@Override
	public void onMessage(Message received) {
		
		ObjectMessage message = null;
		
		try  {
			
			if (received instanceof ObjectMessage) {
				
				message = (ObjectMessage) received;
				
				Order order = (Order) message.getObject();
				
				LOGGER.info("MessageBean received order with amount: " + order.getAmount());
				
				orderService.createOrder(order);
				
			} else {
				
				LOGGER.error("Unexpected message type received");
				
			}
			
		} catch (JMSException ex) {
			
			LOGGER.error("Exception", ex);
			
			throw new RuntimeException(ex);
			
		}
	}

}
