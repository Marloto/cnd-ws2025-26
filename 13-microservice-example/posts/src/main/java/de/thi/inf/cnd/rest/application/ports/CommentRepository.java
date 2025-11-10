package de.thi.inf.cnd.rest.application.ports;

import de.thi.inf.cnd.rest.domain.model.Comment;

import java.util.List;
import java.util.UUID;

public interface CommentRepository {
    void save(Comment comment, UUID postId);
    List<Comment> getCommentsByPostId(UUID postId);
    Comment getComment(UUID id);
}
