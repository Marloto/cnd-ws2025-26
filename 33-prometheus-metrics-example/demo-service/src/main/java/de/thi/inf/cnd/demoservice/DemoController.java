package de.thi.inf.cnd.demoservice;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class DemoController {

    private static final Logger log = LoggerFactory.getLogger(DemoController.class);
    private final Random random = new Random();

    // Micrometer metrics
    private final Counter requestCounter;
    private final Counter errorCounter;
    private final Timer responseTimer;
    private final AtomicInteger activeUsers;

    @Value("${spring.application.name:demo-service}")
    private String serviceName;

    public DemoController(MeterRegistry registry) {
        // Custom counter: total requests
        this.requestCounter = Counter.builder("demo_requests_total")
            .description("Total number of requests to the demo service")
            .tag("service", "demo-service")
            .register(registry);

        // Custom counter: errors
        this.errorCounter = Counter.builder("demo_errors_total")
            .description("Total number of errors")
            .tag("service", "demo-service")
            .register(registry);

        // Custom timer: response time
        this.responseTimer = Timer.builder("demo_response_time")
            .description("Response time of demo endpoints")
            .tag("service", "demo-service")
            .register(registry);

        // Custom gauge: active users (simulated)
        this.activeUsers = registry.gauge("demo_active_users",
            new AtomicInteger(0));
    }

    @GetMapping("/hello")
    public Map<String, String> hello() {
        return responseTimer.record(() -> {
            // Increment request counter
            requestCounter.increment();

            // Simulate some processing time
            simulateProcessing();

            log.info("Processing hello request");

            return Map.of(
                "service", serviceName,
                "message", "Hello from Demo Service",
                "timestamp", String.valueOf(System.currentTimeMillis())
            );
        });
    }

    @GetMapping("/slow")
    public Map<String, String> slow() {
        return responseTimer.record(() -> {
            requestCounter.increment();

            // Simulate slow endpoint (500ms - 2s)
            try {
                Thread.sleep(500 + random.nextInt(1500));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            log.info("Processing slow request");

            return Map.of(
                "service", serviceName,
                "message", "This was a slow operation",
                "duration", "500-2000ms"
            );
        });
    }

    @GetMapping("/error")
    public Map<String, String> error() {
        requestCounter.increment();
        errorCounter.increment();

        log.error("Error endpoint called - simulating error");

        // Simulate random errors
        if (random.nextBoolean()) {
            throw new RuntimeException("Simulated random error");
        }

        return Map.of(
            "service", serviceName,
            "error", "Simulated error",
            "timestamp", String.valueOf(System.currentTimeMillis())
        );
    }

    @GetMapping("/users/login")
    public Map<String, String> login() {
        requestCounter.increment();
        // Simulate user login
        activeUsers.incrementAndGet();

        log.info("User logged in. Active users: {}", activeUsers.get());

        return Map.of(
            "service", serviceName,
            "message", "User logged in",
            "activeUsers", String.valueOf(activeUsers.get())
        );
    }

    @GetMapping("/users/logout")
    public Map<String, String> logout() {
        requestCounter.increment();
        // Simulate user logout
        if (activeUsers.get() > 0) {
            activeUsers.decrementAndGet();
        }

        log.info("User logged out. Active users: {}", activeUsers.get());

        return Map.of(
            "service", serviceName,
            "message", "User logged out",
            "activeUsers", String.valueOf(activeUsers.get())
        );
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of(
            "status", "UP",
            "service", serviceName
        );
    }

    private void simulateProcessing() {
        // Simulate 10-100ms processing time
        try {
            Thread.sleep(10 + random.nextInt(90));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
