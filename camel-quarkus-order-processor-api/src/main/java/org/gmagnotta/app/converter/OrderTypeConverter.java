package org.gmagnotta.app.converter;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import java.util.GregorianCalendar;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;

import org.apache.camel.Exchange;
import org.apache.camel.TypeConversionException;
import org.apache.camel.support.TypeConverterSupport;
import org.gmagnotta.jaxb.Order;
import org.gmagnotta.jaxb.Ordertype;
import org.jboss.logging.Logger;
import org.gmagnotta.jaxb.Item;
import org.gmagnotta.jaxb.LineItem;
import org.gmagnotta.jaxb.Lineitemtype;

public class OrderTypeConverter extends TypeConverterSupport {

	private static final Logger LOGGER = Logger.getLogger(OrderTypeConverter.class);

	@Override
	public <T> T convertTo(Class<T> toType, Exchange exchange, Object value) throws TypeConversionException {

		if (toType.equals(Order.class) && value instanceof Ordertype) {
			Order order = new Order();

			Ordertype ordertype = (Ordertype) value;

			order.setExternalOrderId(ordertype.getOrderid());

			for (Lineitemtype xmlLineItem : ordertype.getLineitem()) {

				Item i = new Item();
				i.setId(Integer.valueOf(xmlLineItem.getItemid()));

				LineItem lineItem = new LineItem();
				order.getLineItem().add(lineItem);

				lineItem.setItem(i);

				lineItem.setQuantity(xmlLineItem.getQuantity().intValue());

			}

			try {

				GregorianCalendar c = new GregorianCalendar();
				c.setTime(new Date());
				XMLGregorianCalendar date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
				order.setCreationDate(date2);

				return (T) order;

			} catch (DatatypeConfigurationException ex) {

				throw new TypeConversionException(value, Order.class, ex);
			
			}

		}

		return null;
	}
}
