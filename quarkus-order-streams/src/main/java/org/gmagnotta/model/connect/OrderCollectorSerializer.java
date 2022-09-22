package org.gmagnotta.model.connect;

import org.apache.kafka.common.serialization.Serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class OrderCollectorSerializer implements Serializer<OrderCollector> {
    private ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public byte[] serialize(String topic, OrderCollector data) {
        try {
            return objectMapper.writeValueAsBytes(data);
            } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
            }
    }
    
}
