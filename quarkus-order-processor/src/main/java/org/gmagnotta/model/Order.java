package org.gmagnotta.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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

@Entity
@Table(name = "orders")
@NamedQueries({
        @NamedQuery(name = "getAllOrders", query = "SELECT DISTINCT o FROM Order o INNER JOIN FETCH o.lineItems"),
        @NamedQuery(name = "getOrderById", query = "SELECT DISTINCT o FROM Order o INNER JOIN FETCH o.lineItems where o.id = :id") })
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
    private List<LineItem> lineItems;

    public Order() {
        this.lineItems = new ArrayList<LineItem>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public List<LineItem> getLineItems() {
        return lineItems;
    }

    public void addLineItem(LineItem lineItem) {
        lineItems.add(lineItem);
    }
    
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
    	return "Order id: " + id + "; creationDate " + creationDate + "; amount " + amount + " items:" + Arrays.toString(lineItems.toArray());
    }

}
