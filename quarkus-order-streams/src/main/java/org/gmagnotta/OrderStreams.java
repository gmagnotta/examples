package org.gmagnotta;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.Topology.AutoOffsetReset;
import org.apache.kafka.streams.kstream.Aggregator;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.Initializer;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.StreamJoined;
import org.apache.kafka.streams.kstream.ValueJoiner;
import org.apache.kafka.streams.kstream.ValueMapper;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.gmagnotta.model.BiggestOrders;
import org.gmagnotta.model.EnrichedLineItem;
import org.gmagnotta.model.connect.EnrichedOrder;
import org.gmagnotta.model.connect.Item;
import org.gmagnotta.model.connect.LineItem;
import org.gmagnotta.model.connect.Order;
import org.gmagnotta.model.connect.OrderCollector;
import org.gmagnotta.serde.QuarkusOrderStreamsSerdes;
import org.jboss.logging.Logger;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.google.protobuf.ByteString;

@ApplicationScoped
public class OrderStreams {

    @Inject
    Logger logger;

    @ConfigProperty(name = "items.topic")
    String itemsTopic;

    @ConfigProperty(name = "lineitems.topic")
    String lineItemsTopic;

    @ConfigProperty(name = "orders.topic")
    String ordersTopic;

    @ConfigProperty(name = "ordercreated.topic")
    String orderCreatedTopic;

