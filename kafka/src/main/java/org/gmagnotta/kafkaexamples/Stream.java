package org.gmagnotta.kafkaexamples;

import java.util.Properties;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serdes.IntegerSerde;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.kafka.streams.StreamsBuilder;
import java.util.Arrays;
import java.util.regex.Pattern;

public class Stream {
    private static final Logger LOGGER = LoggerFactory.getLogger(Stream.class);

    public static void main(String[] args) throws InterruptedException {


        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "my-application");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "prova-cajgta-qu--fu--oaf-a.bf2.kafka.rhcloud.com:443");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.Integer().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.Integer().getClass().getName());
		props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
		props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
		props.put(SaslConfigs.SASL_JAAS_CONFIG, "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"srvc-acct-d08d53d2-edce-4840-a6e5-9af700998ce6\" password=\"ff073215-080b-4e5e-aed1-53c316faed3b\";");

        // setting offset reset to earliest so that we can re-run the demo code with the same pre-loaded data
        // Note: To re-run the demo, you need to use the offset reset tool:
        // https://cwiki.apache.org/confluence/display/KAFKA/Kafka+Streams+Application+Reset+Tool
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // work-around for an issue around timing of creating internal topics
        // Fixed in Kafka 0.10.2.0
        // don't use in large production apps - this increases network load
        // props.put(CommonClientConfigs.METADATA_MAX_AGE_CONFIG, 500);

        Serde<Integer> intSerde = Serdes.Integer();

        StreamsBuilder builder = new StreamsBuilder();

        builder
			.stream("sensors", Consumed.with(intSerde, intSerde))
			//.foreach((key, value) -> LOGGER.info("Received " + key + " " + value))
            .groupByKey()
            .count()
            .toStream()
            .to("output", Produced.with(intSerde, Serdes.Long()));

        // KStream counts  = source.flatMapValues(value-> Arrays.asList(pattern.split(value.toLowerCase())))
        //         .map((key, value) -> new KeyValue<Object, Object>(value, value))
        //         .filter((key, value) -> (!value.equals("the")))
        //         .groupByKey()
        //         .count().mapValues(value->Long.toString(value)).toStream();
        // counts.to("wordcount-output");

        Topology topology = builder.build();
        KafkaStreams streams = new KafkaStreams(topology, props);

        LOGGER.info("Topology is " + topology.describe());

        // This is for reset to work. Don't use in production - it causes the app to re-load the state from Kafka on every start
        streams.cleanUp();

        streams.start();

        // usually the stream application would be running forever,
        // in this example we just let it run for some time and stop since the input data is finite.
        //Thread.sleep(5000L);

        //streams.close();
    }
}
