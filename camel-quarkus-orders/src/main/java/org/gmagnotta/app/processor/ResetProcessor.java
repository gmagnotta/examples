package org.gmagnotta.app.processor;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.jboss.logging.Logger;


@ApplicationScoped
@Named("resetprocessor")
/**
 * This class is a Camel Processor used to purge the database (for demo purposes!).
 */
public class ResetProcessor implements Processor {
	
	private static final Logger LOG = Logger.getLogger(ResetProcessor.class);

	@Inject
    EntityManager entityManager;
	
	@Transactional(value = TxType.REQUIRES_NEW)
    public void process(Exchange exchange) throws Exception {
    	
    	Query query = entityManager.createNativeQuery("delete from line_item; delete from orders;");
    	
    	query.executeUpdate();
    	
    }

}
