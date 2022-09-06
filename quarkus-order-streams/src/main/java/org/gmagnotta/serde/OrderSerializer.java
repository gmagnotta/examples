package org.gmagnotta.serde;

import org.apache.kafka.common.serialization.Serializer;
import org.gmagnotta.model.event.OrderOuterClass.Order;

public class OrderSerializer implements Serializer<Order> {

    @Override
    public byte[] serialize(String topic, Order data) {

        return data.toByteArray();
    }
    
}
