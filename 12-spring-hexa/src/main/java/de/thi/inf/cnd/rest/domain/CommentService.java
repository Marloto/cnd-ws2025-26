package de.thi.inf.cnd.rest.domain;

import de.thi.inf.cnd.rest.domain.model.Comment;

import java.util.List;
import java.util.UUID;

public interface CommentService {
    Comment addComment(UUID postId, String text);
    List<Comment> getCommentsByPostId(UUID postId);
}
