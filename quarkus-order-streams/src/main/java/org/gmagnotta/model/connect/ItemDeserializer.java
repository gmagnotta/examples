package org.gmagnotta.model.connect;

import org.apache.kafka.common.serialization.Deserializer;

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

    public static void main(String[] args) {
        String s = "{\"id\":0,\"description\":\"Bacon King\",\"price\":\"ZA==\", \"__op\":\"r\"}";

        ItemDeserializer i = new ItemDeserializer();

        Item item = i.deserialize("test", s.getBytes());

        System.out.println(item);

        ItemSerializer serializer = new ItemSerializer();

        byte[] serialized = serializer.serialize("test", item);

        System.out.println(new String(serialized));

        item = i.deserialize("test", serialized);

        System.out.println(item);
    }
    
}
