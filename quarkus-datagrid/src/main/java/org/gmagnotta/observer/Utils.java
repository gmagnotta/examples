package org.gmagnotta.observer;

import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
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
	
	public static <T> StringWriter marshall(JAXBElement<T> jaxbElement) throws JAXBException {
		
		JAXBContext jaxbContext = JAXBContext.newInstance(jaxbElement.getValue().getClass());
    	StringWriter stringWriter = new StringWriter();
    	Marshaller marshaller = jaxbContext.createMarshaller();
    	marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    	marshaller.marshal(jaxbElement, stringWriter);
    	
    	return stringWriter;
    	
	}
	
	public static org.gmagnotta.model.Order convertoToModel(org.gmagnotta.model.event.Orderchangeevent.Order order) {
		
		org.gmagnotta.model.Order modelOrder = new org.gmagnotta.model.Order();
		
		modelOrder.setId(order.getId());
		modelOrder.setCreationDate(new Date(order.getCreationDate()));
		modelOrder.setAmount(convertToModel(order.getAmount()));
		modelOrder.setExternalOrderId(order.getExternalOrderId());
		
		for (org.gmagnotta.model.event.Orderchangeevent.LineItem l : order.getLineItemsList()) {
            
			
			org.gmagnotta.model.LineItem modelLineItem = convertToModel(l);
			modelLineItem.setOrder(modelOrder);
			
			modelOrder.addLineItem(modelLineItem);

            
        }
		
    	return modelOrder;
		
	}
	
	public static org.gmagnotta.model.LineItem convertToModel(org.gmagnotta.model.event.Orderchangeevent.LineItem lineItem) {
		
		org.gmagnotta.model.LineItem model = new org.gmagnotta.model.LineItem();
		model.setId(lineItem.getId());
		model.setPrice(convertToModel(lineItem.getPrice()));
//    	model.setOrder(modelOrder);
		model.setItem(convertToModel(lineItem.getItem()));
		model.setQuantity(lineItem.getQuantity());
		model.setOrderid(lineItem.getOrderid());
		
		return model;
    	
	}
	
	public static org.gmagnotta.model.Item convertToModel(org.gmagnotta.model.event.Orderchangeevent.Item item) {
		
    	org.gmagnotta.model.Item modelItem = new org.gmagnotta.model.Item();
    	
    	modelItem.setId(item.getId());
    	modelItem.setDescription(item.getDescription());
    	modelItem.setPrice(convertToModel(item.getPrice()));
		
		return modelItem;
		
	}
	
	public static BigDecimal convertToModel(org.gmagnotta.model.event.Orderchangeevent.BigDecimal price) {
		
    	BigInteger bi = new BigInteger(price.getUnscaledValue().toByteArray());
    	return (new BigDecimal(bi, price.getScale()));
    	
	}

}
