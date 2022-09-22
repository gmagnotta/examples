package org.gmagnotta.model.connect;

import org.apache.kafka.common.serialization.Serializer;
import org.gmagnotta.model.EnrichedLineItem;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EnrichedLineItemSerializer implements Serializer<EnrichedLineItem> {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(String topic, EnrichedLineItem data) {
        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
    
}
