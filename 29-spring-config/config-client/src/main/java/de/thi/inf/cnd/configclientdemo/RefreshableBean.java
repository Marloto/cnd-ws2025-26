package de.thi.inf.cnd.configclientdemo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@RefreshScope
@Component
public class RefreshableBean {
    @Value("${cnd.example:unknown}")
    private String msg;

    public String getMsg() {
        return msg + " (refreshable)";
    }
}
