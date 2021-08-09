package org.gmagnotta.observer;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.jms.BytesMessage;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;

import org.eclipse.microprofile.config.inject.ConfigProperty;
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
public class OrderCreatedEventObserver implements MessageListener {
	
	private static final Logger LOGGER = Logger.getLogger(OrderCreatedEventObserver.class);

	private static final String ORDER_CHANGED_QUEUE = "orderChanged";
	
	private static final String INVALID_MESSAGE_QUEUE = "invalidMessage";
	
	@Produces
	RemoteCacheManager remoteCacheManager;

	@ConfigProperty(name = "infinispan.uri")
    String infinispanUri;

	@ConfigProperty(name = "infinispan.username")
    String infinispanUsername;

	@ConfigProperty(name = "infinispan.password")
    String infinispanPassword;
	
    @Inject
    ConnectionFactory connectionFactory;
    
    JMSContext context;
    
    JMSConsumer consumer;

    RemoteCache<String, DenormalizedLineItem> lineItemsCache;
    
    RemoteCache<String, Order> orderCache;
    
    Queue invalidMessageQueue;
    
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
		
		context = connectionFactory.createContext();
    	
     	Queue orderChangedTopic = context.createQueue(ORDER_CHANGED_QUEUE);
     	
     	consumer = context.createConsumer(orderChangedTopic);
     	
     	invalidMessageQueue = context.createQueue(INVALID_MESSAGE_QUEUE);

	}
    
     void onStart(@Observes StartupEvent ev) throws JMSException {

     	consumer.setMessageListener(this);
     	
     	LOGGER.info("Starting consumer");

    }

    void onStop(@Observes ShutdownEvent ev) throws JMSException {
    	
    	consumer.close();
    	
    	context.close();
        
    	LOGGER.info("Stopped consumer");
    	
    }

	@Override
	public void onMessage(Message message) {
		
		try {
			
            if (message == null || !(message instanceof BytesMessage)) {
            	
            	LOGGER.warn("Received unexpected message");
            	
            	context.createProducer().send(invalidMessageQueue, message);
            	
            } else {
            	
            	BytesMessage requestMessage = (BytesMessage) message;
            	
            	byte[] bytes = new byte[(int) requestMessage.getBodyLength()];
            	requestMessage.readBytes(bytes);
            	
            	OrderChangeEvent event = OrderChangeEvent.parseFrom(bytes);
            	
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
	            	
	            	context.createProducer().send(invalidMessageQueue, message);
	            	
	            }
            
            }
            
        } catch (Exception e) {
        	
        	LOGGER.error("An exception occurred during message processing", e);
        	
        }
		
	}
	
}
