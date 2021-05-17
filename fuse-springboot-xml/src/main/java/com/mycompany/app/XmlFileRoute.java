package com.mycompany.app;

import javax.xml.bind.JAXBContext;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.springframework.stereotype.Component;

@Component
public class XmlFileRoute extends RouteBuilder {
	
	@Override
	public void configure() throws Exception {
		
		JAXBContext jaxbContext = JAXBContext.newInstance("com.model");
		JaxbDataFormat jaxbDataFormat = new JaxbDataFormat(jaxbContext);
		jaxbDataFormat.setSchema("classpath:shiporder.xsd");
		
		from("file:/tmp/books")
		 .routeId("file")
		 .to("log:test")
		 .unmarshal(jaxbDataFormat)
		 .process(new CustomProcessor())
		 .log("Sending ${headers}")
//		 	.setHeader(InfinispanConstants.OPERATION).constant(InfinispanOperation.PUT)
//		    .setHeader(InfinispanConstants.KEY).constant(123)
//		    .setHeader(InfinispanConstants.VALUE).constant("Hello")
		 .to("jms:queue:test");
//		    .to("infinispan?cacheContainerConfiguration=#cacheContainerConfiguration");
		
	}
	
}