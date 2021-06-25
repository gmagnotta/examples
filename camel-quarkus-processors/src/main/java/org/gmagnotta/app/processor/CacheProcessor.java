package org.gmagnotta.app.processor;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.gmagnotta.jaxb.Aggregatedordertype;
import org.gmagnotta.jaxb.Aggregationtype;
import org.gmagnotta.jaxb.Aggregateditemtype;
import org.gmagnotta.model.DenormalizedLineItem;
import org.gmagnotta.model.LineItem;
import org.gmagnotta.model.Order;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.Search;
import org.infinispan.query.dsl.QueryFactory;
import org.infinispan.query.dsl.QueryResult;
import org.jboss.logging.Logger;



@ApplicationScoped
@Named("cacheprocessor")
public class CacheProcessor  {
	
	private static final Logger LOG = Logger.getLogger(CacheProcessor.class);

//    @Inject @Remote("lineitems")
//    RemoteCache<String, Order> lineItemsCache;
//    
//    @Inject @Remote("aggregate")
//    RemoteCache<Integer, Integer> aggregateCache;
//    
//    @Inject @Remote("orders")
//    RemoteCache<String, Order> orderCache;
	
	@Inject
	RemoteCacheManager remoteCacheManager;
	
	RemoteCache<String, LineItem> lineItemsCache;
    
    RemoteCache<String, Order> orderCache;
    
	@PostConstruct
	private void init() {
		
		orderCache = remoteCacheManager.getCache("orders");
		
		lineItemsCache = remoteCacheManager.getCache("lineitems");
		
	}
    
    public void getTopOrders(Exchange exchange) throws Exception {
    	
    	QueryFactory qf = Search.getQueryFactory(lineItemsCache);
    	
    	org.infinispan.query.dsl.Query<Object[][]> q = qf.create("SELECT sum(quantity), orderid FROM library.DenormalizedLineItem GROUP BY orderid ORDER by sum(quantity) desc, orderid asc");
    	
    	QueryResult<Object[][]> result = q.maxResults(10).execute();
    	
		List<Object[][]> list = result.list();

		Aggregationtype aggregation = new Aggregationtype();
    	
		Iterator<Object[][]> iterator = list.iterator();
    	while (iterator.hasNext()) {
    		
    		Object[] temp = (Object[]) iterator.next();
    		
    		Aggregatedordertype order = new Aggregatedordertype();
    		
    		Long itemsLong = (Long) temp[0];
    		
    		BigInteger items = BigInteger.valueOf(itemsLong);
    		
    		Integer orderId = (Integer) temp[1];
    		
    		order.setItems(items);
    		order.setOrderid(String.valueOf(orderId));
    		
    		aggregation.getAggregatedorder().add(order);
    		
    	}
    	
    	exchange.getOut().setBody(aggregation);

//		iterator.close();
    	
//    	LOG.info("Retrieved " + l.size() + " elements");
//    	
//    	for (Object o : l) {
//    		
//    		Object[] temp = (Object[]) o;
//    		
//    		LOG.info("Order " + temp[1] + " contains  " + temp[0] + " items");
//    		
//    	}
        
    }
    
    public void getTopItems(Exchange exchange) throws Exception {
    	
    	QueryFactory qf = Search.getQueryFactory(lineItemsCache);
    	
    	org.infinispan.query.dsl.Query<Object[][]> q = qf.create("SELECT itemId, sum(quantity) FROM library.DenormalizedLineItem GROUP BY itemId ORDER by sum(quantity) desc");
    	
    	QueryResult<Object[][]> result = q.maxResults(10).execute();
    	
		List<Object[][]> list = result.list();

		Aggregationtype aggregation = new Aggregationtype();
    	
		Iterator<Object[][]> iterator = list.iterator();
    	while (iterator.hasNext()) {
    		
    		Object[] temp = (Object[]) iterator.next();
    		
    		Aggregateditemtype order = new Aggregateditemtype();
    		
    		Integer itemsLong = (Integer) temp[0];
    		
    		BigInteger items = BigInteger.valueOf(itemsLong);
    		
    		Long quantity = (Long) temp[1];
    		
    		order.setItem(String.valueOf(items));
    		order.setQuantity(BigInteger.valueOf(quantity.longValue()));
    		
    		aggregation.getAggregateditem().add(order);
    		
    	}
    	
    	exchange.getOut().setBody(aggregation);

    }
    
    @Transactional(value = TxType.REQUIRES_NEW)
    public void resetCache(Exchange exchange) throws Exception {
    	
    	lineItemsCache.clear();
    	
    	orderCache.clear();
    	
    	
    }

}
