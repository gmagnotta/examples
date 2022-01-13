package org.gmagnotta.observer;

import java.math.BigDecimal;
import java.util.List;

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

import org.eclipse.microprofile.opentracing.Traced;
import org.gmagnotta.jaxb.Item;
import org.gmagnotta.jaxb.LineItem;
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
	@Traced
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

					org.gmagnotta.model.Order jpaOrder = convertoToJpaOrder(jaxbOrder);

					logger.info("Received Order: " + jpaOrder.toString());

				}  else {

					logger.warn("Unknown operation");

					context.createProducer().send(invalidMessageQueue, message);

				}

			}

		} catch (Exception e) {

			logger.error("An exception occurred during message processing", e);

		}

	}

	private org.gmagnotta.model.Order convertoToJpaOrder(org.gmagnotta.jaxb.Order order) {

		long sum = 0;

		org.gmagnotta.model.Order jpaOrder = new org.gmagnotta.model.Order();

		jpaOrder.setCreationDate(order.getCreationDate().toGregorianCalendar().getTime());
		jpaOrder.setExternalOrderId(order.getExternalOrderId());

		List<LineItem> lineItems = order.getLineItem();

		for (LineItem l : lineItems) {

			org.gmagnotta.model.Item i = new org.gmagnotta.model.Item();
			i.setId(l.getItem().getId());

			org.gmagnotta.model.LineItem jpaLineItem = new org.gmagnotta.model.LineItem();
			jpaLineItem.setItem(i);
			jpaLineItem.setOrder(jpaOrder);
			jpaLineItem.setQuantity(l.getQuantity());
			jpaLineItem.setPrice(null);

			jpaOrder.addLineItem(jpaLineItem);

		}

		jpaOrder.setAmount(BigDecimal.valueOf(sum));
		return jpaOrder;

	}

}
