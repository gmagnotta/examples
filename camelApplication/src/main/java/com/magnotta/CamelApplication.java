package com.magnotta;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Example of a Camel Application 
 */
public class CamelApplication {
	
	private static final CamelContext context = new DefaultCamelContext();
	
	private static Logger logger = LoggerFactory.getLogger(PeppePrinter.class);
	
	public static void main(String[] args) throws Exception {

		context.addRoutes(new RouteBuilder() {
			
			public void configure() {
				from("file:orders/incoming")
				 .choice()
				  .when(xpath("/order/user/text() = 'peppe'"))
				   .to("file:orders/peppe")
				  .when(xpath("/order/user/text() = 'pluto'"))
				   .to("file:orders/pluto")
				//.log("new file ${header.CamelFileName} picket up from ${header.CamelFileAbsolute}")
//				.process(new PeppePrinter())
				//.filter(xpath("/order/quantity > 10"))
				.routeId("route1");
			}
			
		});
		
		logger.info("Starting Camel Context");
		context.start();
		
		/**
		 * Register a shutdown hook to stop Camel Context
		 */
		Runtime.getRuntime().addShutdownHook(new Thread() {
			
			public void run() {
				
				synchronized (context) {
					
					if (!context.isStopped()) {
						logger.info("Stopping context");
						context.stop();
						
						context.notify();
					}

				}
				
			}
		});
		
		/**
		 * While the context is not stopped, we wait
		 */
		synchronized (context) {
			
			try {
				
				while (!context.isStopped()) {
					
					logger.info("Context is running... waiting");
					
					context.wait();
					
				}
				
				logger.info("Context terminated");
			
			} catch (InterruptedException ex) {
				
				logger.error("InterruptedException", ex);
				
			}
		
		}
		
		logger.info("Done Integration");
		
	}
}
