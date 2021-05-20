package org.gmagnotta.app;

import java.math.BigDecimal;
import java.util.Date;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.gmagnotta.jaxb.Lineitemtype;
import org.gmagnotta.jaxb.Ordertype;
import org.gmagnotta.model.Item;
import org.gmagnotta.model.LineItem;
import org.gmagnotta.model.Order;

@ApplicationScoped
@Named("orderprocessor")
public class OrderProcessor implements Processor {

    @Inject
    EntityManager entityManager;

    @Transactional(TxType.REQUIRES_NEW)
    public void process(Exchange exchange) throws Exception {

        // extract unmarshaled xml
        Ordertype xmlOrder = exchange.getIn().getBody(Ordertype.class);

        // create new entity
        Order jpaOrder = new Order();
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

        exchange.getIn().setBody(jpaOrder.getId() + "");
    }

    private Item getItemById(int id) {

        Query query = entityManager.createNamedQuery("getItemById", Item.class);
        query.setParameter("id", id);

        return (Item) query.getSingleResult();
    }

}
