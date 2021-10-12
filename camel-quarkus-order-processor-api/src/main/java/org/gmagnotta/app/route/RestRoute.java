package org.gmagnotta.app.route;

import javax.enterprise.context.ApplicationScoped;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;
import org.gmagnotta.jaxb.Aggregationtype;
import org.jboss.logging.Logger;

import org.apache.camel.converter.jaxb.JaxbDataFormat;

import org.apache.camel.component.activemq.ActiveMQComponent;

@ApplicationScoped
/**
 *  Camel Route to expose Rest services
 */
public class RestRoute extends RouteBuilder {

    private static final Logger LOG = Logger.getLogger(RestRoute.class);

    @Override
    public void configure() throws Exception {

      JaxbDataFormat jaxbDataFormat = new JaxbDataFormat("org.gmagnotta.jaxb");

      ActiveMQComponent activeMq = new ActiveMQComponent();
      getContext().addComponent("activemq2", activeMq);

    	restConfiguration()
        .component("platform-http")
        .bindingMode(RestBindingMode.auto)
        .contextPath("/api");
   	
        rest("/topOrders")
        .get().outType(Aggregationtype.class)
        .route()
         .circuitBreaker()
          .to("bean://queryutils?method=prepareGetTopOrders")
          .marshal(jaxbDataFormat)
          .to("activemq2:queue:orderCommand?jmsMessageType=Text")
          .to("bean://queryutils?method=decodeGetTopOrders")
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
          .to("bean://queryutils?method=prepareGetTopItems")
          .marshal(jaxbDataFormat)
          .to("activemq2:queue:orderCommand?jmsMessageType=Text")
          .to("bean://queryutils?method=decodeGetTopItems")
         .onFallback()
           .setBody(constant("Dependant service not available"))
           .setHeader(Exchange.CONTENT_TYPE, constant("text/plain"))
           .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
         .end()
        .endRest();

       rest("/order")
        .post("/generate")
         .param().name("amount").type(RestParamType.query).defaultValue("1").endParam()
         .route()
          .setHeader(Exchange.HTTP_RESPONSE_CODE, simple("201"))
          .setBody(constant("Done"))
         .to("bean://generateprocessor")
        .endRest();
       
      rest("/reset")
        .post()
        .route()
         .circuitBreaker()
          .to("bean://queryutils?method=prepareReset")
          .marshal(jaxbDataFormat)
          .to("activemq2:queue:orderCommand?jmsMessageType=Text")
          .to("bean://queryutils?method=decodeReset")
          .setHeader(Exchange.HTTP_RESPONSE_CODE, simple("200"))
          .setBody(constant("Done"))
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
          .to("bean://queryutils?method=prepareRebuild")
          .marshal(jaxbDataFormat)
          .to("activemq2:queue:orderCommand?jmsMessageType=Text")
          .to("bean://queryutils?method=decodeRebuild")
          .setHeader(Exchange.HTTP_RESPONSE_CODE, simple("200"))
          .setBody(constant("Done"))
         .onFallback()
           .setBody(constant("Dependant service not available"))
           .setHeader(Exchange.CONTENT_TYPE, constant("text/plain"))
           .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
         .end()
        .endRest();
       
    }

}
