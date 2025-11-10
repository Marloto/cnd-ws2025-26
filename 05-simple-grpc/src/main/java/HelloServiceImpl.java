import de.thi.inf.cnd.simplegrpc.HelloRequest;
import de.thi.inf.cnd.simplegrpc.HelloResponse;
import de.thi.inf.cnd.simplegrpc.HelloServiceGrpc;
import io.grpc.stub.StreamObserver;

public class HelloServiceImpl extends HelloServiceGrpc.HelloServiceImplBase {
    @Override
    public void hello(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
        String first = request.getFirstName();
        String last = request.getLastName();

        String greeting = String.format("Hello, %s!", first + " " + last);

        HelloResponse response = HelloResponse
                .newBuilder()
                .setGreeting(greeting)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
