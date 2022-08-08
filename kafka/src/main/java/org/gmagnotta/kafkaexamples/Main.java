package org.gmagnotta.kafkaexamples;

import java.util.Properties;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        Properties kafkaProps = new Properties();
		kafkaProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "prova-cajgta-qu--fu--oaf-a.bf2.kafka.rhcloud.com:443");
		kafkaProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
		kafkaProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,  IntegerSerializer.class.getName());
		kafkaProps.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
		kafkaProps.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
		kafkaProps.put(SaslConfigs.SASL_JAAS_CONFIG, "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"srvc-acct-aea9fb23-d3b4-4f5f-b123-86ce7cdb8c1b\" password=\"55f3b951-f64a-4caf-9828-36b73753b364\";");

		Properties kafkaConsumer = new Properties();
		kafkaConsumer.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "prova-cajgta-qu--fu--oaf-a.bf2.kafka.rhcloud.com:443");
		kafkaConsumer.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class.getName());
		kafkaConsumer.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class.getName());
		kafkaConsumer.put(ConsumerConfig.GROUP_ID_CONFIG, "my-application");
		kafkaConsumer.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		kafkaConsumer.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
		kafkaConsumer.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
		kafkaConsumer.put(SaslConfigs.SASL_JAAS_CONFIG, "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"srvc-acct-d08d53d2-edce-4840-a6e5-9af700998ce6\" password=\"ff073215-080b-4e5e-aed1-53c316faed3b\";");


        Producer producer = new Producer(kafkaProps);

        Thread t = new Thread(producer);

        t.start();

		// Consumer consumer = new Consumer(kafkaConsumer);
		// Thread t2 = new Thread(consumer);

		// t2.start();

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

			@Override
			public void run() {
				t.interrupt();
				// t2.interrupt();
				
			}
			
		}
		));

        t.join();
		// t2.join();
    }
}
