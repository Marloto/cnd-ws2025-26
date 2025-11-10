import com.google.protobuf.InvalidProtocolBufferException;
import de.thi.inf.cnd.simplegrpc.HelloRequest;
import de.thi.inf.cnd.simplegrpc.HelloResponse;

public class Main {
    public static void main(String[] args) throws InvalidProtocolBufferException {
        // Warum ist JSON ineffizient?
        // -> Datentypen sind beschränkt und alles ist auf Zeichen abgebildet
        // -> Formatierung z.B. zum Einrücken
        // -> [{"long": ..., "lat": ...}, ... x 10000]
        // -> {"long": [1,2,3,4,5,...], "lat": [1,2,3,4,5,...]}
        HelloResponse resp = HelloResponse
                .newBuilder()
                .setGreeting("Bla")
                .build();
        byte[] data = resp.toByteArray();

        HelloResponse response = HelloResponse.parseFrom(data);
        System.out.println(response.getGreeting());
    }
}