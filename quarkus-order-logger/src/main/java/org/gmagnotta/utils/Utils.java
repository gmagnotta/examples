package org.gmagnotta.utils;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

public class Utils {
	
	public static <T> T unmarshall(Class<T> type, String string) throws JAXBException {
		
		JAXBContext jaxbContext = JAXBContext.newInstance(type);
    	Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
    	
    	StringReader stringReader = new StringReader(string);
    	Source source = new StreamSource(stringReader);
    	return unmarshaller.unmarshal(source, type).getValue();
		
	}
	
}
