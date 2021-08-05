package org.gmagnotta.model;

import java.io.Serializable;

import org.infinispan.protostream.annotations.ProtoDoc;
import org.infinispan.protostream.annotations.ProtoField;

@ProtoDoc("@Indexed")
public class DenormalizedLineItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	private int id;
	
	
	private int price;
	
	
	private int quantity;
	
	
	private int orderid;
	
	
	private int itemId;

	@ProtoDoc("@Field(index=Index.YES, analyze = Analyze.NO, store = Store.NO)")
    @ProtoField(number =  1, required = true)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@ProtoDoc("@Field(index=Index.YES, analyze = Analyze.NO, store = Store.NO)")
    @ProtoField(number =  2, required = true)
	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	@ProtoDoc("@Field(index=Index.YES, analyze = Analyze.NO, store = Store.NO)")
    @ProtoField(number =  3, required = true)
	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	@ProtoDoc("@Field(index=Index.YES, analyze = Analyze.NO, store = Store.NO)")
    @ProtoField(number =  4, required = true)
	public int getOrderid() {
		return orderid;
	}

	public void setOrderid(int orderid) {
		this.orderid = orderid;
	}

	@ProtoDoc("@Field(index=Index.YES, analyze = Analyze.NO, store = Store.NO)")
    @ProtoField(number =  5, required = true)
	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}
	
	public static DenormalizedLineItem fromLineItem(LineItem lineItem) {
		
		DenormalizedLineItem denormalizedLineItem = new DenormalizedLineItem();
		denormalizedLineItem.setId(lineItem.getId());
		denormalizedLineItem.setItemId(lineItem.getItem().getId());
		denormalizedLineItem.setOrderid(lineItem.getOrder().getId());
		denormalizedLineItem.setPrice(lineItem.getPrice().intValue());
		denormalizedLineItem.setQuantity(lineItem.getQuantity());
		
		return denormalizedLineItem;
		
	}

}
