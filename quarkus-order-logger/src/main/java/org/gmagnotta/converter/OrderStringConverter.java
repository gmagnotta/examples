package org.gmagnotta.converter;

import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.TypeConversionException;
import org.apache.camel.support.TypeConverterSupport;
import org.gmagnotta.model.event.OrderOuterClass.LineItem;
import org.gmagnotta.model.event.OrderOuterClass.Order;

public class OrderStringConverter extends TypeConverterSupport {

    @Override
    public <T> T convertTo(Class<T> type, Exchange exchange, Object value) throws TypeConversionException {
        if (type.equals(String.class) && value instanceof Order) {

            StringBuffer buffer = new StringBuffer();

            Order order = (Order) value;

            buffer.append("Order " + order.getId() + ": ");

            buffer.append(" user [" + order.getUser() + "] ");

            java.math.MathContext mc = new java.math.MathContext(order.getAmount().getPrecision());
            java.math.BigDecimal deserialized = new java.math.BigDecimal(
                new java.math.BigInteger(order.getAmount().getValue().toByteArray()),
                order.getAmount().getScale(),
                mc);
            
            buffer.append("amount [" + deserialized.toString() + "] ");

            StringBuffer itemsBuffer = new StringBuffer();

            List<LineItem> items = order.getLineItemsList();
            for (LineItem item : items) {
                itemsBuffer.append(item.getQuantity() + " x " + item.getItem().getDescription() + "; ");
            }

            if (items.size() > 0) {
                buffer.append("items [");
                buffer.append(itemsBuffer.toString() + "]");
            }

            return (T) buffer.toString();
        }

        return null;
    }
    
}
