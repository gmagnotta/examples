package com.mycompany.app.messaging;

import jakarta.annotation.Resource;
import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.inject.Inject;
import jakarta.jms.Destination;
import jakarta.jms.JMSContext;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.Queue;
import jakarta.jms.TextMessage;
import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@MessageDriven(name = "PingProcessor", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "java:global/remoteContext/pingCommand"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Queue")
})
public class PingProcessor implements MessageListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(PingProcessor.class);

	@Inject
	private JMSContext context;

	@Resource(lookup = "java:global/remoteContext/invalidMessage")
	private Queue invalidMessageQueue;

	@Transactional
	public void onMessage(Message message) {

		try {

			if (message == null || !(message instanceof TextMessage)) {

				LOGGER.warn("Received unexpected message");

				context.createProducer().send(invalidMessageQueue, message);

			} else {

				TextMessage requestMessage = (TextMessage) message;

				String content = requestMessage.getText();

				LOGGER.info("Received content " + content);

				LOGGER.info("Received message id " + message.getJMSMessageID());

				if ("PING".equals(content)) {

					TextMessage textResponse = context.createTextMessage("PONG " + System.currentTimeMillis());

					textResponse.setJMSCorrelationID(requestMessage.getJMSCorrelationID());
	
					Destination responseChannel = requestMessage.getJMSReplyTo();
	
					context.createProducer().send(responseChannel, textResponse);
	
					LOGGER.info("Sent response");

				} else {

					LOGGER.warn("Received unexpected message");

					context.createProducer().send(invalidMessageQueue, message);
					
				}

			}

		} catch (Exception e) {

			LOGGER.error("An exception occurred during message processing", e);

			context.createProducer().send(invalidMessageQueue, message);

		}

	}

}