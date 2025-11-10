package de.thi.inf.cnd;
import java.net.InetAddress;
import java.util.Properties;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExampleProducer {
	private static Logger logger = LoggerFactory.getLogger(ExampleProducer.class);
	public static void main(String[] args) throws Exception {
		final String topic = "test-topic";
		
		Properties config = new Properties();
		config.put("client.id", InetAddress.getLocalHost().getHostName());
		config.put("bootstrap.servers", "localhost:9092");
		config.put("acks", "all");
		
		// Information notwendig, wie Key und Value in byte[] überführt werden
		config.put("key.serializer", StringSerializer.class.getName());
		config.put("value.serializer", StringSerializer.class.getName());


		// Erzeugt Producer-API
		// Try-Catch-Ressource: Instanz wird nur im try-Kontext erzeugt, und abschließend automatisch
		// geschlossen.
		try(Producer<String, String> producer = new KafkaProducer<>(config)) {
			while(true) {
				ProducerRecord<String, String> test = new ProducerRecord<>(topic, "test", "Hello, World!");
				Future<RecordMetadata> send = producer.send(test);// ProducerRecord
				RecordMetadata recordMetadata = send.get();
				logger.info(recordMetadata.toString());
				Thread.sleep(1000);
			}
		}
	}
}