package org.gmagnotta.app.processor;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.gmagnotta.jaxb.Order;
import org.gmagnotta.jaxb.OrderCommandRequest;
import org.gmagnotta.jaxb.OrderCommandRequestEnum;
import org.jboss.logging.Logger;

@ApplicationScoped
@Named("orderprocessor")
/**
 * This class is a Camel Processor used to wrap the Order inside an OrderCommandRequest to be sent to other microservice
 */
public class OrderProcessor implements Processor {
	
	private static final Logger LOGGER = Logger.getLogger(OrderProcessor.class);

    public void process(Exchange exchange) throws Exception {

        // extract Order
        Order order = exchange.getIn().getBody(Order.class);

        // wrap it in OrderCommandRequest
        OrderCommandRequest request = new OrderCommandRequest();
        request.setOrder(order);
        request.setOrderCommandEnum(OrderCommandRequestEnum.ORDER_RECEIVED);
        
        // replace the body of the exchange
        exchange.getIn().setBody(request);
        
    }
    
}
