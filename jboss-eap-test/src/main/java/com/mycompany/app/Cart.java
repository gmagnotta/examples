package com.mycompany.app;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mycompany.model.Item;

public class Cart implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Map<Integer, CartItem> items;
	
	public Cart() {
		this.items = new HashMap<Integer, CartItem>();
	}
	
	public void addToCart(Item item, int quantity) {
		
		CartItem value = items.get(item.getId());
		
		if (value == null) {
			items.put(item.getId(), new CartItem(item, quantity));
		} else {
			value.setQuantity(quantity + value.getQuantity());
		}
	}
	
	public void updateItemQuantity(Item item, int quantity) {
		
		CartItem value = items.get(item.getId());
		
		if (value != null) {
			value.setQuantity(quantity);
		}
	}
	
	public List<CartItem> getItems() {
		return new ArrayList<CartItem>(items.values());
	}
	
	public void deleteFromCart(Item item) {
		CartItem value = items.get(item.getId());
		
		if (value != null) {
			items.remove(item.getId());
		}
	}
	
	public float getTotal() {
		
		float sum = 0;
		
		for (CartItem i : items.values()) {
			
			sum += (i.getItem().getPrice().multiply(BigDecimal.valueOf(i.getQuantity()))).floatValue();
			
		}
		
		return sum;
	}
}
