package org.gmagnotta.observer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Acknowledgment;
import org.eclipse.microprofile.reactive.messaging.Incoming;
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
    
	@Incoming("order-created")
    @Acknowledgment(Acknowledgment.Strategy.PRE_PROCESSING)
	public void onMessage(byte[] message) {
		
		try {
			
            if (message == null) {
            	
            	LOGGER.warn("Received unexpected message");
            	
            } else {
            	
            	Order protoOrder = Order.parseFrom(message);
            	
	            LOGGER.info("Received order: " + protoOrder.getId());

				org.gmagnotta.model.Order order = Utils.convertoToModel(protoOrder);
	            
				orderCache.putAsync(String.valueOf(order.getId()), order);
				
				LOGGER.info("Order id " + order.getId() + " saved in Data Grid");
            
            }
            
        } catch (Exception e) {
        	
        	LOGGER.error("An exception occurred during message processing", e);
        	
        }
		
	}

}
