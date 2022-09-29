package org.gmagnotta.route;

import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Acknowledgment;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.gmagnotta.model.event.OrderOuterClass.Order;
import org.jboss.logging.Logger;

import com.google.protobuf.InvalidProtocolBufferException;

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
}
