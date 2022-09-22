package org.gmagnotta;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Aggregator;
import org.apache.kafka.streams.kstream.Consumed;
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
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.gmagnotta.model.BiggestOrders;
import org.gmagnotta.model.EnrichedLineItem;
import org.gmagnotta.model.connect.EnrichedOrder;
import org.gmagnotta.model.connect.Item;
import org.gmagnotta.model.connect.LineItem;
import org.gmagnotta.model.connect.Order;
import org.gmagnotta.model.connect.OrderCollector;
import org.gmagnotta.serde.QuarkusOrderStreamsSerdes;
import org.jboss.logging.Logger;

import com.google.protobuf.ByteString;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
public class OrderStreams {

    @ConfigProperty(name = "kafka.broker")
    String kafkaBroker;

    @Inject
    Logger logger;

    @Produces
    KafkaStreams streams;

    @PostConstruct
    void init() {

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "quarkus-order-streams");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBroker);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.Integer().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.Integer().getClass().getName());

        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        StreamsBuilder builder = new StreamsBuilder();

        // Build item Stream
        KStream<String, Item> itemsStream = builder
                .stream("dbserver1.public.items",
                        Consumed.with(Serdes.String(), QuarkusOrderStreamsSerdes.Item()));

        // itemsStream.print(Printed.<String,org.gmagnotta.model.connect.Item>toSysOut().withLabel("original"));

        KStream<Long, Item> newItemsStream = itemsStream
                .selectKey(new KeyValueMapper<String, Item, Long>() {
                    @Override
                    public Long apply(String key, Item value) {
                        return Long.valueOf(value.getId());
                    }
                });

        // newItemsStream.print(Printed.<Long,org.gmagnotta.model.connect.Item>toSysOut().withLabel("items"));

        KStream<String, LineItem> lineItemsStream = builder
                .stream("dbserver1.public.line_items",
                        Consumed.with(Serdes.String(), QuarkusOrderStreamsSerdes.LineItem()));

        // lineItemsStream.print(Printed.<String,org.gmagnotta.model.connect.LineItem>toSysOut().withLabel("original"));

        KStream<Long, LineItem> newLineItemsStream = lineItemsStream
                .selectKey(new KeyValueMapper<String, LineItem, Long>() {
                    @Override
                    public Long apply(String key, LineItem value) {
                        return Long.valueOf(value.getItem());
                    }
                });

        // newLineItemsStream.print(Printed.<Long,org.gmagnotta.model.connect.LineItem>toSysOut().withLabel("new"));

        // Join order and line item
        KStream<Long, EnrichedLineItem> lineItemWithItems = newLineItemsStream.join(newItemsStream,
                new ValueJoiner<org.gmagnotta.model.connect.LineItem, org.gmagnotta.model.connect.Item, EnrichedLineItem>() {
                    @Override
                    public EnrichedLineItem apply(org.gmagnotta.model.connect.LineItem leftValue,
                            org.gmagnotta.model.connect.Item rightValue) {

                        EnrichedLineItem enrichedLineItem = new EnrichedLineItem();

                        enrichedLineItem.setItem(rightValue);
                        enrichedLineItem.setLineItem(leftValue);

                        return enrichedLineItem;

                    }
                },
                JoinWindows.of(Duration.ofMinutes(5)),
                StreamJoined.with(
                        Serdes.Long(),
                        QuarkusOrderStreamsSerdes.LineItem(),
                        QuarkusOrderStreamsSerdes.Item()));

        // lineItemWithItems.print(Printed.<Long,EnrichedLineItem>toSysOut().withLabel("Joined2"));

        KStream<Long, EnrichedLineItem> newlineItemWithItems = lineItemWithItems
                .selectKey(new KeyValueMapper<Long, EnrichedLineItem, Long>() {
                    @Override
                    public Long apply(Long key, EnrichedLineItem value) {
                        return value.getLineItem().getOrd();
                    }
                });

        // Build order Stream
        KStream<String, Order> ordersStream = builder
                .stream("dbserver1.public.orders",
                        Consumed.with(Serdes.String(), QuarkusOrderStreamsSerdes.Order()));

        // ordersStream.print(Printed.<String,org.gmagnotta.model.connect.Order>toSysOut().withLabel("original"));

        KStream<Long, Order> newOrderStream = ordersStream
                .selectKey(new KeyValueMapper<String, Order, Long>() {
                    @Override
                    public Long apply(String key, Order value) {
                        return Long.valueOf(value.getId());
                    }
                });

        // newOrderStream.print(Printed.<Long,org.gmagnotta.model.connect.Order>toSysOut().withLabel("new"));

        // Join order and line item
        KStream<Long, EnrichedOrder> joinedStream = newOrderStream.join(newlineItemWithItems,
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
                JoinWindows.of(Duration.ofDays(1000)),
                StreamJoined.with(Serdes.Long(), QuarkusOrderStreamsSerdes.Order(), QuarkusOrderStreamsSerdes.EnrichedLineItem()));

        // joinedStream.print(Printed.<Long,EnrichedOrder>toSysOut().withLabel("Joined"));

        KTable<Long, OrderCollector> aggregated = joinedStream
                .groupByKey(Grouped.with(Serdes.Long(), QuarkusOrderStreamsSerdes.EnrichedOrder())).aggregate(
                        new Initializer<OrderCollector>() {
                            @Override
                            public OrderCollector apply() {
                                return new OrderCollector();
                            }
                        },
                        new Aggregator<Long, EnrichedOrder, OrderCollector>() {
                            @Override
                            public OrderCollector apply(Long aggKey, EnrichedOrder newValue, OrderCollector aggValue) {
                                aggValue.addOrder(newValue);
                                return aggValue;
                            }
                        }, Materialized.with(Serdes.Long(), QuarkusOrderStreamsSerdes.OrderCollector()));

        // KStream<Long, OrderCollector> testStream = builder.stream("test",
        // Consumed.with(Serdes.Long(), orderCollectorSerde));

        // aggregated.toStream().print(Printed.<Long,OrderCollector>toSysOut().withLabel("Joined"));

        KTable<Long, org.gmagnotta.model.event.OrderOuterClass.Order> protoOrders = aggregated.mapValues(
                new ValueMapper<OrderCollector, org.gmagnotta.model.event.OrderOuterClass.Order>() {
                    @Override
                    public org.gmagnotta.model.event.OrderOuterClass.Order apply(OrderCollector s) {

                        return convertToProtobuf(s.getOrders());

                    }
                }, Materialized.with(Serdes.Long(), QuarkusOrderStreamsSerdes.Orders()));

        
        //protoOrders.toStream().print(Printed.<Long, Order>toSysOut().withLabel("orders"));

        KStream<String, org.gmagnotta.model.event.OrderOuterClass.Order> newProtoOrders = protoOrders.toStream().selectKey(new KeyValueMapper<Long, org.gmagnotta.model.event.OrderOuterClass.Order, String>() {

            @Override
            public String apply(Long key, org.gmagnotta.model.event.OrderOuterClass.Order value) {
                return String.valueOf(value.getId());
            }

        });

        newProtoOrders.to("outbox.event.OrderCreated", Produced.with(Serdes.String(), QuarkusOrderStreamsSerdes.Orders()));

        
        // Source stream from OrderCreated topics
        // KStream<String, Order> ordersStream2 = builder
        //   .stream("outbox.event.OrderCreated",
        //             Consumed.with(Serdes.String(), QuarkusOrderStreamsSerdes.Orders()));
          
        // ordersStream2.print(Printed.<String,Order>toSysOut().withLabel("orders"));
          
          
        // Extract items and quantities from line_items
        KStream<Integer, Integer> itemsQtyStream = newProtoOrders.flatMap(
            (key, value) -> {
                List<KeyValue<Integer, Integer>> result = new LinkedList<>();
            
                List<org.gmagnotta.model.event.OrderOuterClass.LineItem> items = value.getLineItemsList();
            
                for (org.gmagnotta.model.event.OrderOuterClass.LineItem l : items) {
                    result.add(KeyValue.pair(l.getItem().getId(), l.getQuantity()));
                }
            
                return result;
            });
          
        //itemsQtyStream.print(Printed.<Integer,Integer>toSysOut().withLabel("items"));
        
          // itemsStream.to("items", Produced.with(Serdes.Integer(),
          //Serdes.Integer()));
          
          // Aggregated itemsQty by item key and sum all the items
        KTable<Integer, Integer> groupedTable = itemsQtyStream.groupByKey().aggregate(
        () -> Integer.valueOf(0),
        (key, value, aggValue) -> Integer.sum(aggValue, value),
        Materialized.<Integer, Integer, KeyValueStore<Bytes, byte[]>>as("itemsQuantity")
            .withKeySerde(Serdes.Integer())
            .withValueSerde(Serdes.Integer()));
        
        groupedTable.toStream().to("topItems", Produced.with(Serdes.Integer(), Serdes.Integer()));
        
          // Find biggest Orders
        String orderStateStoreName = "orderStateStore";
        KeyValueBytesStoreSupplier orderSupplier = Stores.persistentKeyValueStore(orderStateStoreName);
        StoreBuilder<KeyValueStore<String, BiggestOrders>> storeBuilder = Stores.keyValueStoreBuilder(orderSupplier,
        Serdes.String(), QuarkusOrderStreamsSerdes.BiggestOrders(10));
          
        builder.addStateStore(storeBuilder);
        
        KStream<String, BiggestOrders> statefulBiggestOrder = newProtoOrders
        .transformValues(() -> new BiggestOrderTransformer(orderStateStoreName, 10), orderStateStoreName);
        // statefulBiggestOrder.print(Printed.<String,
        // BiggestOrders>toSysOut().withLabel("biggest"));
        
        statefulBiggestOrder.to("topOrders",
        Produced.with(Serdes.String(), QuarkusOrderStreamsSerdes.BiggestOrders(10)));
          


        Topology topology = builder.build();
        logger.info("Topology is " + topology.describe());

        streams = new KafkaStreams(topology, props);

    }

    void onStart(@Observes StartupEvent ev) {

        streams.start();

        logger.info("Consumer started");

    }

    void onStop(@Observes ShutdownEvent ev) {

        streams.close();

        logger.info("Consumer stopped");

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
