package org.gmagnotta.app.route;

import javax.enterprise.context.ApplicationScoped;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.gmagnotta.jaxb.Aggregationtype;
import org.jboss.logging.Logger;

@ApplicationScoped
public class RestRoute extends RouteBuilder {

    private static final Logger LOG = Logger.getLogger(RestRoute.class);

    @Override
    public void configure() throws Exception {
    	
        restConfiguration()
         .component("platform-http")
         .bindingMode(RestBindingMode.auto)
         .contextPath("/api");
    	
        rest("/topOrders")
        .get().outType(Aggregationtype.class)
        .to("bean://cacheprocessor?method=getTopOrders");
        
        rest("/topItems")
        .get().outType(Aggregationtype.class)
        .to("bean://cacheprocessor?method=getTopItems");
        
        rest("/reset")
        .post()
         .route().setHeader(Exchange.HTTP_RESPONSE_CODE, simple("201"))
         .to("bean://cacheprocessor?method=resetCache");

    }

}
