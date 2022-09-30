package org.gmagnotta.deserializer;

import org.apache.kafka.common.serialization.Deserializer;
import org.gmagnotta.model.Item;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ItemDeserializer implements Deserializer<Item> {

    private ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Override
    public Item deserialize(String topic, byte[] data) {
        try {
            return objectMapper.readValue(new String(data, "UTF-8"),
            Item.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        
    }

}
