package org.gmagnotta.kafkaexamples;

import java.util.Properties;
import java.util.Random;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hello world!
 */
public class Producer implements Runnable
{

	private static final Logger LOGGER = LoggerFactory.getLogger(Producer.class);

	private final Random RAND = new Random();

	private Properties kafkaProps;

	public Producer(Properties properties) {
		kafkaProps = properties;
	}
    public static void main( String[] args )
    {
        LOGGER.info( "Hello World!" );

        Properties kafkaProps = new Properties();
		kafkaProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "peppe-cahlmg-qu--fu--mv-bg.bf2.kafka.rhcloud.com:443");
		kafkaProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		kafkaProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,  StringSerializer.class.getName());
		//kafkaProps.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
		//kafkaProps.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, "ABSOLUTE_PATH_TO_YOUR_WORKSPACE_FOLDER/truststore.jks");
		//kafkaProps.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, "password");
		kafkaProps.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
		kafkaProps.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
		kafkaProps.put(SaslConfigs.SASL_JAAS_CONFIG, "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"srvc-acct-aea9fb23-d3b4-4f5f-b123-86ce7cdb8c1b\" password=\"55f3b951-f64a-4caf-9828-36b73753b364\";");

		try {
			while (!Thread.currentThread().isInterrupted()) {

				KafkaProducer<String, String> producer = new KafkaProducer<String, String>(kafkaProps);

				ProducerRecord<String, String> record = new ProducerRecord<String, String>("first_topic", "hello world!");

				try {
					producer.send(record);
				} catch (Exception e) {
					// If the producer encountered errors before sending the message to Kafka.
					LOGGER.error("Exception", e);
				} finally {
					producer.flush();
					producer.close();
				}

				Thread.sleep(1000);
			}
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt(); 
		}
    }

	@Override
	public void run() {

		KafkaProducer<Integer, Integer> producer = new KafkaProducer<Integer, Integer>(kafkaProps);

		try {
			while (!Thread.currentThread().isInterrupted()) {

				int id = RAND.nextInt(10);

				ProducerRecord<Integer, Integer> record = new ProducerRecord<Integer, Integer>("sensors", id, RAND.nextInt(100));

				try {
					LOGGER.info("Sending message");
					producer.send(record);
				} catch (Exception e) {
					// If the producer encountered errors before sending the message to Kafka.
					LOGGER.error("Exception", e);
				} 

				Thread.sleep(200);
			}
		} catch (InterruptedException ex) {
			LOGGER.error("Exception", ex);
			Thread.currentThread().interrupt(); 
		} finally {
			producer.flush();
			producer.close();
		}
	}
}
