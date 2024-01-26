package com.mycompany.app.messaging;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.transaction.Transactional;

import org.gmagnotta.jaxb.Item;
import org.gmagnotta.jaxb.LineItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mycompany.app.messaging.utils.UmarshallUtils;
import com.mycompany.app.service.ItemService;
import com.mycompany.app.service.OrderService;
import com.mycompany.model.Order;

@MessageDriven(name = "OrderProcessor", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "java:global/remoteContext/createOrderCommand"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
})
public class OrderProcessor implements MessageListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(OrderProcessor.class);

	@Inject
	private JMSContext context;

	@Resource(lookup = "java:global/remoteContext/invalidMessage")
	private Queue invalidMessageQueue;

	@Inject
	private OrderService orderService;

	@Inject
	private ItemService itemService;

	@Transactional
	public void onMessage(Message message) {

		try {

			if (message == null || !(message instanceof TextMessage)) {

				LOGGER.warn("Received unexpected message");

				context.createProducer().send(invalidMessageQueue, message);

			} else {

				TextMessage requestMessage = (TextMessage) message;

				// LOGGER.info("Received content " + requestMessage.getText());

				org.gmagnotta.jaxb.CreateOrderRequest createOrder = UmarshallUtils.unmarshall(org.gmagnotta.jaxb.CreateOrderRequest.class,
						requestMessage.getText());

				LOGGER.info("Received message id " + message.getJMSMessageID());

				com.mycompany.model.Order jpaOrder = convertoToJpaOrder(createOrder.getOrder());

				orderService.createOrder(jpaOrder);

			}

		} catch (Exception e) {

			LOGGER.error("An exception occurred during message processing", e);

			context.createProducer().send(invalidMessageQueue, message);

		}

	}

	private Order convertoToJpaOrder(org.gmagnotta.jaxb.Order jaxbOrder) {

		long sum = 0;

		Order jpaOrder = new Order();

		jpaOrder.setCreationDate(jaxbOrder.getCreationDate().toGregorianCalendar().getTime());
		// jpaOrder.setExternalOrderId(order.getExternalOrderId());

		List<LineItem> lineItems = jaxbOrder.getLineItem();

		for (LineItem l : lineItems) {

			Item i = l.getItem();

			com.mycompany.model.Item jpaItem = itemService.getItemById(i.getId());

			com.mycompany.model.LineItem jpaLineItem = new com.mycompany.model.LineItem();
			jpaLineItem.setItem(jpaItem);
			jpaLineItem.setOrder(jpaOrder);
			jpaLineItem.setQuantity(l.getQuantity());
			jpaLineItem.setPrice(jpaItem.getPrice());

			jpaOrder.addLineItem(jpaLineItem);

			sum += jpaItem.getPrice().multiply(BigDecimal.valueOf(jpaLineItem.getQuantity())).longValue();
		}

		jpaOrder.setAmount(BigDecimal.valueOf(sum));
		jpaOrder.setUser("JMS");
		return jpaOrder;

	}

}