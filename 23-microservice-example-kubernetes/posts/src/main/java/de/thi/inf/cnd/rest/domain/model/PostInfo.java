package de.thi.inf.cnd.rest.domain.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

@Data
@AllArgsConstructor
public class PostInfo {
    @Setter(AccessLevel.NONE)
    private UUID id;
    private String title;
    private String content;
    private LocalDateTime date;
    private String userRef;

    public PostInfo() {
        this.id = UUID.randomUUID();
    }
}
