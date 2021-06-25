package org.gmagnotta.app.route;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.apache.activemq.ActiveMQXAConnectionFactory;
import org.apache.camel.Component;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.activemq.ActiveMQComponent;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.gmagnotta.model.Order;
import org.jboss.logging.Logger;
import org.jboss.narayana.jta.jms.ConnectionFactoryProxy;
import org.jboss.narayana.jta.jms.TransactionHelperImpl;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.gmagnotta.jaxb.Aggregationtype;

@ApplicationScoped
/**
 * Camel Route to parse incoming xml files
 */
public class XmlFileRoute extends RouteBuilder {

    private static final Logger LOGGER = Logger.getLogger(XmlFileRoute.class);

    @ConfigProperty(name = "orders.dir")
    String ordersDirectory;
    
    @ConfigProperty(name = "broker.url")
    String brokerUrl;
    
    @ConfigProperty(name = "processingthreads")
    int processingThreads;
    
    @Inject
    TransactionManager transactionManager;
    
    @Inject
    UserTransaction userTransaction;
    
    @Override
    public void configure() throws Exception {
    	
    	JtaTransactionManager jtaTransactionManager = new JtaTransactionManager(userTransaction, transactionManager);
    	getContext().getRegistry().bind("jtaTransactionManager", jtaTransactionManager);

    	// https://access.redhat.com/solutions/5482771
    	// https://github.com/quarkusio/quarkus/issues/5762
    	ActiveMQXAConnectionFactory activeMQConectionFactory = new ActiveMQXAConnectionFactory(brokerUrl);
    	
    	ActiveMQComponent activeMq = new ActiveMQComponent();
    	activeMq.setConnectionFactory(new ConnectionFactoryProxy(activeMQConectionFactory, new TransactionHelperImpl(transactionManager)));
    	activeMq.setTransacted(true);
    	activeMq.setTransactionManager(jtaTransactionManager);
    	
    	getContext().addComponent("activemq", activeMq);
    	
        JaxbDataFormat jaxbDataFormat = new JaxbDataFormat("org.gmagnotta.jaxb");
        jaxbDataFormat.setSchema("classpath:order.xsd");
        
        onException(Exception.class)
         .log("Exception occurred during route processing: ${exception.stacktrace}")
         .markRollbackOnly();

        from("file:" + ordersDirectory + "?moveFailed=.error&readLock=markerFile&readLockTimeout=100&readLockCheckInterval=20&maxMessagesPerPoll=" + processingThreads)
         .routeId("fileRoute")
         .log("Consuming file ${headers.CamelFileAbsolutePath}")
         .threads(processingThreads, processingThreads, "fileProcessor")
          .unmarshal(jaxbDataFormat)
          .transacted()
           .to("bean://orderprocessor")
           .setBody(simple("${headers.OrderEntity.id}", String.class))
           .to("activemq:queue:ordercreated")
          .end()
         .log("done processing order ${headers.OrderEntity.id}");
        
    }
}
