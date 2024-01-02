package com.mycompany.app.service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    public Map<Integer, Double> getTopOrders() {

        Query query = entityManager.createNamedQuery("getTopOrders");

        List<Object[]> queryResult = query.setMaxResults(10).getResultList();

        Map<Integer, Double> values = new LinkedHashMap<>();

        for (Object[] a : queryResult) {

            Integer id = (Integer) a[0];
            BigDecimal amount = (BigDecimal) a[1];

            values.put(id, Double.valueOf(amount.doubleValue()));
        }

        return values;

    }

}
