package org.gmagnotta;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.gmagnotta.model.BiggestOrders;
import org.gmagnotta.model.event.OrderOuterClass.LineItem;
import org.gmagnotta.model.event.OrderOuterClass.Order;
import org.gmagnotta.serde.BiggestOrdersDeserializer;
import org.gmagnotta.serde.BiggestOrdersSerializer;
import org.gmagnotta.serde.QuarkusOrderStreamsSerdes;
import org.jboss.logging.Logger;

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

        // Source stream from OrderCreated topics
        KStream<String, Order> ordersStream = builder
			.stream("outbox.event.OrderCreated", Consumed.with(Serdes.String(), QuarkusOrderStreamsSerdes.Orders()));
        
        //ordersStream.print(Printed.<String,Order>toSysOut().withLabel("orders"));
          
        // Extract items and quantities from line_items
        KStream<Integer, Integer> itemsQtyStream = ordersStream.flatMap(
            (key, value) ->  {
                List<KeyValue<Integer, Integer>> result = new LinkedList<>();

                List<LineItem> items = value.getLineItemsList();

                for (LineItem l : items) {
                    result.add(KeyValue.pair(l.getItem().getId(), l.getQuantity()));
                }

                return result;
            }
        );

        //itemsStream.print(Printed.<Integer,Integer>toSysOut().withLabel("items"));
        
        //itemsStream.to("items", Produced.with(Serdes.Integer(), Serdes.Integer()));

        // Aggregated itemsQty by item key and sum all the items
        KTable<Integer, Integer> groupedTable = itemsQtyStream.groupByKey().aggregate(
             () -> Integer.valueOf(0),
             (key, value, aggValue) -> Integer.sum(aggValue, value),
             Materialized.<Integer, Integer, KeyValueStore<Bytes, byte[]>>as("itemsQuantity")
             .withKeySerde(Serdes.Integer())
             .withValueSerde(Serdes.Integer())
        );

        // Find biggest Orders
        String orderStateStoreName = "orderStateStore";
        KeyValueBytesStoreSupplier orderSupplier = Stores.persistentKeyValueStore(orderStateStoreName);
        StoreBuilder<KeyValueStore<String, BiggestOrders>> storeBuilder = Stores.keyValueStoreBuilder(orderSupplier, Serdes.String(), QuarkusOrderStreamsSerdes.BiggestOrders(10));
        
        builder.addStateStore(storeBuilder);

        KStream<String, BiggestOrders> statefulBiggestOrder = ordersStream.transformValues(() -> new BiggestOrderTransformer(orderStateStoreName, 10), orderStateStoreName);
        //statefulBiggestOrder.print(Printed.<String, BiggestOrders>toSysOut().withLabel("biggest"));

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
}
