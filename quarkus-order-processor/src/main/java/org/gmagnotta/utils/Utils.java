package org.gmagnotta.utils;

import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import com.google.protobuf.ByteString;


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
	
	public static org.gmagnotta.model.event.Orderchangeevent.BigDecimal convertToProtobuf(BigDecimal bigDecimal) {
		
		return org.gmagnotta.model.event.Orderchangeevent.BigDecimal.newBuilder()
				.setScale(bigDecimal.scale())
				.setUnscaledValue(ByteString.copyFrom(bigDecimal.unscaledValue().toByteArray()))
				.build();
	
	}
	
	public static org.gmagnotta.model.event.Orderchangeevent.Item convertToProtobuf(org.gmagnotta.model.Item item) {
		
		return org.gmagnotta.model.event.Orderchangeevent.Item.newBuilder()
				.setId(item.getId())
				.setDescription(item.getDescription())
				.setPrice(convertToProtobuf(item.getPrice()))
				.build();
	}
	
	public static org.gmagnotta.model.event.Orderchangeevent.LineItem convertToProtobuf(org.gmagnotta.model.LineItem lineItem) {
		
		return org.gmagnotta.model.event.Orderchangeevent.LineItem.newBuilder()
				.setId(lineItem.getId())
				.setPrice(convertToProtobuf(lineItem.getPrice()))
				.setQuantity(lineItem.getQuantity())
				.setItem(convertToProtobuf(lineItem.getItem()))
				.setOrderid(lineItem.getOrder().getId())
				.build();
	}
	
	public static org.gmagnotta.model.event.Orderchangeevent.Order convertToProtobuf(org.gmagnotta.model.Order order) {

		List<org.gmagnotta.model.event.Orderchangeevent.LineItem> pLineItems = new ArrayList<org.gmagnotta.model.event.Orderchangeevent.LineItem>();
		
		for (org.gmagnotta.model.LineItem lineItem : order.getLineItems()) {
			pLineItems.add(convertToProtobuf(lineItem));
		}
		
		return org.gmagnotta.model.event.Orderchangeevent.Order.newBuilder()
				.setId(order.getId())
				.setCreationDate(order.getCreationDate().getTime())
				.setAmount(convertToProtobuf(order.getAmount()))
				.addAllLineItems(pLineItems)
				.setExternalOrderId(order.getExternalOrderId())
				.build();

	}

}
