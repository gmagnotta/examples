package com.mycompany.app.service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

import com.mycompany.model.LineItem;
import com.mycompany.model.Order;

@Stateless
public class OrderService {
    @PersistenceContext(unitName = "store")
    private EntityManager entityManager;

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
