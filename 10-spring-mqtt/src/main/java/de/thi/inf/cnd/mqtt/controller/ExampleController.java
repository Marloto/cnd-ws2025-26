package de.thi.inf.cnd.mqtt.controller;

import de.thi.inf.cnd.mqtt.services.PublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/example")
public class ExampleController {
    public final PublisherService service; // wird von Spring bereitgestellt

    public ExampleController(PublisherService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity doSomething() {
        this.service.publish("Hello World");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}