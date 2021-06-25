package org.gmagnotta.app.processor;

import java.math.BigInteger;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.apache.camel.Exchange;
import org.gmagnotta.jaxb.Aggregatedordertype;
import org.gmagnotta.jaxb.Aggregateditemtype;
import org.gmagnotta.jaxb.Aggregationtype;
import org.jboss.logging.Logger;


@ApplicationScoped
@Named("queryutils")
/**
 * This class contains some methods to query the database
 */
public class QueryUtils {
	
	private static final Logger LOG = Logger.getLogger(QueryUtils.class);

	@Inject
    EntityManager entityManager;
    
	@Transactional(value = TxType.REQUIRES_NEW)
	/**
	 * Returns top 10 Orders with maximum items
	 */
    public void getTopOrders(Exchange exchange) throws Exception {
    	
    	Query query = entityManager.createNativeQuery("select sum(quantity) as items, orders from line_item group by orders order by items desc, orders asc");
    	
    	List queryResult = query.setMaxResults(10).getResultList();
    	
    	Aggregationtype aggregation = new Aggregationtype();
    	
    	for (Object o : queryResult) {
    		
    		Object[] temp = (Object[]) o;
    		
    		Aggregatedordertype order = new Aggregatedordertype();
    		
    		BigInteger items = (BigInteger) temp[0];
    		
    		Integer orderId = (Integer) temp[1];
    		
    		order.setItems(items);
    		order.setOrderid(String.valueOf(orderId));
    		
    		aggregation.getAggregatedorder().add(order);
    		
    	}
    	
    	exchange.getOut().setBody(aggregation);
    	
    }
	
	@Transactional(value = TxType.REQUIRES_NEW)
	/**
	 * Return most requested Items
	 */
    public void getTopItems(Exchange exchange) throws Exception {
    	
    	Query query = entityManager.createNativeQuery("select item, sum(quantity) from line_item group by item order by sum(quantity) desc");
    	
    	List queryResult = query.setMaxResults(10).getResultList();
    	
    	Aggregationtype aggregation = new Aggregationtype();
    	
    	for (Object o : queryResult) {
    		
    		Object[] temp = (Object[]) o;
    		
    		Aggregateditemtype itemtype = new Aggregateditemtype();
    		
    		Integer item = (Integer) temp[0];
    		
    		BigInteger quantity = (BigInteger) temp[1];
    		
    		itemtype.setItem(String.valueOf(item));
    		itemtype.setQuantity(quantity);
    		
    		aggregation.getAggregateditem().add(itemtype);
    		
    	}
    	
    	exchange.getOut().setBody(aggregation);
    	
    }

}
