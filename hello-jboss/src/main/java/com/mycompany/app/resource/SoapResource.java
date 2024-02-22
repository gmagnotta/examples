package com.mycompany.app.resource;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gmagnotta.jaxb.Element;
import org.gmagnotta.jaxb.LineItem;
import org.gmagnotta.jaxb.Order;
import org.gmagnotta.jaxb.TopElements;

import com.mycompany.app.service.ItemService;
import com.mycompany.app.service.OrderService;

import jakarta.inject.Inject;
import jakarta.jws.WebMethod;
import jakarta.jws.WebService;

/// Read https://docs.jboss.org/author/display/JBWS/SOAP%20over%20JMS.html

@WebService(name = "SoapResource", targetNamespace = "http://com.mycompany.app.resource/SoapResource")
public class SoapResource {

    @Inject
    ItemService itemService;

    @Inject
    OrderService ordersService;

    @WebMethod
    public List<com.mycompany.model.Item> getItems() {
       
        return itemService.getAllItems();

    }

    @WebMethod
    public TopElements getTopItems() {

        Map<Integer, Integer> topItems = itemService.getTopItems();

        Set<Integer> keys = topItems.keySet();

        Iterator<Integer> iterator = keys.iterator();

        TopElements result = new TopElements();

        while (iterator.hasNext()) {
            
            Integer k = iterator.next();
            
            Integer value = topItems.get(k);
            
            Element top = new Element();

            top.setId(k.intValue());
            top.setValue(BigDecimal.valueOf(value.intValue()));
            result.getElement().add(top);

        }

        return result;
    }

    @WebMethod
    public TopElements getTopOrders() {
        Map<Integer, Double> topOrders = ordersService.getTopOrders();

        Set<Integer> keys = topOrders.keySet();

        Iterator<Integer> iterator = keys.iterator();

        TopElements result = new TopElements();

        while (iterator.hasNext()) {
            
            Integer k = iterator.next();
            
            Double value = topOrders.get(k);
            
            Element top = new Element();

            top.setId(k.intValue());
            top.setValue(BigDecimal.valueOf(value.intValue()));
            result.getElement().add(top);

        }

        return result;
    }
    
    @WebMethod
    public void createOrder(Order jaxbOrder) throws Exception {
        
        com.mycompany.model.Order jpaOrder = convertoToJpaOrder(jaxbOrder);

        ordersService.createOrder(jpaOrder);
    }

    private com.mycompany.model.Order convertoToJpaOrder(Order jaxbOrder) {

		long sum = 0;

		com.mycompany.model.Order jpaOrder = new com.mycompany.model.Order();

		jpaOrder.setCreationDate(jaxbOrder.getCreationDate().toGregorianCalendar().getTime());
		// jpaOrder.setExternalOrderId(order.getExternalOrderId());

		List<LineItem> lineItems = jaxbOrder.getLineItem();

		for (LineItem l : lineItems) {

			int i = l.getItemId();

			com.mycompany.model.Item jpaItem = itemService.getItemById(i);

			com.mycompany.model.LineItem jpaLineItem = new com.mycompany.model.LineItem();
			jpaLineItem.setItem(jpaItem);
			jpaLineItem.setOrder(jpaOrder);
			jpaLineItem.setQuantity(l.getQuantity());
			jpaLineItem.setPrice(jpaItem.getPrice());

			jpaOrder.addLineItem(jpaLineItem);

			sum += jpaItem.getPrice().multiply(BigDecimal.valueOf(jpaLineItem.getQuantity())).longValue();
		}

		jpaOrder.setAmount(BigDecimal.valueOf(sum));
		jpaOrder.setUser("JMS");
		return jpaOrder;

	}
}
