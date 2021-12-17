package com.mycompany.app;

import java.io.Serializable;

import com.mycompany.model.Item;

public class CartItem implements Serializable {
	
	private Item item;
	private int quantity;
	
	public CartItem(Item item) {
		this(item, 1);
	}
	
	public CartItem(Item item, int quantity) {
		this.item = item;
		this.quantity = quantity;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	public Item getItem() {
		return item;
	}
	
}
