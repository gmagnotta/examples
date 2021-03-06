package org.gmagnotta.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "items")
@NamedQueries({ @NamedQuery(name = "getAllItems", query = "SELECT i FROM Item i ORDER BY i.id"),
        @NamedQuery(name = "getItemById", query = "SELECT i FROM Item i where i.id = :id") })
public class Item implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
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
}
