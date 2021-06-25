package org.gmagnotta.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.infinispan.protostream.annotations.ProtoDoc;
import org.infinispan.protostream.annotations.ProtoField;
import org.infinispan.protostream.descriptors.Type;


@Entity
@Table(name = "orders")
@NamedQueries({
        @NamedQuery(name = "getAllOrders", query = "SELECT DISTINCT o FROM Order o INNER JOIN FETCH o.lineItems"),
        @NamedQuery(name = "getOrderById", query = "SELECT DISTINCT o FROM Order o INNER JOIN FETCH o.lineItems where o.id = :id") })
@ProtoDoc("@Indexed")
public class Order implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private int id;

    @Column(name = "creation_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    @Column(name = "order_withdrawal")
    @Temporal(TemporalType.TIMESTAMP)
    private Date orderWithdrawal;

    private BigDecimal amount;
    
    private String externalOrderId;

    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER)
    private Set<LineItem> lineItems;
    
    public Order() {
        this.lineItems = new HashSet<>();
    }

    @ProtoDoc("@Field(index=Index.YES, analyze = Analyze.YES, store = Store.NO)")
    @ProtoField(number =  1, required = true)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @ProtoDoc("@Field(index=Index.YES, analyze = Analyze.YES, store = Store.NO)")
    @ProtoField(number =  2, required = true)
    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getOrderWithdrawal() {
        return orderWithdrawal;
    }

    public void setOrderWithdrawal(Date orderWithdrawal) {
        this.orderWithdrawal = orderWithdrawal;
    }

    @ProtoDoc("@Field(index=Index.YES, analyze = Analyze.YES, store = Store.NO)")
    @ProtoField(number =  3, required = true)
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @ProtoField(number =  4)
    public Set<LineItem> getLineItems() {
        return lineItems;
    }

    public void addLineItem(LineItem lineItem) {
        lineItems.add(lineItem);
    }
    
    public void setLineItems(Set<LineItem> lineItems) {
    	this.lineItems = lineItems;
    }
    
    @ProtoField(number =  5, required = false)
    public String getExternalOrderId() {
		return externalOrderId;
	}

	public void setExternalOrderId(String externalOrderId) {
		this.externalOrderId = externalOrderId;
	}

	@Override
    public int hashCode() {
        return java.util.Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj)
            return true;

        if (obj == null)
            return false;

        if (obj instanceof Order) {

            if (((Order) obj).getId() == id)
                return true;

        }

        return false;
    }
    
    @Override
    public String toString() {
    	return "Order id: " + id + "; creationDate " + creationDate + "; amount " + amount;
    }

}