    @Produces
    public Topology getTopology() {

        StreamsBuilder builder = new StreamsBuilder();

        // Creates a Global KTable containing all items
        GlobalKTable<String, Item> itemsTable = builder.globalTable(itemsTopic,
             Consumed.with(Serdes.String(), QuarkusOrderStreamsSerdes.Item(), null, AutoOffsetReset.EARLIEST));

        // Creates line items Stream
        KStream<Long, LineItem> lineItemsStream = builder
                .stream(lineItemsTopic,
                        Consumed.with(Serdes.String(), QuarkusOrderStreamsSerdes.LineItem(), null, AutoOffsetReset.EARLIEST))

                // replace the original Key created by debezium with the ord id (Long)
                // making possible to join with orders
                .selectKey(new KeyValueMapper<String, LineItem, Long>() {
                    @Override
                    public Long apply(String key, LineItem value) {
                        return value.getOrd();
                    }
                });

        // lineItemsStream.print(Printed.<Long, LineItem>toSysOut().withLabel("lineItemsStream"));

        // Join line items Stream with Items Table and produce an Enriched Line Item
        KStream<Long, EnrichedLineItem> enrichedLineItems = lineItemsStream.join(itemsTable,
                new KeyValueMapper<Long,LineItem,String>() {

                    @Override
                    public String apply(Long key, LineItem value) {
                        return "{\"id\":" + value.getItem() + "}";
                    }
                    
                },
                new ValueJoiner<org.gmagnotta.model.connect.LineItem, org.gmagnotta.model.connect.Item, EnrichedLineItem>() {
                    @Override
                    public EnrichedLineItem apply(org.gmagnotta.model.connect.LineItem leftValue,
                            org.gmagnotta.model.connect.Item rightValue) {

                        EnrichedLineItem enriched = new EnrichedLineItem();

                        enriched.setItem(rightValue);
                        enriched.setLineItem(leftValue);

                        return enriched;

                    }
                })
                ;

        //enrichedLineItems.print(Printed.<Long, EnrichedLineItem>toSysOut().withLabel("enrichedLineItems"));

        // Build orders Stream
        KStream<Long, Order> ordersStream = builder
                .stream(ordersTopic,
                        Consumed.with(Serdes.String(), QuarkusOrderStreamsSerdes.Order(), null, AutoOffsetReset.EARLIEST))

                // replace the original Key created by debezium with the order id (Long)
                // making possible to join with line items
                .selectKey(new KeyValueMapper<String, Order, Long>() {
                    @Override
                    public Long apply(String key, Order value) {
                        return value.getId();
                    }
                });

        // ordersStream.print(Printed.<Long, Order>toSysOut().withLabel("ordersStream"));

        // Join orders stream and line items to produce Enriched Orders
        KStream<Long, EnrichedOrder> orderCollection = ordersStream.join(enrichedLineItems,
                new ValueJoiner<Order, EnrichedLineItem, EnrichedOrder>() {
                    @Override
                    public EnrichedOrder apply(Order leftValue,
                    EnrichedLineItem rightValue) {
                        EnrichedOrder enrichedOrder = new EnrichedOrder();

                        enrichedOrder.setLineItem(rightValue);
                        enrichedOrder.setOrder(leftValue);

                        return enrichedOrder;
                    }
                },
                JoinWindows.of(Duration.ofMinutes(5)),
                StreamJoined.with(Serdes.Long(), QuarkusOrderStreamsSerdes.Order(), QuarkusOrderStreamsSerdes.EnrichedLineItem()))

                // replace the Key with the order id (Long)
                // making possible to group by key
                .selectKey(new KeyValueMapper<Long, EnrichedOrder, Long>() {
                    @Override
                    public Long apply(Long key, EnrichedOrder value) {
                        return value.getOrder().getId();
                    }
                });

        // orderCollection.print(Printed.<Long, EnrichedOrder>toSysOut().withLabel("orderCollection"));

        // we will obtain a list of Enriched Ordes. We'll group similar
        // elements by key
        KTable<Long, OrderCollector> grouped = orderCollection.groupByKey(Grouped.with(Serdes.Long(), QuarkusOrderStreamsSerdes.EnrichedOrder())).aggregate(
                new Initializer<OrderCollector>() {
                    @Override
                    public OrderCollector apply() {
                        return new OrderCollector();
                    }
                },
                // aggregates all the elements with the same key
                // producing an order collector aggregate element
                new Aggregator<Long, EnrichedOrder, OrderCollector>() {
                    @Override
                    public OrderCollector apply(Long aggKey, EnrichedOrder newValue, OrderCollector aggValue) {
                        aggValue.addOrder(newValue);
                        return aggValue;
                    }
                }, Materialized.with(Serdes.Long(), QuarkusOrderStreamsSerdes.OrderCollector()));
        
        //grouped.toStream().print(Printed.<Long,OrderCollector>toSysOut().withLabel("grouped"));

        // transforms each order collector element in the final encoded protobuf Order class
        KStream<String, org.gmagnotta.model.event.OrderOuterClass.Order> protoOrders = grouped.mapValues(
                new ValueMapper<OrderCollector, org.gmagnotta.model.event.OrderOuterClass.Order>() {
                    @Override
                    public org.gmagnotta.model.event.OrderOuterClass.Order apply(OrderCollector s) {

                        return convertToProtobuf(s.getOrders());

                    }
                }, Materialized.with(Serdes.Long(), QuarkusOrderStreamsSerdes.Orders()))

                // transform the obtained KTable to a Stream replacing the Long key with the
                // a String as expeted by other microservices
                .toStream().selectKey(new KeyValueMapper<Long, org.gmagnotta.model.event.OrderOuterClass.Order, String>() {

                    @Override
                    public String apply(Long key, org.gmagnotta.model.event.OrderOuterClass.Order value) {
                        return String.valueOf(value.getId());
                    }

                });

        protoOrders.print(Printed.<String,org.gmagnotta.model.event.OrderOuterClass.Order>toSysOut().withLabel("protoOrders"));
        
        
        // we'll materialize the Stream to the topic "outbox.event.OrderCreated"
        // so other clients can process the Aggregate
        protoOrders.to(orderCreatedTopic, Produced.with(Serdes.String(), QuarkusOrderStreamsSerdes.Orders()));

        
        // Extract items id and quantities from line_items
        KStream<Integer, Integer> topItemsStream = lineItemsStream.flatMap(
            (key, value) -> {
                List<KeyValue<Integer, Integer>> result = new LinkedList<>();
            
                result.add(KeyValue.pair((int) value.getItem(), (int) value.getQuantity()));
            
                return result;
        }) 
          
        // Aggregate elements by item id and sum all the quantities
        .groupByKey(Grouped.with(Serdes.Integer(), Serdes.Integer())).aggregate(
            () -> Integer.valueOf(0),
            (key, value, aggValue) -> Integer.sum(aggValue, value),
            Materialized.<Integer, Integer, KeyValueStore<Bytes, byte[]>>as("itemsQuantity")
                .withKeySerde(Serdes.Integer())
                .withValueSerde(Serdes.Integer())).toStream();
        
        topItemsStream.print(Printed.<Integer,Integer>toSysOut().withLabel("topItems"));

        // write to "topItems" topic
        topItemsStream.to("topItems", Produced.with(Serdes.Integer(), Serdes.Integer()));
        
        
        // Find biggest Orders
        String orderStateStoreName = "orderStateStore";
        KeyValueBytesStoreSupplier orderSupplier = Stores.persistentKeyValueStore(orderStateStoreName);
        StoreBuilder<KeyValueStore<String, BiggestOrders>> storeBuilder = Stores.keyValueStoreBuilder(orderSupplier,
            Serdes.String(), QuarkusOrderStreamsSerdes.BiggestOrders(10));
          
        builder.addStateStore(storeBuilder);
        
        // Keep biggest orders from the stream
        KStream<Long, BiggestOrders> topOrdersStream = ordersStream.transformValues(
            () -> new BiggestOrderTransformer(orderStateStoreName, 10),
            orderStateStoreName);
        
        topOrdersStream.print(Printed.<Long,BiggestOrders>toSysOut().withLabel("topOrders"));
        
        // write biggest orders to "topOrders" topic
        topOrdersStream.to("topOrders", Produced.with(Serdes.Long(), QuarkusOrderStreamsSerdes.BiggestOrders(10)));
        

        Topology topology = builder.build();
        logger.info("Topology is " + topology.describe());

        return topology;
    }

