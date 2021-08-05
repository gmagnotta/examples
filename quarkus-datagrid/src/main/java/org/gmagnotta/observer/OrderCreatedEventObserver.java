package org.gmagnotta.observer;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.TextMessage;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.gmagnotta.jaxb.OrderChangeEvent;
import org.gmagnotta.jaxb.OrderChangeEventEnum;
import org.gmagnotta.jaxb.PersistedItem;
import org.gmagnotta.jaxb.PersistedLineItem;
import org.gmagnotta.jaxb.PersistedOrder;
import org.gmagnotta.model.DenormalizedLineItem;
import org.gmagnotta.model.LineItem;
import org.gmagnotta.model.Order;
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

	private static final String ORDER_CREATED_QUEUE = "orderCreated";
	
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
    	
     	Queue orderCreatedTopic = context.createQueue(ORDER_CREATED_QUEUE);
     	
     	consumer = context.createConsumer(orderCreatedTopic);
     	
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
			
            if (message == null || !(message instanceof TextMessage)) {
            	
            	LOGGER.warn("Received unexpected message");
            	
            	context.createProducer().send(invalidMessageQueue, message);
            	
            } else {
            	
            	TextMessage requestMessage = (TextMessage) message;
            	
            	OrderChangeEvent event = Utils.unmarshall(OrderChangeEvent.class, requestMessage.getText());
	            
	            LOGGER.info("Received event: " + requestMessage.getText());
	            
	            if (event.getOrderChangeEventEnum().equals(OrderChangeEventEnum.ORDER_CREATED)) {
	            
	            	PersistedOrder persistedOrder = event.getOrder();
	            	
	            	org.gmagnotta.model.Order order = convertoToJpaOrder(persistedOrder);
	            	
	            	for (LineItem l : order.getLineItems()) {
	            		
	            		l.setOrderid(order.getId());
	            		
	            		// fix relationship
	            		l.setOrder(order);
	            		
	            		DenormalizedLineItem d = DenormalizedLineItem.fromLineItem(l);
	            		
	            		lineItemsCache.putAsync(String.valueOf(l.getId()), d);
	            		
	            	}

	            	orderCache.putAsync(String.valueOf(order.getId()), order);
	            	
	            	LOGGER.info("Order id " + persistedOrder.getId() + " saved in Data Grid");
		    	
	            } else {
	            	
	            	LOGGER.warn("Unknown operation");
	            	
	            	context.createProducer().send(invalidMessageQueue, message);
	            	
	            }
            
            }
            
        } catch (Exception e) {
        	
        	LOGGER.error("An exception occurred during message processing", e);
        	
        }
		
	}
	
	private org.gmagnotta.model.Order convertoToJpaOrder(org.gmagnotta.jaxb.PersistedOrder order) {
		
		org.gmagnotta.model.Order jpaOrder = new org.gmagnotta.model.Order();
		
		jpaOrder.setCreationDate(order.getCreationDate().toGregorianCalendar().getTime());
		jpaOrder.setExternalOrderId(order.getExternalOrderId());
		
		List<PersistedLineItem> lineItems = order.getLineItem();
		
		for (PersistedLineItem l : lineItems) {
            
    		PersistedItem i = l.getItem();
            
        	org.gmagnotta.model.Item jpaItem = new org.gmagnotta.model.Item();
        	jpaItem.setDescription(i.getDescription());
        	jpaItem.setId(i.getId());
        	jpaItem.setPrice(i.getPrice());

        	org.gmagnotta.model.LineItem jpaLineItem = new org.gmagnotta.model.LineItem();
        	jpaLineItem.setItem(jpaItem);
        	jpaLineItem.setOrder(jpaOrder);
        	jpaLineItem.setQuantity(l.getQuantity());
        	jpaLineItem.setPrice(l.getPrice());
        	jpaLineItem.setId(l.getId());
        	
        	jpaOrder.addLineItem(jpaLineItem);
            
        }
    	
		jpaOrder.setAmount(order.getAmount());
		jpaOrder.setId(order.getId());
    	return jpaOrder;
		
	}
	
}
