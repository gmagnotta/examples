package com.mycompany.app;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.model.Itemtype;
import com.model.Shipordertype;

public class CustomProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		
		Shipordertype ship = exchange.getIn().getBody(Shipordertype.class);
		
		BigDecimal sum = BigDecimal.ZERO;
		
		for (Itemtype i : ship.getItem()) {
			
			BigInteger quantity = i.getQuantity();
			BigDecimal price = i.getPrice();
			
			BigDecimal amount = price.multiply(new BigDecimal(quantity));
			
			sum = sum.add(amount);
			
		}
		
		if (sum.compareTo(BigDecimal.valueOf(100)) > 0) {
		
			System.out.println("orderid "+ ship.getOrderid() + " is bigger than 100");
			exchange.getIn().setHeader("PremiumOrder", Boolean.TRUE);
			
		} else {
			
			exchange.getIn().setHeader("PremiumOrder", Boolean.FALSE);
			
		}

	}

}
