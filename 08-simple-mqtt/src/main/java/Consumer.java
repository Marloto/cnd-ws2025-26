import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.IOException;

public class Consumer {
    public static void main(String[] args) {
        // Config Consumer Connection
        String topic = "example";
        String broker = "tcp://localhost:1883";
        String clientId = "ExampleServer";
        MemoryPersistence persistence = new MemoryPersistence();

        try {
            // Create Connection
            MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println("Connecting to broker: " + broker);
            sampleClient.connect(connOpts);
            System.out.println("Connected");

            // Handle Messages, hier als Callback in Java mit Lambda
            /*sampleClient.subscribe(topic, new IMqttMessageListener() {
                public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {

                }
            });*/
            // topic kann Wildcard-Elemente enthalten, das Topic im Lambda
            // enthält das konkrete Topic in dem die Nachricht eingegangen ist
            sampleClient.subscribe(topic, (t, msg) -> {
                // was passiert, wenn eine nachricht eingeht
                System.out.println(msg.toString());
            });

            // Wait for shutdown
            System.in.read();
            System.out.println("Disconnected");
            System.exit(0);
        } catch (MqttException me) {
            System.out.println("reason " + me.getReasonCode());
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
            me.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}