import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class Publisher {
    public static void main(String[] args) {
        // Config Publisher Connection
        int qos = 2;
        String topic = "example";
        String broker = "tcp://localhost:1883";
        String clientId = "ExampleClient";
        MemoryPersistence persistence = new MemoryPersistence();

        try {
            // Create Connection
            MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println("Connecting to broker: " + broker);
            sampleClient.connect(connOpts);
            System.out.println("Connected");

            // Send Events
            int i = 0;
            while(i < 100) {
                MqttMessage message = new MqttMessage();
                message.setPayload(("Hello World " + i).getBytes());
                sampleClient.publish(topic, message);
                i ++;
                Thread.sleep(1000);
            }

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
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}