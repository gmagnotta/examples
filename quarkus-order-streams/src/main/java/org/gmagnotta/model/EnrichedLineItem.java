package org.gmagnotta.model;

import org.gmagnotta.model.connect.Item;
import org.gmagnotta.model.connect.LineItem;

public class EnrichedLineItem {

    private LineItem lineItem;
    private Item item;

    public LineItem getLineItem() {
        return lineItem;
    }
    public void setLineItem(LineItem lineItem) {
        this.lineItem = lineItem;
    }
    public Item getItem() {
        return item;
    }
    public void setItem(Item item) {
        this.item = item;
    }

    public String toString() {
        return item.toString() + " " + lineItem.toString();
    }

}
