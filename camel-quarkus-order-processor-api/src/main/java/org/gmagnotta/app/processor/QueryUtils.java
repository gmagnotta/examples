package org.gmagnotta.app.processor;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import org.apache.camel.Exchange;
import org.eclipse.microprofile.opentracing.Traced;
import org.gmagnotta.jaxb.Aggregateditemtype;
import org.gmagnotta.jaxb.Aggregatedordertype;
import org.gmagnotta.jaxb.Aggregationtype;
import org.gmagnotta.jaxb.OrderCommandResponse;
import org.gmagnotta.jaxb.TopItemsRequest;
import org.gmagnotta.jaxb.TopOrdersRequest;
import org.gmagnotta.jaxb.TopValue;
import org.jboss.logging.Logger;


@ApplicationScoped
@Named("queryutils")
@Traced
/**
 * This class contains some methods to query the database
 */
public class QueryUtils {
	
	private static final Logger LOGGER = Logger.getLogger(QueryUtils.class);
	
	public void prepareGetTopOrders(Exchange exchange) throws Exception {

		TopOrdersRequest request = new TopOrdersRequest();
        
        // replace the body of the exchange
        exchange.getIn().setBody(request);

	}

	public void decodeGetTopOrders(Exchange exchange) throws Exception {

		String responseContent = exchange.getIn().getBody(String.class);
        	
		OrderCommandResponse responseMessage = Utils.unmarshall(OrderCommandResponse.class, responseContent);
		List<TopValue> result = responseMessage.getTopvalue();
		
		Aggregationtype aggregation = new Aggregationtype();
		
		Iterator<TopValue> iterator = result.iterator();
		while (iterator.hasNext()) {
			
			TopValue temp = iterator.next();
			
			Aggregatedordertype order = new Aggregatedordertype();
			
			int items = temp.getId();
			
			int orderId = temp.getValue();
			
			order.setItems(BigInteger.valueOf(items));
			order.setOrderid(String.valueOf(orderId));
			
			aggregation.getAggregatedorder().add(order);
			
		}
		
		exchange.getOut().setBody(aggregation);
	}

	
	public void prepareGetTopItems(Exchange exchange) throws Exception {

        TopItemsRequest request = new TopItemsRequest();
        
        // replace the body of the exchange
        exchange.getIn().setBody(request);

	}

	public void decodeGetTopItems(Exchange exchange) throws Exception {
    	
    	
        String responseContent = exchange.getIn().getBody(String.class);
        	
		OrderCommandResponse responseMessage = Utils.unmarshall(OrderCommandResponse.class, responseContent);
		
		List<TopValue> result = responseMessage.getTopvalue();
		
		Aggregationtype aggregation = new Aggregationtype();
		
		Iterator<TopValue> iterator = result.iterator();
		while (iterator.hasNext()) {
			
			TopValue temp = iterator.next();
			
			Aggregateditemtype order = new Aggregateditemtype();
			
			int items = temp.getId();
			
			int qty = temp.getValue();
			
			order.setItem(String.valueOf(items));
			order.setQuantity(BigInteger.valueOf(qty));
			
			aggregation.getAggregateditem().add(order);
			
		}
		
		exchange.getOut().setBody(aggregation);
		
    }
	
	public void prepareReset(Exchange exchange) throws Exception {

        /*OrderCommandRequest request = new OrderCommandRequest();
        request.setOrderCommandEnum(OrderCommandRequestEnum.RESET);
        
        // replace the body of the exchange
        exchange.getIn().setBody(request);*/

	}

	public void decodeReset(Exchange exchange) throws Exception {
    	
        String responseContent = exchange.getIn().getBody(String.class);

		OrderCommandResponse responseMessage = Utils.unmarshall(OrderCommandResponse.class, responseContent);
        	
		if (!"200".equalsIgnoreCase(responseMessage.getStatus())) {
			throw new Exception("Error");
		}

	}
	
	public void prepareRebuild(Exchange exchange) throws Exception {

        /*OrderCommandRequest request = new OrderCommandRequest();
        request.setOrderCommandEnum(OrderCommandRequestEnum.REBUILD);
        
        // replace the body of the exchange
        exchange.getIn().setBody(request);*/

	}

	public void decodeRebuild(Exchange exchange) throws Exception {
    	
        String responseContent = exchange.getIn().getBody(String.class);

		OrderCommandResponse responseMessage = Utils.unmarshall(OrderCommandResponse.class, responseContent);
        	
		if (!"200".equalsIgnoreCase(responseMessage.getStatus())) {
			throw new Exception("Error");
		}

	}
	
}
