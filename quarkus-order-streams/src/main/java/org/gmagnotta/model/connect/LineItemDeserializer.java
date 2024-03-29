package org.gmagnotta.model.connect;

import org.apache.kafka.common.serialization.Deserializer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LineItemDeserializer implements Deserializer<LineItem> {

    private ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Override
    public LineItem deserialize(String topic, byte[] data) {
        try {
            return objectMapper.readValue(new String(data, "UTF-8"),
                    LineItem.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

}
