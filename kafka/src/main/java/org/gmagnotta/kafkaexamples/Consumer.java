package org.gmagnotta.kafkaexamples;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;

/**
 * Example Kafka Consumer
 */
public class Consumer
{
	public static final Logger LOGGER = LoggerFactory.getLogger(Consumer.class);

	private Properties kafkaProps;

	public Consumer(Properties properties) {
		this.kafkaProps = properties;
	}

    public static void main( String[] args )
    {
        LOGGER.info( "Hello Kafka Consumer!" );

        Properties kafkaProps = new Properties();
		kafkaProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "prova-cajgta-qu--fu--oaf-a.bf2.kafka.rhcloud.com:443");
		kafkaProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class.getName());
		kafkaProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
		kafkaProps.put(ConsumerConfig.GROUP_ID_CONFIG, "my-application");
		kafkaProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

		//kafkaProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
		//kafkaProps.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
		//kafkaProps.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, "ABSOLUTE_PATH_TO_YOUR_WORKSPACE_FOLDER/truststore.jks");
		//kafkaProps.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, "password");
		kafkaProps.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
		kafkaProps.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
		kafkaProps.put(SaslConfigs.SASL_JAAS_CONFIG, "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"srvc-acct-d08d53d2-edce-4840-a6e5-9af700998ce6\" password=\"ff073215-080b-4e5e-aed1-53c316faed3b\";");

		KafkaConsumer<Integer, Integer> consumer = new KafkaConsumer<Integer, Integer>(kafkaProps);

		consumer.subscribe(Collections.singletonList("sensors"));

		try {
			while (!Thread.currentThread().isInterrupted()) {
				ConsumerRecords<Integer, Integer> records = consumer.poll(Duration.ofMillis(1000));
				
				for (ConsumerRecord<Integer, Integer> record : records) {
					LOGGER.info("read: " + record.key() + "; " + record.value());
				}
			}
		} catch (Exception e) {
			// If the producer encountered errors before sending the message to Kafka.
			e.printStackTrace();
			Thread.currentThread().interrupt(); 
		} finally {
			consumer.close();
		}
    }
	
}