    private static BigDecimal bigDecimalFromString(String encodedString) {

        int scale = 2;
        return new BigDecimal(new BigInteger(Base64.getDecoder().decode(encodedString)), scale);

    }

    public static org.gmagnotta.model.event.OrderOuterClass.BigDecimal convertToProtobuf(BigDecimal bigDecimal) {

        return org.gmagnotta.model.event.OrderOuterClass.BigDecimal.newBuilder()
                .setScale(bigDecimal.scale())
                .setPrecision(bigDecimal.precision())
                .setValue(ByteString.copyFrom(bigDecimal.unscaledValue().toByteArray()))
                .build();

    }

    public static org.gmagnotta.model.event.OrderOuterClass.Item convertToProtobuf(
            org.gmagnotta.model.connect.Item item) {

        return org.gmagnotta.model.event.OrderOuterClass.Item.newBuilder()
                .setId((int) item.getId())
                .setDescription(item.getDescription())
                .setPrice(convertToProtobuf(bigDecimalFromString(item.getPrice())))
                .build();
    }

    public static org.gmagnotta.model.event.OrderOuterClass.LineItem convertToProtobuf(EnrichedLineItem lineItem) {

        return org.gmagnotta.model.event.OrderOuterClass.LineItem.newBuilder()
                .setId((int) lineItem.getLineItem().getId())
                .setPrice(convertToProtobuf(bigDecimalFromString(lineItem.getLineItem().getPrice())))
                .setQuantity((int) lineItem.getLineItem().getQuantity())
                .setItem(convertToProtobuf(lineItem.getItem()))
                .build();

    }

    public static org.gmagnotta.model.event.OrderOuterClass.Order convertToProtobuf(
            List<EnrichedOrder> enrichedOrders) {

        Iterator<EnrichedOrder> iterator = enrichedOrders.iterator();

        List<org.gmagnotta.model.event.OrderOuterClass.LineItem> pLineItems = new ArrayList<org.gmagnotta.model.event.OrderOuterClass.LineItem>();

        while (iterator.hasNext()) {

            EnrichedOrder enrichedOrder = iterator.next();

            pLineItems.add(convertToProtobuf(enrichedOrder.getLineItem()));

        }

        return org.gmagnotta.model.event.OrderOuterClass.Order.newBuilder()
                .setId((int) enrichedOrders.get(0).getOrder().getId())
                .setCreationDate(enrichedOrders.get(0).getOrder().getCreation_date())
                .setAmount(convertToProtobuf(bigDecimalFromString(enrichedOrders.get(0).getOrder().getAmount())))
                .addAllLineItems(pLineItems)
                .setUser(enrichedOrders.get(0).getOrder().getUsername())
                .build();
    }
}
