package org.gmagnotta.app.processor;

import java.math.BigDecimal;
import java.util.Date;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.gmagnotta.jaxb.Lineitemtype;
import org.gmagnotta.jaxb.Ordertype;
import org.gmagnotta.model.Item;
import org.gmagnotta.model.LineItem;
import org.gmagnotta.model.Order;
import org.jboss.logging.Logger;

@ApplicationScoped
@Named("orderprocessor")
/**
 * This class is a Camel Processor used to perse incoming xml files and transform as Order objects.
 * 
 * The resulting Order object is saved as OrderEntity header in the message.
 */
public class OrderProcessor implements Processor {
	
	private static final Logger LOG = Logger.getLogger(OrderProcessor.class);

    @Inject
    EntityManager entityManager;
    
    @Transactional
    public void process(Exchange exchange) throws Exception {

        // extract unmarshaled xml
        Ordertype xmlOrder = exchange.getIn().getBody(Ordertype.class);

        // create new entity
        Order jpaOrder = new Order();
        jpaOrder.setExternalOrderId(xmlOrder.getOrderid());
        
        long sum = 0;

        for (Lineitemtype xmlLineItem : xmlOrder.getLineitem()) {

    		Item i = getItemById(Integer.valueOf(xmlLineItem.getItemid()));
        		
            LineItem jpaLineItem = new LineItem();
            jpaOrder.addLineItem(jpaLineItem);
            jpaLineItem.setOrder(jpaOrder);

            jpaLineItem.setItem(i);

            jpaLineItem.setQuantity(xmlLineItem.getQuantity().intValue());
            jpaLineItem.setPrice(i.getPrice());

            sum += i.getPrice().multiply(BigDecimal.valueOf(jpaLineItem.getQuantity())).longValue();

        }

        jpaOrder.setAmount(new BigDecimal(sum));
        jpaOrder.setCreationDate(new Date());

        entityManager.persist(jpaOrder);
        for (LineItem l : jpaOrder.getLineItems()) {
            entityManager.persist(l);
        }
        
        exchange.getIn().setHeader("OrderEntity", jpaOrder);
    }

    private Item getItemById(int id) {

        Query query = entityManager.createNamedQuery("getItemById", Item.class);
        query.setParameter("id", id);

        return (Item) query.getSingleResult();
    }

}
