package de.thi.inf.cnd.rest.adapter.outgoing.rest.comment;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class CommentEntity {
    @Id
    private UUID id;
    private UUID postId;
    private String text;
    private LocalDateTime date;
    private String userRef;
}
