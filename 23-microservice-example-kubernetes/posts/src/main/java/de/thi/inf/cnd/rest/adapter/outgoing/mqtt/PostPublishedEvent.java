package de.thi.inf.cnd.rest.adapter.outgoing.mqtt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostPublishedEvent {
    private UUID id;
    private String title;
    private String content;
    private LocalDateTime date;
    private String userRef;
}
