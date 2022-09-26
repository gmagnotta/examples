package org.gmagnotta.observer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.eclipse.microprofile.reactive.messaging.Acknowledgment;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.gmagnotta.model.BiggestOrders;
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
	RemoteCache<String, BiggestOrders> topOrderCache;

	@Inject
	@Remote("topitems")
	RemoteCache<Integer, Integer> topItemsCache;

	@Incoming("topitems")
	@Acknowledgment(Acknowledgment.Strategy.POST_PROCESSING)
	public void onTopItems(ConsumerRecord<Integer, Integer> message) {

		if (message.value() != null) {

			LOGGER.info("Inserting item " + message.key());
			topItemsCache.put(message.key(), message.value());

		} else {
			LOGGER.info("Removing item " + message.key());
			topItemsCache.remove(message.key());

		}
	}

	@Incoming("toporders")
	@Acknowledgment(Acknowledgment.Strategy.POST_PROCESSING)
	public void onTopOrders(ConsumerRecord<Integer, BiggestOrders> message) {

		BiggestOrders biggestOrders = message.value();

		LOGGER.info("Inserting top orders");
		topOrderCache.put("TOP_ORDERS", biggestOrders);

	}

	@Incoming("order-created")
	@Acknowledgment(Acknowledgment.Strategy.POST_PROCESSING)
	public void onMessage(byte[] message) {

		try {

			if (message == null) {

				LOGGER.warn("Received unexpected message");

			} else {

				org.gmagnotta.model.event.OrderOuterClass.Order protoOrder = org.gmagnotta.model.event.OrderOuterClass.Order.parseFrom(message);

				org.gmagnotta.model.Order order = Utils.convertoToModel(protoOrder);

				LOGGER.info("Inserting order id " + order.getId());
				orderCache.put(String.valueOf(order.getId()), order);

			}

		} catch (Exception e) {

			LOGGER.error("An exception occurred during message processing", e);

		}

	}

}
