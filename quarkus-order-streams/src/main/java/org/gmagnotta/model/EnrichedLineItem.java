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
        StringBuffer buffer = new StringBuffer();

        if (item != null) {
            buffer.append("item " + item.toString());
        }
        if (lineItem != null) {
            buffer.append("lineItem " + lineItem.toString());
        }
        return buffer.toString();
    }

}
