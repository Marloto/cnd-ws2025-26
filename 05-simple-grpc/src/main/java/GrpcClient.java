import de.thi.inf.cnd.simplegrpc.HelloRequest;
import de.thi.inf.cnd.simplegrpc.HelloResponse;
import de.thi.inf.cnd.simplegrpc.HelloServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GrpcClient {
    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9898)
                .usePlaintext()
                .build();

        HelloServiceGrpc.HelloServiceBlockingStub stub = HelloServiceGrpc.newBlockingStub(channel);
        HelloRequest req = HelloRequest.newBuilder().setFirstName("John").setLastName("Musterman").build();

        HelloResponse resp = stub.hello(req);

        System.out.println(resp.getGreeting());

        channel.shutdown();
    }
}