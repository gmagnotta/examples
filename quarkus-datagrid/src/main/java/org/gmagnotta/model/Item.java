package org.gmagnotta.model;

import java.io.Serializable;
import java.math.BigDecimal;

import org.infinispan.protostream.annotations.ProtoField;
import org.infinispan.protostream.descriptors.Type;

public class Item implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private int id;

    private String description;

    private BigDecimal price;

    @ProtoField(number =  1, required = true)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @ProtoField(number =  2, required = true)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ProtoField(number =  3, required = true)
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
