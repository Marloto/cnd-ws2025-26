package de.thi.inf.cnd.rest.adapter.outgoing.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.thi.inf.cnd.rest.application.ports.PostPublisher;
import de.thi.inf.cnd.rest.domain.model.Post;
import de.thi.inf.cnd.rest.domain.model.PostInfo;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class MqttPostPublisher implements PostPublisher {
    private static final Logger logger = LoggerFactory.getLogger(MqttPostPublisher.class);

    @Value("${mqtt.broker}")
    private String broker;

    @Value("${mqtt.client}")
    private String clientId;

    @Value("${mqtt.topic}")
    private String topic;

    private MqttClient client;
    private final ObjectMapper objectMapper;

    public MqttPostPublisher(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    void connect() {
        try {
            MemoryPersistence persistence = new MemoryPersistence();
            this.client = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            logger.info("MQTT: Connecting to broker: {}", broker);
            client.connect(connOpts);
            logger.info("MQTT: Successfully connected to broker");
        } catch (MqttException me) {
            logger.error("MQTT: Failed to connect to broker: {} (reason: {})", broker, me.getReasonCode(), me);
        }
    }

    @Override
    public void publish(PostInfo post) {
        logger.info("MQTT: Publishing post event for post ID: {}", post.getId());
        // Map domain object to MQTT event DTO
        PostPublishedEvent event = new PostPublishedEvent(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getDate(),
                post.getUserRef()
        );
        this.publishMessage(asJsonString(event), 2);
        logger.debug("MQTT: Post event published successfully");
    }

    private String asJsonString(final Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void publishMessage(String content, int qos) {
        MqttMessage message = new MqttMessage(content.getBytes());
        message.setQos(qos);
        try {
            logger.debug("MQTT: Publishing message to topic: {}", topic);
            client.publish(topic, message);
        } catch (MqttException me) {
            logger.error("MQTT: Failed to publish message to topic {} (reason: {})", topic, me.getReasonCode(), me);
        }
    }
}
