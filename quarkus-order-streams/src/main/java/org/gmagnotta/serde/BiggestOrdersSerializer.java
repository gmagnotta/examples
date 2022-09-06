package org.gmagnotta.serde;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.kafka.common.serialization.Serializer;
import org.gmagnotta.model.event.OrderOuterClass.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BiggestOrdersSerializer implements Serializer<BiggestOrders> {

    @Override
    public byte[] serialize(String topic, BiggestOrders orders) {

        Iterator<Order> iterator = orders.iterator();

        List<Order> listOrders = new ArrayList<>();

        while (iterator.hasNext()) {
            Order o = iterator.next();
            listOrders.add(o);
        }

        org.gmagnotta.model.event.OrderOuterClass.BiggestOrders biggest = org.gmagnotta.model.event.OrderOuterClass.BiggestOrders.newBuilder()
            .addAllOrders(listOrders)
            .build();

        return biggest.toByteArray();
    }
    
}
