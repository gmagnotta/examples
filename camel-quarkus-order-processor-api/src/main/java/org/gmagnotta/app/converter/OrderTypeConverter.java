package org.gmagnotta.app.converter;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import java.util.GregorianCalendar;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;

import org.apache.camel.Exchange;
import org.apache.camel.TypeConversionException;
import org.apache.camel.support.TypeConverterSupport;
import org.gmagnotta.jaxb.LineItem;
import org.gmagnotta.jaxb.Order;
import org.gmagnotta.order.ObjectFactory;
import org.jboss.logging.Logger;

import resource.app.mycompany.com.soapresource.CreateOrder;


public class OrderTypeConverter extends TypeConverterSupport {

	private static final Logger LOGGER = Logger.getLogger(OrderTypeConverter.class);

	@Override
	public <T> T convertTo(Class<T> toType, Exchange exchange, Object value) throws TypeConversionException {

		if (toType.equals(CreateOrder.class) && value instanceof Order) {
			
			Order srcOrder = (Order) value;

			ObjectFactory objectFactory = new ObjectFactory();

			org.gmagnotta.order.Order dstOrder = objectFactory.createOrder();
			
			dstOrder.setExternalOrderId(srcOrder.getExternalOrderId());
			
			for (LineItem l : srcOrder.getLineItem()) {
				
				org.gmagnotta.order.LineItem lineItem = objectFactory.createLineItem();
				dstOrder.getLineItem().add(lineItem);
				
				lineItem.setItemId(Integer.valueOf(l.getItemId()));
				lineItem.setQuantity(l.getQuantity());

			}
			
			CreateOrder request = new CreateOrder();
			request.setArg0(dstOrder);

			try {

				GregorianCalendar c = new GregorianCalendar();
				c.setTime(new Date());
				XMLGregorianCalendar date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
				dstOrder.setCreationDate(date2);

				return (T) request;

			} catch (DatatypeConfigurationException ex) {

				throw new TypeConversionException(value, Order.class, ex);
			
			}

		}

		return null;
	}
}
