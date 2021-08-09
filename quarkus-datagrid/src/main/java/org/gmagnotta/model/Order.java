package org.gmagnotta.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.infinispan.protostream.annotations.ProtoDoc;
import org.infinispan.protostream.annotations.ProtoField;
import org.infinispan.protostream.descriptors.Type;


@ProtoDoc("@Indexed")
public class Order implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private int id;

    private Date creationDate;

    private Date orderWithdrawal;

    private BigDecimal amount;
    
    private String externalOrderId;

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
    	return "Order id: " + id + "; creationDate " + creationDate + "; amount " + amount  + " items:" + Arrays.toString(lineItems.toArray());
    }

}
