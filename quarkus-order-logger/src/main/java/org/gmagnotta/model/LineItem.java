package org.gmagnotta.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class LineItem implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private int id;

    private BigDecimal price;

    private Order order;

    private Item Item;

    private int quantity;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Item getItem() {
        return Item;
    }

    public void setItem(Item item) {
        Item = item;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public String toString() {
    	return "LineItem id: " + id + "; item " + Item + "; qty " + quantity;
    }

}
