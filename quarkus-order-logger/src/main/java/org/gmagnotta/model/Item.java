package org.gmagnotta.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class Item implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private int id;

    private String description;

    private BigDecimal price;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hashCode(description);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null)
            return false;

        if (obj instanceof Item) {

            if (((Item) obj).getDescription().equals(description) && ((Item) obj).getPrice().equals(price)
                    && ((Item) obj).getId() == id)
                return true;

        }

        return false;
    }
    
    public String toString() {
    	return "Item id: " + id + "; price " + price + "; description " + description;
    }
}
