package org.gmagnotta.model.connect;

import org.apache.kafka.common.serialization.Deserializer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EnrichedOrderDeserializer implements Deserializer<EnrichedOrder> {
    private ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Override
    public EnrichedOrder deserialize(String topic, byte[] data) {
        try {
            return objectMapper.readValue(new String(data, "UTF-8"),
            EnrichedOrder.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
