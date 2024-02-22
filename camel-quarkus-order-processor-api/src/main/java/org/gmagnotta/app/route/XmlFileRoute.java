package org.gmagnotta.app.route;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.gmagnotta.app.converter.OrderTypeConverter;
import org.gmagnotta.jaxb.Order;

import resource.app.mycompany.com.soapresource.CreateOrder;

@ApplicationScoped
/**
 * Camel Route to parse incoming xml files
 */
public class XmlFileRoute extends RouteBuilder {

    @ConfigProperty(name = "orders.dir")
    String ordersDirectory;
    
    @ConfigProperty(name = "processingthreads")
    int processingThreads;

    @Inject
    @ConfigProperty(name = "app.webservice.soap.url")
    private String soapurl;

    @Override
    public void configure() throws Exception {
    	
        getContext().getTypeConverterRegistry().addTypeConverter(CreateOrder.class, Order.class, new OrderTypeConverter());
    	
        JaxbDataFormat jaxbDataFormat = new JaxbDataFormat("org.gmagnotta.jaxb");
        //jaxbDataFormat.setSchema("classpath:order.xsd");

        JaxbDataFormat soapDataFormat = new JaxbDataFormat("resource.app.mycompany.com.soapresource");

        onException(Exception.class)
         .log("Exception occurred during route processing: ${exception.stacktrace}")
         .markRollbackOnly();

        from("file:" + ordersDirectory + "?moveFailed=.error&readLock=markerFile&readLockTimeout=100&readLockCheckInterval=20&maxMessagesPerPoll=" + processingThreads)
         .routeId("fileRoute")
         .log("Consuming file ${headers.CamelFileAbsolutePath}")
         .threads(processingThreads, processingThreads, "fileProcessor")
          .unmarshal(jaxbDataFormat)
          .convertBodyTo(CreateOrder.class)
          .marshal(soapDataFormat)
          //.to("activemq:queue:createOrderCommand?jmsMessageType=Text")
          .toD("cxf:" + soapurl + "?exchangePattern=InOut&dataFormat=PAYLOAD")
          .log("done processing order ${headers.OrderEntity.id}");
        
    }
}
