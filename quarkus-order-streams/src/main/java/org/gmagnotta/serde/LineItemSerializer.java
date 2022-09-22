package org.gmagnotta.serde;

import org.apache.kafka.common.serialization.Serializer;
import org.gmagnotta.model.event.OrderOuterClass.LineItem;

public class LineItemSerializer implements Serializer<LineItem> {

    @Override
    public byte[] serialize(String topic, LineItem data) {

        return data.toByteArray();
    }
    
}
