package org.gmagnotta.app.processor;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import org.apache.camel.Exchange;
import org.eclipse.microprofile.opentracing.Traced;
import org.gmagnotta.jaxb.TopItemsRequest;
import org.gmagnotta.jaxb.TopOrdersRequest;
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

	public void prepareGetTopItems(Exchange exchange) throws Exception {

        TopItemsRequest request = new TopItemsRequest();
        
        // replace the body of the exchange
        exchange.getIn().setBody(request);

	}

}
