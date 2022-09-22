package org.gmagnotta.serde;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.gmagnotta.model.event.OrderOuterClass.LineItem;
import org.gmagnotta.model.event.OrderOuterClass.Order;

import org.gmagnotta.model.BiggestOrders;
import org.gmagnotta.model.EnrichedLineItem;
import org.gmagnotta.model.connect.EnrichedOrder;
import org.gmagnotta.model.connect.OrderCollectorDeserializer;
import org.gmagnotta.model.connect.OrderCollectorSerializer;
import org.gmagnotta.model.connect.OrderCollector;

public class QuarkusOrderStreamsSerdes {

    static public Serde<Order> Orders() {
        return Serdes.serdeFrom(new OrderSerializer(), new OrderDeserializer());
    }

    static public Serde<BiggestOrders> BiggestOrders(int maxSize) {
        return Serdes.serdeFrom(new BiggestOrdersSerializer(), new BiggestOrdersDeserializer(maxSize));
    }

    static public Serde<LineItem> LineItems() {
        return Serdes.serdeFrom(new LineItemSerializer(), new LineItemDeserializer());
    }

    static public Serde<org.gmagnotta.model.connect.Item> Item() {
        return Serdes.serdeFrom(
                new org.gmagnotta.model.connect.ItemSerializer(), new org.gmagnotta.model.connect.ItemDeserializer());
    }

    static public Serde<org.gmagnotta.model.connect.LineItem> LineItem() {
        return Serdes.serdeFrom(
            new org.gmagnotta.model.connect.LineItemSerializer(),
            new org.gmagnotta.model.connect.LineItemDeserializer());
    }

    static public Serde<org.gmagnotta.model.connect.Order> Order() {
        return Serdes.serdeFrom(
            new org.gmagnotta.model.connect.OrderSerializer(), new org.gmagnotta.model.connect.OrderDeserializer());
    }

    static public Serde<EnrichedLineItem> EnrichedLineItem() {
        return Serdes.serdeFrom(
            new org.gmagnotta.model.connect.EnrichedLineItemSerializer(),
            new org.gmagnotta.model.connect.EnrichedLineItemDeserializer());

    }

    static public Serde<EnrichedOrder> EnrichedOrder() {
        return Serdes.serdeFrom(
            new org.gmagnotta.model.connect.EnrichedOrderSerialized(),
            new org.gmagnotta.model.connect.EnrichedOrderDeserializer());
    }

    static public Serde<OrderCollector> OrderCollector() {
        return Serdes.serdeFrom(new OrderCollectorSerializer(),
        new OrderCollectorDeserializer());
    }

}
