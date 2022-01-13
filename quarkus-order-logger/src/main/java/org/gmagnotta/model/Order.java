package org.gmagnotta.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
    	return "Order id: " + externalOrderId + "; creationDate " + creationDate + "; items:" + Arrays.toString(lineItems.toArray());
    }

}
