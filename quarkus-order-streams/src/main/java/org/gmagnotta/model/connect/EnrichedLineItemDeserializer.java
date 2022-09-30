package org.gmagnotta.model.connect;

import org.apache.kafka.common.serialization.Deserializer;
import org.gmagnotta.model.EnrichedLineItem;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EnrichedLineItemDeserializer implements Deserializer<EnrichedLineItem> {

    private ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Override
    public EnrichedLineItem deserialize(String topic, byte[] data) {
        try {
            return objectMapper.readValue(new String(data, "UTF-8"),
                EnrichedLineItem.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
}
