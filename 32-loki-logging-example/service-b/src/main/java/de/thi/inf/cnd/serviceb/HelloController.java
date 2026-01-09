package de.thi.inf.cnd.serviceb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class HelloController {

    private static final Logger log = LoggerFactory.getLogger(HelloController.class);
    private final RestClient restClient = RestClient.create();

    @Value("${spring.application.name:service-b}")
    private String serviceName;

    @Value("${server.port:8082}")
    private String port;

    @GetMapping("/hello")
    public Map<String, String> hello() {
        // Generate trace ID for this request
        String traceId = UUID.randomUUID().toString();
        MDC.put("traceId", traceId);
        MDC.put("spanId", UUID.randomUUID().toString().substring(0, 16));

        log.info("Processing hello request");
        log.debug("Service name: {}, Port: {}", serviceName, port);

        Map<String, String> response = Map.of(
            "service", serviceName,
            "message", "Hello from Service B",
            "port", port,
            "traceId", traceId
        );

        log.info("Returning response: {}", response);

        // Clean up MDC
        MDC.clear();

        return response;
    }

    @GetMapping("/call-service-a")
    public Map<String, Object> callServiceA() {
        // Generate trace ID that will be shared across services
        String traceId = UUID.randomUUID().toString();
        MDC.put("traceId", traceId);
        MDC.put("spanId", UUID.randomUUID().toString().substring(0, 16));

        log.info("Service B is calling Service A");

        try {
            // Call service-a (using Docker service name)
            log.debug("Making HTTP request to service-a");
            Map<String, String> serviceAResponse = restClient.get()
                .uri("http://service-a:8081/hello")
                .header("X-Trace-ID", traceId)  // Pass trace ID to service-a
                .retrieve()
                .body(Map.class);

            log.info("Successfully received response from Service A");

            // Build combined response
            Map<String, Object> response = new HashMap<>();
            response.put("service", serviceName);
            response.put("message", "Hello from Service B");
            response.put("port", port);
            response.put("called-service", "service-a");
            response.put("service-a-response", serviceAResponse);
            response.put("traceId", traceId);

            log.info("Returning combined response");

            MDC.clear();
            return response;

        } catch (Exception e) {
            log.error("Failed to call Service A", e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("service", serviceName);
            errorResponse.put("error", "Failed to call service-a: " + e.getMessage());
            errorResponse.put("traceId", traceId);

            MDC.clear();
            return errorResponse;
        }
    }
}
