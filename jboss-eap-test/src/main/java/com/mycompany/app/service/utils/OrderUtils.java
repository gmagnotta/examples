package com.mycompany.app.service.utils;

import java.io.StringWriter;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.mycompany.model.LineItem;
import com.mycompany.model.Order;

import org.gmagnotta.jaxb.ObjectFactory;
import org.gmagnotta.jaxb.OrderCommandRequest;
import org.gmagnotta.jaxb.OrderCommandRequestEnum;

public class OrderUtils {

    public static String marshallOrder(Order order) throws JAXBException, DatatypeConfigurationException {
        
        JAXBContext jaxbContext = JAXBContext.newInstance(OrderCommandRequest.class);
        Marshaller mar = jaxbContext.createMarshaller();

        org.gmagnotta.jaxb.Order jaxbOrder = new org.gmagnotta.jaxb.Order();
        
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(new Date());
        XMLGregorianCalendar date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        jaxbOrder.setCreationDate(date2);
        jaxbOrder.setExternalOrderId(order.getId() + "");

        for (LineItem i : order.getLineItems()) {
        
            org.gmagnotta.jaxb.Item item = new org.gmagnotta.jaxb.Item();
			item.setId(Integer.valueOf(i.getItem().getId()));
            
            org.gmagnotta.jaxb.LineItem jaxbLineItem = new org.gmagnotta.jaxb.LineItem();
            jaxbLineItem.setItem(item);
            jaxbLineItem.setQuantity(i.getQuantity());

            jaxbOrder.getLineItem().add(jaxbLineItem);
        
        }

        OrderCommandRequest request = new OrderCommandRequest();
        request.setOrder(jaxbOrder);
        request.setOrderCommandEnum(OrderCommandRequestEnum.ORDER_RECEIVED);

        mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        StringWriter sw = new StringWriter();
        mar.marshal(new ObjectFactory().createOrderCommandRequest(request), sw);

        return sw.toString();

    }
    
}
