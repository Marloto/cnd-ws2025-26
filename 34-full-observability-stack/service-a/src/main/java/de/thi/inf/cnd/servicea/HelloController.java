package de.thi.inf.cnd.servicea;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HelloController {

    @Value("${service.message:Hello from Service A}")
    private String message;

    @Value("${server.port:8080}")
    private String port;

    @GetMapping("/hello")
    public Map<String, String> hello() {
        return Map.of(
            "service", "service-a",
            "message", message,
            "port", port
        );
    }
}
