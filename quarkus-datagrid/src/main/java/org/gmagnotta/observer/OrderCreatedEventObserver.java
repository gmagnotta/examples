package org.gmagnotta.observer;

import java.math.BigDecimal;
import java.util.Iterator;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.eclipse.microprofile.reactive.messaging.Acknowledgment;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.gmagnotta.model.BiggestOrders;
import org.gmagnotta.model.event.OrderOuterClass.Order;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.jboss.logging.Logger;

import io.quarkus.infinispan.client.Remote;

@ApplicationScoped
public class OrderCreatedEventObserver {

	private static final Logger LOGGER = Logger.getLogger(OrderCreatedEventObserver.class);

	@Inject
	RemoteCacheManager remoteCacheManager;

	@Inject
	@Remote("orders")
	RemoteCache<String, org.gmagnotta.model.Order> orderCache;

	@Inject
	@Remote("toporders")
	RemoteCache<Integer, Integer> topOrderCache;

	@Inject
	@Remote("topitems")
	RemoteCache<Integer, Integer> topItemsCache;

	@Incoming("topitems")
	@Acknowledgment(Acknowledgment.Strategy.PRE_PROCESSING)
	public void onTopItems(ConsumerRecord<Integer, Integer> message) {

		if (message.value() != null) {

			LOGGER.info("Inserting item " + message.key());
			topItemsCache.putAsync(message.key(), message.value());

		} else {
			LOGGER.info("Removing item " + message.key());
			topItemsCache.removeAsync(message.key());

		}
	}

	@Incoming("toporders")
	@Acknowledgment(Acknowledgment.Strategy.PRE_PROCESSING)
	public void onTopOrders(ConsumerRecord<Integer, BiggestOrders> message) {

		// clear cache
		LOGGER.info("Clearing toporders cache");
		topOrderCache.clearAsync();

		BiggestOrders orders = message.value();

		Iterator<Order> iterator = orders.iterator();

		while (iterator.hasNext()) {

			Order order = iterator.next();

			LOGGER.info("Inserting top order " + order.getId());
			topOrderCache.putAsync(order.getId(), fromProtoBuf(order.getAmount()).intValue());

		}

	}

	@Incoming("order-created")
	@Acknowledgment(Acknowledgment.Strategy.PRE_PROCESSING)
	public void onMessage(byte[] message) {

		try {

			if (message == null) {

				LOGGER.warn("Received unexpected message");

			} else {

				Order protoOrder = Order.parseFrom(message);

				org.gmagnotta.model.Order order = Utils.convertoToModel(protoOrder);

				LOGGER.info("Inserting order id " + order.getId());
				orderCache.putAsync(String.valueOf(order.getId()), order);

			}

		} catch (Exception e) {

			LOGGER.error("An exception occurred during message processing", e);

		}

	}

	private static BigDecimal fromProtoBuf(org.gmagnotta.model.event.OrderOuterClass.BigDecimal proto) {

		java.math.MathContext mc2 = new java.math.MathContext(proto.getPrecision());
		java.math.BigDecimal value = new java.math.BigDecimal(
				new java.math.BigInteger(proto.getValue().toByteArray()),
				proto.getScale(),
				mc2);

		return value;
	}

}
