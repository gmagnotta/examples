package org.gmagnotta.observer;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Acknowledgment;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.gmagnotta.model.DenormalizedLineItem;
import org.gmagnotta.model.LineItem;
import org.gmagnotta.model.Order;
import org.gmagnotta.model.event.Orderchangeevent.OrderChangeEvent;
import org.gmagnotta.protobuf.OrderInitializerImpl;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.transaction.lookup.JBossStandaloneJTAManagerLookup;
import org.jboss.logging.Logger;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
public class OrderCreatedEventObserver {
	
	private static final Logger LOGGER = Logger.getLogger(OrderCreatedEventObserver.class);

	@Produces
	RemoteCacheManager remoteCacheManager;

	@ConfigProperty(name = "infinispan.uri")
    String infinispanUri;

	@ConfigProperty(name = "infinispan.username")
    String infinispanUsername;

	@ConfigProperty(name = "infinispan.password")
    String infinispanPassword;
	
    RemoteCache<String, DenormalizedLineItem> lineItemsCache;
    
    RemoteCache<String, Order> orderCache;
    
	@PostConstruct
	private void init() {
		
		ConfigurationBuilder clientBuilder = new ConfigurationBuilder();
		
		clientBuilder.transaction().transactionManagerLookup(new JBossStandaloneJTAManagerLookup());
		clientBuilder.uri(infinispanUri);
		clientBuilder.security().authentication().username(infinispanUsername)
				.password(infinispanPassword).addContextInitializer(new OrderInitializerImpl());

		remoteCacheManager = new RemoteCacheManager(clientBuilder.build());
		
		orderCache = remoteCacheManager.getCache("orders");
		
		lineItemsCache = remoteCacheManager.getCache("lineitems");
		
	}
    
	@Incoming("order-created")
    @Acknowledgment(Acknowledgment.Strategy.PRE_PROCESSING)
	public void onMessage(byte[] message) {
		
		try {
			
            if (message == null) {
            	
            	LOGGER.warn("Received unexpected message");
            	
            } else {
            	
            	OrderChangeEvent event = OrderChangeEvent.parseFrom(message);
            	
	            LOGGER.info("Received event: " + event.getType());
	            
	            if (event.getType().equals(OrderChangeEvent.EventType.ORDER_CREATED)) {
	            
	            	org.gmagnotta.model.event.Orderchangeevent.Order orderCreated = event.getOrder();
	            	
	            	org.gmagnotta.model.Order order = Utils.convertoToModel(orderCreated);
	            	
	            	for (LineItem l : order.getLineItems()) {
	            		
	            		DenormalizedLineItem d = DenormalizedLineItem.fromLineItem(l);
	            		
	            		lineItemsCache.putAsync(String.valueOf(l.getId()), d);
	            		
	            	}

	            	orderCache.putAsync(String.valueOf(order.getId()), order);
	            	
	            	LOGGER.info("Order id " + order.getId() + " saved in Data Grid");
		    	
	            } else {
	            	
	            	LOGGER.warn("Unknown operation");
	            	
	            }
            
            }
            
        } catch (Exception e) {
        	
        	LOGGER.error("An exception occurred during message processing", e);
        	
        }
		
	}
	
}
