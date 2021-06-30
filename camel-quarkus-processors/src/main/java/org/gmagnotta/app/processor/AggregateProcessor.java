package org.gmagnotta.app.processor;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.gmagnotta.model.DenormalizedLineItem;
import org.gmagnotta.model.LineItem;
import org.gmagnotta.model.Order;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.jboss.logging.Logger;



@ApplicationScoped
@Named("aggregateprocessor")
public class AggregateProcessor implements Processor {
	
	private static final Logger LOG = Logger.getLogger(AggregateProcessor.class);

	@Inject
    EntityManager entityManager;
	
	@Inject
	RemoteCacheManager remoteCacheManager;
	
    RemoteCache<String, DenormalizedLineItem> lineItemsCache;
    
    RemoteCache<String, Order> orderCache;
    
	@PostConstruct
	private void init() {
		
		orderCache = remoteCacheManager.getCache("orders");
		
		lineItemsCache = remoteCacheManager.getCache("lineitems");
		
	}
	
    public void process(Exchange exchange) throws Exception {
    	
		String orderId = exchange.getIn().getBody(String.class);
		
		Order order = getOrderById(Integer.valueOf(orderId));
    	
    	for (LineItem l : order.getLineItems()) {
    		
    		l.setOrderid(order.getId());
    		
    		DenormalizedLineItem d = DenormalizedLineItem.fromLineItem(l);
    		
    		lineItemsCache.putAsync(String.valueOf(l.getId()), d);
    		
    	}

    	orderCache.putAsync(orderId, order);
    	
    	LOG.info("Order id " + orderId + " saved in Data Grid");
    	
    }
	
    
	private Order getOrderById(int id) {
		
		
		LOG.info("Reading order id " + id);

        Query query = entityManager.createNamedQuery("getOrderById", Order.class);
        query.setParameter("id", id);

        return (Order) query.getSingleResult();
    }

}
