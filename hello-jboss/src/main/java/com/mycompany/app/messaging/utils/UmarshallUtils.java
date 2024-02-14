package com.mycompany.app.messaging.utils;

import java.io.StringReader;
import java.io.StringWriter;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

public class UmarshallUtils {
    
    public static <T> T unmarshall(Class<T> type, String string) throws JAXBException {
		
		JAXBContext jaxbContext = JAXBContext.newInstance(type);
    	Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
    	
    	StringReader stringReader = new StringReader(string);
    	Source source = new StreamSource(stringReader);
    	return unmarshaller.unmarshal(source, type).getValue();
		
	}
	
	public static <T> StringWriter marshall(JAXBElement<T> jaxbElement) throws JAXBException {
		
		JAXBContext jaxbContext = JAXBContext.newInstance(jaxbElement.getValue().getClass());
    	StringWriter stringWriter = new StringWriter();
    	Marshaller marshaller = jaxbContext.createMarshaller();
    	//marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    	marshaller.marshal(jaxbElement, stringWriter);
    	
    	return stringWriter;
    	
	}
}
