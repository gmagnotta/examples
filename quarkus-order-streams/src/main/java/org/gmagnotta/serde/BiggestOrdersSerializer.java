package org.gmagnotta.serde;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.kafka.common.serialization.Serializer;
import org.gmagnotta.ProtoUtils;
import org.gmagnotta.model.BiggestOrders;
import org.gmagnotta.model.connect.Order;

public class BiggestOrdersSerializer implements Serializer<BiggestOrders> {

    @Override
    public byte[] serialize(String topic, BiggestOrders orders) {

        Iterator<Order> iterator = orders.iterator();

        List<org.gmagnotta.model.event.OrderOuterClass.BiggestOrder> listOrders = new ArrayList<>();

        while (iterator.hasNext()) {
            Order o = iterator.next();

            org.gmagnotta.model.event.OrderOuterClass.BiggestOrder biggestOrder = org.gmagnotta.model.event.OrderOuterClass.BiggestOrder.newBuilder()
                .setId((int) o.getId())
                .setAmount(o.getAmount())
                .build();

            listOrders.add(biggestOrder);
        }

        org.gmagnotta.model.event.OrderOuterClass.BiggestOrders biggest = org.gmagnotta.model.event.OrderOuterClass.BiggestOrders.newBuilder()
            .addAllOrders(listOrders)
            .build();

        return biggest.toByteArray();
    }
    
}
