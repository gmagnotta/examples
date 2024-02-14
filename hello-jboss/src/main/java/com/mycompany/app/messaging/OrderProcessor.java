package com.mycompany.app.messaging;

import java.math.BigDecimal;
import java.util.List;

import jakarta.annotation.Resource;
import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.inject.Inject;
import jakarta.jms.JMSContext;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.Queue;
import jakarta.jms.TextMessage;
import jakarta.transaction.Transactional;

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
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Queue")
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