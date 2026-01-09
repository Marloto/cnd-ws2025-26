package de.thi.inf.cnd.eurekaclientdemo;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ExampleRestController {

    private DiscoveryClient discovery;

    @Value("${example.name:cnd-service-example}")
    private String targetService;

    private final RestClient restClient = RestClient.create();
    private final RestClient loadBalancedRestClient;

    public ExampleRestController(DiscoveryClient discovery, RestClient.Builder loadBalancedRestClientBuilder) {
        this.loadBalancedRestClient = loadBalancedRestClientBuilder.build();
        this.discovery = discovery;
    }

    @GetMapping("/lookup")
    public List<String> lookup() {
        return discovery.getInstances(targetService).stream()
            .map(e -> e.toString())
            .collect(Collectors.toList());
    }

    @GetMapping("/hello")
    public ResponseEntity<Object> hello(HttpServletResponse response) {
        List<ServiceInstance> instances = discovery.getInstances(targetService);

        if (instances.isEmpty()) {
            response.setHeader("X-Discovery-Error", "No instances found");
            return ResponseEntity.notFound().build();
        }

        // Use the first available instance (in production, use load balancing)
        ServiceInstance instance = instances.get(0);
        String url = instance.getUri() + "/hello";

        try {
            Object result = restClient.get()
                .uri(url)
                .retrieve()
                .body(Object.class);

            response.setHeader("X-Called-Service", url);
            response.setHeader("X-Discovery-Method", "manual-first-instance");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            response.setHeader("X-Discovery-Error", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/hello-balanced")
    public ResponseEntity<Object> helloBalanced(HttpServletResponse response) {
        try {
            // Use service name directly - Spring Cloud LoadBalancer handles instance selection
            Object result = loadBalancedRestClient.get()
                .uri("http://" + targetService + "/hello")
                .retrieve()
                .body(Object.class);

            response.setHeader("X-Called-Service", targetService);
            response.setHeader("X-Discovery-Method", "load-balanced");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            response.setHeader("X-Discovery-Error", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

}
