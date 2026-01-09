package de.thi.inf.cnd.servicea;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
public class HelloController {

    private static final Logger log = LoggerFactory.getLogger(HelloController.class);

    @Value("${spring.application.name:service-a}")
    private String serviceName;

    @Value("${server.port:8081}")
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
            "message", "Hello from Service A",
            "port", port,
            "traceId", traceId
        );

        log.info("Returning response: {}", response);

        // Clean up MDC
        MDC.clear();

        return response;
    }

    @GetMapping("/error")
    public Map<String, String> error() {
        String traceId = UUID.randomUUID().toString();
        MDC.put("traceId", traceId);

        log.warn("Error endpoint called - simulating error");

        try {
            // Simulate some error condition
            throw new RuntimeException("Simulated error for logging demonstration");
        } catch (Exception e) {
            log.error("An error occurred while processing request", e);
            MDC.clear();
            return Map.of(
                "service", serviceName,
                "error", "Simulated error",
                "traceId", traceId
            );
        }
    }
}
