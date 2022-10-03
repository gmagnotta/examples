package org.gmagnotta.route;

import javax.enterprise.context.ApplicationScoped;

import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Acknowledgment;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.gmagnotta.model.Item;
import org.gmagnotta.model.event.OrderOuterClass.Order;
import org.jboss.logging.Logger;

import com.google.protobuf.InvalidProtocolBufferException;

@ApplicationScoped
public class OrderReceiver {

    private static final Logger LOGGER = Logger.getLogger(OrderReceiver.class);
    
    @Produce("direct:telegram")
    ProducerTemplate producerTemplate;

	@ConfigProperty(name = "telegram.enabled")
    boolean telegramEnabled;

    @Incoming("order-created")
	@Acknowledgment(Acknowledgment.Strategy.POST_PROCESSING)
	public void onMessage(Order protoOrder) throws InvalidProtocolBufferException {

		if (protoOrder == null) {

			LOGGER.warn("Received unexpected message");

		} else {

			LOGGER.info("Received order id " + protoOrder.getId() + " from Kafka");

			if (telegramEnabled) {
				producerTemplate.sendBody(protoOrder);
			}

		}

	}

	@Incoming("items")
	@Acknowledgment(Acknowledgment.Strategy.POST_PROCESSING)
	public void onItemsChanged(ConsumerRecord<String, Item> item) throws InvalidProtocolBufferException {

		if (item == null) {

			LOGGER.warn("Received unexpected message");

		} else {

			Header header = item.headers().lastHeader("__op");

			if (header != null) {

				String operation = new String(header.value());

				if ("u".equalsIgnoreCase(operation)) {

					LOGGER.info("This item was updated: " + item.value().toString());

				}

			}


		}

	}
}
