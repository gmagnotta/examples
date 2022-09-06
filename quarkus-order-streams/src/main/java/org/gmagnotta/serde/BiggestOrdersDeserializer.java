package org.gmagnotta.serde;

import java.util.Iterator;
import java.util.List;

import org.apache.kafka.common.serialization.Deserializer;
import org.gmagnotta.model.event.OrderOuterClass.Order;

import com.google.protobuf.InvalidProtocolBufferException;

public class BiggestOrdersDeserializer implements Deserializer<BiggestOrders> {

    private int maxSize;

    public BiggestOrdersDeserializer(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public BiggestOrders deserialize(String topic, byte[] data) {

        BiggestOrders orders = null;
        
        try {
            org.gmagnotta.model.event.OrderOuterClass.BiggestOrders biggest = org.gmagnotta.model.event.OrderOuterClass.BiggestOrders.parseFrom(data);

            orders = new BiggestOrders(maxSize);

            List<Order> orderList = biggest.getOrdersList();

            Iterator<Order> i = orderList.iterator();

            while (i.hasNext()) {
                Order o = i.next();

                orders.add(o);
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

        return orders;
    }
    
}
