package com.mycompany.app.service;

import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import com.mycompany.model.Item;

@Stateless
public class ItemService {

    @PersistenceContext(unitName = "store")
    private EntityManager entityManager;

    public void createItem(Item item) {

        entityManager.persist(item);

    }

    @SuppressWarnings("unchecked")
    public List<Item> getAllItems() {
        Query query = entityManager.createNamedQuery("getAllItems", Item.class);

        return query.getResultList();
    }

    public List<Item> getItemsByDescription(String description) {
        Query query = entityManager.createNamedQuery("getItemsByDescription", Item.class);
        query.setParameter("desc", description);

        return query.getResultList();
    }

    public Item getItemById(int id) {

        return entityManager.find(Item.class, id);

    }

    public void updateItem(Item item) {
        entityManager.merge(item);
    }

    public void deleteItem(Item item) {
        entityManager.remove(entityManager.contains(item) ? item : entityManager.merge(item));
    }

    public Map<Integer, Integer> getTopItems() {

        Query query = entityManager.createNamedQuery("getTopItems");

        List<Object[]> queryResult = query.setMaxResults(10).getResultList();

        Map<Integer, Integer> values = new LinkedHashMap<>();

        for (Object[] a : queryResult) {

            Integer item = (Integer) a[0];
            BigInteger qty = (BigInteger) a[1];

            values.put(item, Integer.valueOf(qty.intValue()));
        }

        return values;
    }

}
