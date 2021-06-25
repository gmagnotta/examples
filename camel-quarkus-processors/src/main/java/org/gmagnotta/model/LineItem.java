package org.gmagnotta.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.infinispan.protostream.annotations.ProtoDoc;
import org.infinispan.protostream.annotations.ProtoField;
import org.infinispan.protostream.descriptors.Type;

@Entity
@Table(name = "line_item")
@ProtoDoc("@Indexed")
public class LineItem implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private int id;

    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "orders")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "item")
    private Item Item;

    private int quantity;
    
    @Transient
    private int orderid;

    @ProtoField(number =  1, required = true)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @ProtoField(number =  2, required = true)
    @ProtoDoc("@Field(index=Index.YES, analyze = Analyze.YES, store = Store.NO)")
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

    @ProtoField(number =  4, required = true)
    public Item getItem() {
        return Item;
    }

    public void setItem(Item item) {
        Item = item;
    }

    @ProtoField(number =  3, required = true)
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    @ProtoField(number =  5, required = true)
    public int getOrderid() {
		return orderid;
	}

	public void setOrderid(int orderid) {
		this.orderid = orderid;
	}

	public String toString() {
    	return "LineItem id: " + id + "; price " + price + "; item " + Item + "; qty " + quantity + "; orderid " + orderid;
    }

}
