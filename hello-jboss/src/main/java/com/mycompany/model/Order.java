package com.mycompany.model;

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
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "orders")
@NamedQueries({
		@NamedQuery(name = "getAllOrders", query = "SELECT DISTINCT o FROM Order o INNER JOIN FETCH o.lineItems ORDER BY o.id ASC"),
})
@NamedNativeQueries({
		@NamedNativeQuery(name = "getTopOrders", query = "select id, amount from orders order by amount desc, creation_date desc")
})
public class Order implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private int id;

	@Column(name = "creation_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date creationDate;

	private BigDecimal amount;

	@OneToMany(mappedBy = "order", fetch = FetchType.EAGER)
	private Set<LineItem> lineItems;

	@Column(name = "username")
	private String user;

	public Order() {
		this.lineItems = new HashSet<>();
		this.creationDate = new Date();
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

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Set<LineItem> getLineItems() {
		return lineItems;
	}

	public void addLineItem(LineItem lineItem) {
		lineItems.add(lineItem);
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
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

}
