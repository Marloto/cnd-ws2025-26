package de.thi.inf.cnd.rest.adapter.outgoing.rest.post;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class PostEntity {
    @Id
    private UUID id;
    private String title;
    private String content;
    private LocalDateTime date;
    private String userRef;
}
