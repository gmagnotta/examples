package com.mycompany.app.service.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.ByteString;
import com.mycompany.model.Item;
import com.mycompany.model.LineItem;
import com.mycompany.model.Order;

public class OrderUtils {

    // Helper methods to serialize to Protobuf

    public static org.gmagnotta.model.event.OrderOuterClass.BigDecimal convertToProtobuf(BigDecimal bigDecimal) {
		
		return org.gmagnotta.model.event.OrderOuterClass.BigDecimal.newBuilder()
				.setScale(bigDecimal.scale())
				.setPrecision(bigDecimal.precision())
				.setValue(ByteString.copyFrom(bigDecimal.unscaledValue().toByteArray()))
				.build();
	
	}
	
	public static org.gmagnotta.model.event.OrderOuterClass.Item convertToProtobuf(Item item) {
		
		return org.gmagnotta.model.event.OrderOuterClass.Item.newBuilder()
				.setId(item.getId())
				.setDescription(item.getDescription())
				.setPrice(convertToProtobuf(item.getPrice()))
				.build();
	}
	
	public static org.gmagnotta.model.event.OrderOuterClass.LineItem convertToProtobuf(LineItem lineItem) {
		
		return org.gmagnotta.model.event.OrderOuterClass.LineItem.newBuilder()
				.setId(lineItem.getId())
				.setPrice(convertToProtobuf(lineItem.getPrice()))
				.setQuantity(lineItem.getQuantity())
				.setItem(convertToProtobuf(lineItem.getItem()))
				.build();
	}
	
	public static org.gmagnotta.model.event.OrderOuterClass.Order convertToProtobuf(Order order) {

		List<org.gmagnotta.model.event.OrderOuterClass.LineItem> pLineItems = new ArrayList<org.gmagnotta.model.event.OrderOuterClass.LineItem>();
		
		for (LineItem lineItem : order.getLineItems()) {
			pLineItems.add(convertToProtobuf(lineItem));
		}
		
		return org.gmagnotta.model.event.OrderOuterClass.Order.newBuilder()
				.setId(order.getId())
				.setCreationDate(order.getCreationDate().getTime())
				.setAmount(convertToProtobuf(order.getAmount()))
				.addAllLineItems(pLineItems)
				.setUser(order.getUser())
				.build();

	}
    
}
