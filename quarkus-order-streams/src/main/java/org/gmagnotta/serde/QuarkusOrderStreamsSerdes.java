package org.gmagnotta.serde;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.gmagnotta.model.event.OrderOuterClass.Order;

import org.gmagnotta.model.BiggestOrders;

public class QuarkusOrderStreamsSerdes {

    static public Serde<Order> Orders() {
        return Serdes.serdeFrom(new OrderSerializer(), new OrderDeserializer());
    }

    static public Serde<BiggestOrders> BiggestOrders(int maxSize) {
        return Serdes.serdeFrom(new BiggestOrdersSerializer(), new BiggestOrdersDeserializer(maxSize));
    }

}
