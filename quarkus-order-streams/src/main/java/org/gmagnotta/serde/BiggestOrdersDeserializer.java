package org.gmagnotta.serde;

import java.util.Iterator;
import java.util.List;

import org.apache.kafka.common.serialization.Deserializer;
import org.gmagnotta.model.BiggestOrders;
import org.gmagnotta.model.connect.Order;

import com.google.protobuf.InvalidProtocolBufferException;

public class BiggestOrdersDeserializer implements Deserializer<BiggestOrders> {

    private int maxSize;

    public BiggestOrdersDeserializer(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public BiggestOrders deserialize(String topic, byte[] data) {

        BiggestOrders orders = new BiggestOrders(maxSize);
        
        try {
            org.gmagnotta.model.event.OrderOuterClass.BiggestOrders biggest = org.gmagnotta.model.event.OrderOuterClass.BiggestOrders.parseFrom(data);

            List<org.gmagnotta.model.event.OrderOuterClass.BiggestOrder> orderList = biggest.getOrdersList();

            Iterator<org.gmagnotta.model.event.OrderOuterClass.BiggestOrder> i = orderList.iterator();

            while (i.hasNext()) {
                org.gmagnotta.model.event.OrderOuterClass.BiggestOrder o = i.next();

                Order order = new Order();
                order.setId(o.getId());
                order.setAmount(o.getAmount());

                orders.add(order);
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

        return orders;
    }
    
}
