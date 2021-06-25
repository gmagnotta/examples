package org.gmagnotta.app.route;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.apache.activemq.ActiveMQXAConnectionFactory;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.activemq.ActiveMQComponent;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.jboss.narayana.jta.jms.ConnectionFactoryProxy;
import org.jboss.narayana.jta.jms.TransactionHelperImpl;
import org.springframework.transaction.jta.JtaTransactionManager;

@ApplicationScoped
public class JmsRoute extends RouteBuilder {

    private static final Logger LOG = Logger.getLogger(JmsRoute.class);

    @ConfigProperty(name = "broker.url")
    String brokerUrl;
    
    @Inject
    TransactionManager transactionManager;
    
    @Inject
    UserTransaction userTransaction;
    
    @Override
    public void configure() throws Exception {
    	
    	onException(Exception.class)
    	.handled(true)
    	.markRollbackOnly();
    	
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
        
        from("activemq:queue:ordercreated")
         .routeId("jmsRoute")
         .threads(10, 10, "jmsProcessor")
         .transacted()
         	.to("bean://aggregateprocessor")
         .end();

    }

}
