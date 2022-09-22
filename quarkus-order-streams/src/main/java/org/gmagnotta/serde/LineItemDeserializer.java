package org.gmagnotta.serde;

import org.apache.kafka.common.serialization.Deserializer;
import org.gmagnotta.model.event.OrderOuterClass.LineItem;
import org.gmagnotta.model.event.OrderOuterClass.Order;

import com.google.protobuf.InvalidProtocolBufferException;

public class LineItemDeserializer implements Deserializer<LineItem> {

    @Override
    public LineItem deserialize(String topic, byte[] data) {

        org.gmagnotta.model.event.OrderOuterClass.LineItem lineItem = null;
        
        try {
            lineItem = org.gmagnotta.model.event.OrderOuterClass.LineItem.parseFrom(data);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

        return lineItem;
    }
    
}
