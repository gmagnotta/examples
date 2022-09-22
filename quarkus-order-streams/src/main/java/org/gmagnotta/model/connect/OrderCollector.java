package org.gmagnotta.model.connect;

import java.util.ArrayList;
import java.util.List;

public class OrderCollector {
    public List<EnrichedOrder> orders;
    public String id;

    public OrderCollector() {
        this.orders = new ArrayList<EnrichedOrder>();
    }

    public List<EnrichedOrder> getOrders() {
        return orders;
    }

    public void addOrder(EnrichedOrder order) {
        this.orders.add(order);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        for (EnrichedOrder o : orders) {
             buffer.append(o.getLineItem().getItem().getId());
        }

        return buffer.toString();
    }
    
}
