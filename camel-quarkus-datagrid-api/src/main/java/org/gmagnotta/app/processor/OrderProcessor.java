package org.gmagnotta.app.processor;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.transaction.Transactional;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.gmagnotta.jaxb.Item;
import org.gmagnotta.jaxb.LineItem;
import org.gmagnotta.jaxb.Lineitemtype;
import org.gmagnotta.jaxb.Order;
import org.gmagnotta.jaxb.OrderCommandRequest;
import org.gmagnotta.jaxb.OrderCommandRequestEnum;
import org.gmagnotta.jaxb.Ordertype;
import org.jboss.logging.Logger;

@ApplicationScoped
@Named("orderprocessor")
/**
 * This class is a Camel Processor used to perse incoming xml files and transform as Order objects.
 * 
 * The resulting Order object is saved as OrderEntity header in the message.
 */
public class OrderProcessor implements Processor {
	
	private static final Logger LOGGER = Logger.getLogger(OrderProcessor.class);

    @Transactional
    public void process(Exchange exchange) throws Exception {

        // extract unmarshaled xml
        Ordertype xmlOrder = exchange.getIn().getBody(Ordertype.class);

        // create new entity
        Order order = new Order();
        order.setExternalOrderId(xmlOrder.getOrderid());
        
        for (Lineitemtype xmlLineItem : xmlOrder.getLineitem()) {
        	
        	Item i = new Item();
        	i.setId(Integer.valueOf(xmlLineItem.getItemid()));

            LineItem lineItem = new LineItem();
            order.getLineItem().add(lineItem);

            lineItem.setItem(i);

            lineItem.setQuantity(xmlLineItem.getQuantity().intValue());

        }

        GregorianCalendar c = new GregorianCalendar();
        c.setTime(new Date());
        XMLGregorianCalendar date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        order.setCreationDate(date2);

        OrderCommandRequest request = new OrderCommandRequest();
        request.setOrder(order);
        request.setOrderCommandEnum(OrderCommandRequestEnum.ORDER_RECEIVED);
        
        exchange.getIn().setBody(request);
        
    }
    
}
