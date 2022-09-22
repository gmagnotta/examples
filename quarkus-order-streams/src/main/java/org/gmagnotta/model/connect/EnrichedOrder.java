package org.gmagnotta.model.connect;

import org.gmagnotta.model.EnrichedLineItem;

public class EnrichedOrder {

    private Order order;
    private EnrichedLineItem lineItem;

    public Order getOrder() {
        return order;
    }
    public void setOrder(Order order) {
        this.order = order;
    }
    public EnrichedLineItem getLineItem() {
        return lineItem;
    }
    public void setLineItem(EnrichedLineItem lineItem) {
        this.lineItem = lineItem;
    }

    public String toString() {
        return "order " + order.toString() + " lineItem " + lineItem;
    }
    
}
