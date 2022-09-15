package com.mycompany.app.messaging;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.transaction.Transactional;

import org.gmagnotta.jaxb.Item;
import org.gmagnotta.jaxb.LineItem;
import org.gmagnotta.jaxb.TopOrdersResponse;
import org.gmagnotta.jaxb.TopValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mycompany.app.messaging.utils.UmarshallUtils;
import com.mycompany.app.service.ItemService;
import com.mycompany.app.service.OrderService;
import com.mycompany.model.Order;

import org.gmagnotta.jaxb.ObjectFactory;

@MessageDriven(name = "TopOrderProcessor", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "java:global/remoteContext/getTopOrdersCommand"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
})
public class TopOrderProcessor implements MessageListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(OrderProcessor.class);

	@Inject
	private JMSContext context;

	@Resource(lookup = "java:global/remoteContext/invalidMessage")
	private Queue invalidMessageQueue;

	@Inject
	private OrderService ordersService;

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

				org.gmagnotta.jaxb.TopOrdersRequest getTopOrders = UmarshallUtils.unmarshall(org.gmagnotta.jaxb.TopOrdersRequest.class,
						requestMessage.getText());

				LOGGER.info("Received message id " + message.getJMSMessageID());

				Map<Integer, Double> topOrders = ordersService.getTopOrders();

				Set<Integer> keys = topOrders.keySet();

				Iterator<Integer> iterator = keys.iterator();

				List<TopValue> result = new ArrayList<TopValue>();
		
				while (iterator.hasNext()) {
					
					Integer k = iterator.next();
					
					Double value = topOrders.get(k);
					
					TopValue top = new TopValue();

					top.setId(k.intValue());
					top.setValue(value.intValue());
					result.add(top);

				}

				TopOrdersResponse response = new TopOrdersResponse();

				response.setSource("EAP");
				response.getTopvalue().addAll(result);

				StringWriter s = UmarshallUtils.marshall(new ObjectFactory().createTopOrders(response));

				TextMessage textResponse = context.createTextMessage(s.toString());
				textResponse.setJMSCorrelationID(requestMessage.getJMSCorrelationID());

				Destination responseChannel = requestMessage.getJMSReplyTo();

				context.createProducer().send(responseChannel, textResponse);

				LOGGER.info("Sent response");

			}

		} catch (Exception e) {

			LOGGER.error("An exception occurred during message processing", e);

			context.createProducer().send(invalidMessageQueue, message);

		}

	}

}