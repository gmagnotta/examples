package com.magnotta;

import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PeppePrinter implements Processor {
	
	private static Logger logger = LoggerFactory.getLogger(PeppePrinter.class);

	public void process(Exchange exchange) throws Exception {
		
		String body = exchange.getIn().getBody(String.class);
		logger.info("Body: " + body);

		logger.info("Headers:");
		
		Map<String,Object> headers = exchange.getIn().getHeaders();
		for(String key: headers.keySet()){
			logger.info("Key: " + key + " | Value: " + headers.get(key));
		}
	}

}
