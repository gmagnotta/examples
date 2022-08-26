package org.gmagnotta.deserializer;

import org.apache.kafka.common.serialization.Deserializer;
import org.gmagnotta.model.event.OrderOuterClass.Order;

import com.google.protobuf.InvalidProtocolBufferException;

public class OrderDeserializer implements Deserializer<Order> {

    @Override
    public Order deserialize(String topic, byte[] data) {

        org.gmagnotta.model.event.OrderOuterClass.Order order = null;
        
        try {
            order = org.gmagnotta.model.event.OrderOuterClass.Order.parseFrom(data);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

        return order;
    }
    
}