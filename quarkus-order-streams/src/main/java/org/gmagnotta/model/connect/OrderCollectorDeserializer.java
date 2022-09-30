package org.gmagnotta.model.connect;

import org.apache.kafka.common.serialization.Deserializer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class OrderCollectorDeserializer implements Deserializer<OrderCollector> {
    private ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Override
    public OrderCollector deserialize(String topic, byte[] data) {
        try {
            return objectMapper.readValue(new String(data, "UTF-8"),
                    OrderCollector.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
