package de.thi.inf.cnd.mqtt.services;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class PublisherService {
    // Wer mit Spring arbeitet, sollte einen kurzen Blick bzgl. Konfigurationsmöglichkeiten in die Doku werfen
    // -> verwendung von @Value erlaubt die Konfiguration über verschiedene Wege (applications.properties / .yml, ENV, CLI-Arguments, usw.)
    @Value("${mqtt.broker}")
    private String broker;

    @Value("${mqtt.client}")
    private String clientId;

    @Value("${mqtt.topic}")
    private String topic;

    private MqttClient sampleClient;

    public PublisherService() {

    }

    @PostConstruct
    public void init() {
        MemoryPersistence persistence = new MemoryPersistence();
        try {
            // Create Connection
            this.sampleClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println("Connecting to broker: " + broker);
            sampleClient.connect(connOpts);
            System.out.println("Connected");
        } catch (MqttException me) {
            System.out.println("reason " + me.getReasonCode());
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
            me.printStackTrace();
        }
    }

    public void publish(String payload) {
        int qos = 2;
        try {
            // Send Events
            MqttMessage message = new MqttMessage();
            message.setPayload(payload.getBytes());
            sampleClient.publish(topic, message);
        } catch (MqttException me) {
            System.out.println("reason " + me.getReasonCode());
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
            me.printStackTrace();
        }
    }

    @PreDestroy
    public void destroy() {
        try {
            // Close Connection
            sampleClient.disconnect();
            System.out.println("Disconnected");
            System.exit(0);
        } catch (MqttException me) {
            System.out.println("reason " + me.getReasonCode());
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
            me.printStackTrace();
        }
    }
}