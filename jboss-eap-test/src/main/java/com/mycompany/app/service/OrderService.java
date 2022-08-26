package com.mycompany.app.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import javax.enterprise.event.Event;

import com.mycompany.event.ExportedEvent;
import com.mycompany.event.impl.OrderCreatedEvent;
import com.mycompany.model.LineItem;
import com.mycompany.model.Order;

/**
 * Hello world!
 */
@Stateless
public class OrderService {
    @PersistenceContext(unitName = "store")
    private EntityManager entityManager;

    @Inject
    Event<ExportedEvent> event;

    public List<Order> getOrders() {

        Query query = entityManager.createNamedQuery("getAllOrders", Order.class);

        return query.getResultList();
    }

    @Transactional
    public void createOrder(Order order) throws Exception {

        entityManager.persist(order);

        for (LineItem i : order.getLineItems()) {

            entityManager.persist(i);

        }

        // fire order created event
        event.fire(OrderCreatedEvent.of(order));

    }

    public List<Object> getTopOrders() throws Exception {

        Query query = entityManager.createNativeQuery("getTopOrders", List.class);

        List<Object> queryResult = query.setMaxResults(10).getResultList();

        // Iterator<Object> iterator = queryResult.iterator();

        // List<TopValue> result = new ArrayList<TopValue>();

        // while (iterator.hasNext()) {

        // Object obj = iterator.next();

        // Object[] cast = (Object[]) obj;

        // TopValue top = new TopValue();
        // BigInteger b = (BigInteger) cast[0];
        // Integer i = (Integer) cast[1];

        // top.setId(b.intValue());
        // top.setValue(i);
        // result.add(top);
        // }

        return queryResult;
    }

}
