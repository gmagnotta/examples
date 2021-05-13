package com.mycompany.app;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.model.Shipordertype;

public class CustomProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		
		Shipordertype ship = exchange.getIn().getBody(Shipordertype.class);
		
		System.out.println("orderid "+ ship.getOrderid() +"; person " + ship.getOrderperson());

	}

}
