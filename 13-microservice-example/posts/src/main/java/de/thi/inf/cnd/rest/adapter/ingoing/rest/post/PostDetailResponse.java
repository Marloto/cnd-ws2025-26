package de.thi.inf.cnd.rest.adapter.ingoing.rest.post;

import de.thi.inf.cnd.rest.adapter.ingoing.rest.comment.CommentResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDetailResponse {
    private UUID id;
    private String title;
    private String content;
    private LocalDateTime date;
    private List<CommentResponse> comments;
}
