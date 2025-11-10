package de.thi.inf.cnd;

import java.net.InetAddress;
import java.time.Duration;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExampleConsumer {
	private static Logger logger = LoggerFactory.getLogger(ExampleProducer.class);
	private static boolean running;
	
	public static void main(String[] args) throws Exception {
		final String topic = "test-topic";

		Properties config = new Properties();
		config.put("client.id", InetAddress.getLocalHost().getHostName());
		config.put("bootstrap.servers", "localhost:9092");
		config.put("group.id", "test-consumer-group-" + System.currentTimeMillis()); // Unique group each time
		config.put("auto.offset.reset", "earliest"); // Start from beginning if no offset exists

		// Information notwendig, wie Key und Value in byte[] überführt werden
		config.put("key.deserializer", StringDeserializer.class.getName());
		config.put("value.deserializer", StringDeserializer.class.getName());
		

		// Erzeugt Concumer-API
		try(Consumer<String, String> consumer = new KafkaConsumer<>(config)) {
			consumer.subscribe(List.of(topic));
			logger.info("Consumer subscribed to topic: " + topic);
			logger.info("Waiting for messages... (Press Enter to stop)");
			running = true;
			new Thread(() -> {
				logger.info("Consumer thread started, polling for messages...");
				while(running) {
					ConsumerRecords<String, String> poll = consumer.poll(Duration.ofSeconds(1));
					if (!poll.isEmpty()) {
						logger.info("Received " + poll.count() + " message(s)");
						poll.forEach(el -> {
							logger.info(el.key() + ": " + el.value() + " (" + el.topic() + ")");
						});
						consumer.commitSync();
					}
				}
				logger.info("Consumer thread stopped");
			}).start();
			System.in.read();
			running = false;
		}
	}
}