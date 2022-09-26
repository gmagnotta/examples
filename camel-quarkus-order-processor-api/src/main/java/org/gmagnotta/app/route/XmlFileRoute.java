package org.gmagnotta.app.route;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.activemq.ActiveMQComponent;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.gmagnotta.app.converter.OrderTypeConverter;
import org.gmagnotta.jaxb.Order;
import org.gmagnotta.jaxb.Ordertype;
import org.jboss.logging.Logger;

@ApplicationScoped
/**
 * Camel Route to parse incoming xml files
 */
public class XmlFileRoute extends RouteBuilder {

    private static final Logger LOGGER = Logger.getLogger(XmlFileRoute.class);

    @ConfigProperty(name = "orders.dir")
    String ordersDirectory;
    
    @ConfigProperty(name = "quarkus.artemis.url")
    String brokerUrl;

    @ConfigProperty(name = "quarkus.artemis.username")
    String brokerUsername;

    @ConfigProperty(name = "quarkus.artemis.password")
    String brokerPassword;
    
    @ConfigProperty(name = "processingthreads")
    int processingThreads;

    @Produces
    ActiveMQConnectionFactory activeMQConectionFactory;
    
    @Override
    public void configure() throws Exception {
    	
    	activeMQConectionFactory = new ActiveMQConnectionFactory(brokerUsername, brokerPassword, brokerUrl);
    	
    	ActiveMQComponent activeMq = new ActiveMQComponent();
    	activeMq.setConnectionFactory(activeMQConectionFactory);
    	
    	getContext().addComponent("activemq", activeMq);
        
        getContext().getTypeConverterRegistry().addTypeConverter(Order.class, Ordertype.class, new OrderTypeConverter());
    	
        JaxbDataFormat jaxbDataFormat = new JaxbDataFormat("org.gmagnotta.jaxb");
        //jaxbDataFormat.setSchema("classpath:order.xsd");

        onException(Exception.class)
         .log("Exception occurred during route processing: ${exception.stacktrace}")
         .markRollbackOnly();

        from("file:" + ordersDirectory + "?moveFailed=.error&readLock=markerFile&readLockTimeout=100&readLockCheckInterval=20&maxMessagesPerPoll=" + processingThreads)
         .routeId("fileRoute")
         .log("Consuming file ${headers.CamelFileAbsolutePath}")
         .threads(processingThreads, processingThreads, "fileProcessor")
          .unmarshal(jaxbDataFormat)
          .convertBodyTo(Order.class)
          .to("bean://orderprocessor")
          .marshal(jaxbDataFormat)
          .to("activemq:queue:createOrderCommand?jmsMessageType=Text")
         .log("done processing order ${headers.OrderEntity.id}");
        
    }
}
