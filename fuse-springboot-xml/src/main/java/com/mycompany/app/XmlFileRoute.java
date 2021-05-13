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
		
		from("file:/tmp/books")
		 .routeId("file")
		 .unmarshal(jaxbDataFormat)
		 .process(new CustomProcessor())
		 .log("Sending ${body}")
		 .to("mock:test");
		
	}
	
}