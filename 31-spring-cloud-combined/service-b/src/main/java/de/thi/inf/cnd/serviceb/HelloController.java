package de.thi.inf.cnd.serviceb;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HelloController {

    @Value("${service.message:Hello from Service B}")
    private String message;

    @Value("${server.port:8080}")
    private String port;

    private final RestClient loadBalancedRestClient;

    public HelloController(RestClient.Builder loadBalancedRestClientBuilder) {
        this.loadBalancedRestClient = loadBalancedRestClientBuilder.build();
    }

    @GetMapping("/hello")
    public Map<String, String> hello() {
        return Map.of(
            "service", "service-b",
            "message", message,
            "port", port
        );
    }

    @GetMapping("/call-service-a")
    public Map<String, Object> callServiceA() {
        try {
            // Call service-a using load-balanced RestClient
            Map<String, String> serviceAResponse = loadBalancedRestClient.get()
                .uri("http://service-a/hello")
                .retrieve()
                .body(Map.class);

            // Build combined response
            Map<String, Object> response = new HashMap<>();
            response.put("service", "service-b");
            response.put("message", message);
            response.put("port", port);
            response.put("called-service", "service-a");
            response.put("service-a-response", serviceAResponse);

            return response;
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("service", "service-b");
            errorResponse.put("error", "Failed to call service-a: " + e.getMessage());
            return errorResponse;
        }
    }
}
