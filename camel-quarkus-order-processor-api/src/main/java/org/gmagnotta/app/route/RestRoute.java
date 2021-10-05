package org.gmagnotta.app.route;

import javax.enterprise.context.ApplicationScoped;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;
import org.gmagnotta.jaxb.Aggregationtype;
import org.jboss.logging.Logger;

@ApplicationScoped
/**
 *  Camel Route to expose Rest services
 */
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
        .route()
         .circuitBreaker()
          .to("bean://queryutils?method=getTopOrders")
         .onFallback()
           .setBody(constant("Dependant service not available"))
           .setHeader(Exchange.CONTENT_TYPE, constant("text/plain"))
           .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
         .end()
        .endRest();
       
       rest("/topItems")
       .get().outType(Aggregationtype.class)
       .route()
       .circuitBreaker()
        .to("bean://queryutils?method=getTopItems")
        .onFallback()
        .setBody(constant("Dependant service not available"))
        .setHeader(Exchange.CONTENT_TYPE, constant("text/plain"))
        .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
        .end()
       .endRest();

       rest("/order")
        .post("/generate")
         .param().name("amount").type(RestParamType.query).defaultValue("1").endParam()
         .route().setHeader(Exchange.HTTP_RESPONSE_CODE, simple("201"))
         .to("bean://generateprocessor")
        .endRest();
       
       rest("/reset")
       .post()
        .route()
        .circuitBreaker()
         .to("bean://queryutils?method=reset")
         .setHeader(Exchange.HTTP_RESPONSE_CODE, simple("201"))
        .onFallback()
        .setBody(constant("Dependant service not available"))
        .setHeader(Exchange.CONTENT_TYPE, constant("text/plain"))
        .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
        .end()
      .endRest();
       
       rest("/rebuild")
       .post()
       .route()
       .circuitBreaker()
        .to("bean://queryutils?method=rebuild")
        .setHeader(Exchange.HTTP_RESPONSE_CODE, simple("200"))
       .onFallback()
        .setBody(constant("Dependant service not available"))
        .setHeader(Exchange.CONTENT_TYPE, constant("text/plain"))
        .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
      .end()
      .endRest();
       
    }

}
