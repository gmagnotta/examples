package com.mycompany.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;

@Entity
@NamedQuery(name = "getAllShops", query = "SELECT DISTINCT s FROM Shop s")
public class Shop implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private int id;
	
	private String description;
	
	private String address;

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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	@Override
    public int hashCode() {
        return java.util.Objects.hashCode(description);
    }
	
	@Override
	public boolean equals(Object obj) {
		
		if (this == obj) return true;
		
		if (obj == null) return false;
		
		if (obj instanceof Shop) {
			
			if ( ((Shop) obj).getDescription().equals(description) &&
					((Shop) obj).getAddress().equals(address)) return true;
		
		}
		
		return false;
	}

}
