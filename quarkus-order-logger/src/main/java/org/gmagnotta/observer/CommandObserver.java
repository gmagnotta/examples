package org.gmagnotta.observer;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.transaction.Transactional;

import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.gmagnotta.jaxb.OrderCommandRequest;
import org.gmagnotta.jaxb.OrderCommandRequestEnum;
import org.gmagnotta.utils.Utils;
import org.jboss.logging.Logger;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
public class CommandObserver implements MessageListener {

	private static final String COMMAND_QUEUE = "orderCommand";

	private static final String INVALID_MESSAGE_QUEUE = "invalidMessage";

	@Inject
	ConnectionFactory connectionFactory;

	@Inject
	Logger logger;

	JMSContext context;

	JMSConsumer consumer;

	Queue invalidMessageQueue;

	@Produce("direct:telegram")
    ProducerTemplate producer;

	@PostConstruct
	void init() {

		context = connectionFactory.createContext();

		Queue commandQueue = context.createQueue(COMMAND_QUEUE);

		consumer = context.createConsumer(commandQueue);

		invalidMessageQueue = context.createQueue(INVALID_MESSAGE_QUEUE);

		

	}

	void onStart(@Observes StartupEvent ev) throws JMSException {

		consumer.setMessageListener(this);

		logger.info("Consumer started");

	}

	void onStop(@Observes ShutdownEvent ev) throws JMSException {

		consumer.close();

		context.close();

		logger.info("Consumer stopped");

	}

	@Override
	@Transactional
	public void onMessage(Message message) {

		try {

			if (message == null || !(message instanceof TextMessage)) {

				logger.warn("Received unexpected message");

				context.createProducer().send(invalidMessageQueue, message);

			} else {

				TextMessage requestMessage = (TextMessage) message;

//            	LOGGER.info("Received content " + requestMessage.getText());

				OrderCommandRequest orderCommandRequest = Utils.unmarshall(OrderCommandRequest.class,
						requestMessage.getText());

				logger.info("Received message id " + message.getJMSMessageID());

				if (orderCommandRequest.getOrderCommandEnum().equals(OrderCommandRequestEnum.ORDER_RECEIVED)) {

					org.gmagnotta.jaxb.Order jaxbOrder = orderCommandRequest.getOrder();

					producer.sendBody("Order received " + jaxbOrder.getExternalOrderId() + ", creationDate " + jaxbOrder.getCreationDate());

				}  else {

					logger.warn("Unknown operation");

					context.createProducer().send(invalidMessageQueue, message);

				}

			}

		} catch (Exception e) {

			logger.error("An exception occurred during message processing", e);

		}

	}

}
