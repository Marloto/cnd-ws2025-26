package de.thi.inf.cnd.configclientdemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExampleRestController {
    @Value("${cnd.example:unknown}")
    private String msg;

    @Autowired
    private RefreshableBean refreshableBean;

    @GetMapping("/hello")
    public Message hello() {
        return new Message(msg);
    }

    @GetMapping("/hello-refreshable")
    public Message helloRefreshable() {
        return new Message(refreshableBean.getMsg());
    }
}
