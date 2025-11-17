package de.thi.inf.cnd.rest.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class Post extends PostInfo {
    private List<Comment> comments;

    public Post() {
        super();
        this.comments = new ArrayList<>();
    }

    public Post(UUID id, String title, String content, LocalDateTime date, String userRef, List<Comment> comments) {
        super(id, title, content, date, userRef);
        this.comments = comments;
    }
}
