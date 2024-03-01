package org.gmagnotta.app.route;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.activemq.ActiveMQComponent;
import org.apache.camel.component.jsonb.JsonbDataFormat;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import resource.app.mycompany.com.soapresource.GetItems;
import resource.app.mycompany.com.soapresource.GetTopItems;
import resource.app.mycompany.com.soapresource.GetTopOrders;

/**
 * Camel Route to expose Rest APIs
 */
@ApplicationScoped
public class RestRoute extends RouteBuilder {

  @ConfigProperty(name = "app.webservice.soap.url")
  private String soapurl;
  
  @ConfigProperty(name = "quarkus.artemis.url")
  String brokerUrl;

  @ConfigProperty(name = "quarkus.artemis.username")
  String brokerUsername;

  @ConfigProperty(name = "quarkus.artemis.password")
  String brokerPassword;

  @Produces
  ActiveMQConnectionFactory activeMQConectionFactory; // needed to trigger creation with produces

  @Override
  public void configure() throws Exception {

    JaxbDataFormat soapDataFormat = new JaxbDataFormat("resource.app.mycompany.com.soapresource");

    JsonbDataFormat jsonDataFormat = new JsonbDataFormat();

    activeMQConectionFactory = new ActiveMQConnectionFactory(brokerUsername, brokerPassword, brokerUrl);
    	
    ActiveMQComponent activeMq = new ActiveMQComponent();
    activeMq.setConnectionFactory(activeMQConectionFactory);
    
    getContext().addComponent("activemq", activeMq);

    restConfiguration()
        .component("platform-http")
        .bindingMode(RestBindingMode.auto)
        .contextPath("/api");

    rest()
        .get("/topOrders")
          .to("direct:topOrders")
        .get("/topItems")
          .to("direct:topItems")
        .get("/items")
          .to("direct:items")
        .get("/ping")
          .to("direct:ping")
        .post("/generate").param().name("amount").type(RestParamType.query).defaultValue("1").endParam()
          .to("direct:generate");

    from("direct:topOrders")
        .routeId("topOrders")

        .process(e -> {
          Message m = e.getIn();

          GetTopOrders request = new GetTopOrders();

          m.setBody(request);
        })

        .marshal(soapDataFormat)
        // .log("REST 2 SOAP : SOAP request payload")
        // .log(body().toString())
        .circuitBreaker()
          .toD("cxf:" + soapurl + "?exchangePattern=InOut&dataFormat=PAYLOAD")
          // .log("REST 2 SOAP : SOAP response")
          // .log(body().toString())
          .unmarshal(soapDataFormat)
          .marshal(jsonDataFormat)
          .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
        .onFallback()
          .setBody(constant("Dependant service not available"))
          .setHeader(Exchange.CONTENT_TYPE, constant("text/plain"))
          .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
        .endCircuitBreaker()
        .end();

    from("direct:topItems")
        .routeId("topItems")

        .process(e -> {
          Message m = e.getIn();

          GetTopItems request = new GetTopItems();

          m.setBody(request);
        })

        .marshal(soapDataFormat)
        // .log("REST 2 SOAP : SOAP request payload")
        // .log(body().toString())
        .circuitBreaker()
          .toD("cxf:" + soapurl + "?exchangePattern=InOut&dataFormat=PAYLOAD")
          // .log("REST 2 SOAP : SOAP response")
          // .log(body().toString())
          .unmarshal(soapDataFormat)
          .marshal(jsonDataFormat)
          .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
        .onFallback()
          .setBody(constant("Dependant service not available"))
          .setHeader(Exchange.CONTENT_TYPE, constant("text/plain"))
          .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
        .endCircuitBreaker()
        .end();

    from("direct:items")
        .routeId("items")

        .process(e -> {
          Message m = e.getIn();

          GetItems request = new GetItems();

          m.setBody(request);
        })

        .marshal(soapDataFormat)
        // .log("REST 2 SOAP : SOAP request payload")
        // .log(body().toString())
        .circuitBreaker()
          .toD("cxf:" + soapurl + "?exchangePattern=InOut&dataFormat=PAYLOAD")
          // .log("REST 2 SOAP : SOAP response")
          // .log(body().toString())
          .unmarshal(soapDataFormat)
          .marshal(jsonDataFormat)
          .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
        .onFallback()
          .setBody(constant("Dependant service not available"))
          .setHeader(Exchange.CONTENT_TYPE, constant("text/plain"))
          .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
        .endCircuitBreaker()
        .end();


    from("direct:generate")
      .setHeader(Exchange.CONTENT_TYPE, constant("text/plain"))
      .setHeader(Exchange.HTTP_RESPONSE_CODE, simple("201"))
      .setBody(constant("Done"))
      .to("bean://generateprocessor")
      .end();

    from("direct:ping")
      .routeId("ping")
      .setBody(constant("PING"))
      // .log("REST 2 SOAP : SOAP request payload")
      // .log(body().toString())
      .circuitBreaker()
        .toD("activemq:queue:pingCommand?jmsMessageType=Text")
        // .log("REST 2 SOAP : SOAP response")
        // .log(body().toString())
        .setHeader(Exchange.CONTENT_TYPE, constant("text/plain"))
      .onFallback()
        .setBody(constant("Dependant service not available"))
        .setHeader(Exchange.CONTENT_TYPE, constant("text/plain"))
        .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
      .endCircuitBreaker()
      .end();

  }

}
