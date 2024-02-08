package com.mycompany.model;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.NamedNativeQueries;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

@Entity
@Table(name = "items")
@NamedQueries({
		// @NamedQuery(name = "getAllItems", query = "SELECT i FROM Item i ORDER BY
		// i.id"),
		@NamedQuery(name = "getAllItems", query = "SELECT i FROM Item i ORDER BY i.price DESC"),
		@NamedQuery(name = "getItemsByDescription", query = "SELECT i FROM Item i where lower(i.description) LIKE lower(concat('%', :desc, '%')) ORDER BY i.id")
})
@NamedNativeQueries({
	@NamedNativeQuery(name = "getTopItems", query = "select item, sum(quantity) from line_items group by item order by sum(quantity) desc")
})
public class Item implements Serializable {

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

			if (((Item) obj).getDescription().equals(description) &&
					((Item) obj).getPrice().equals(price))
				return true;

		}

		return false;
	}
}
