package de.thi.inf.cnd.rest.application;

import de.thi.inf.cnd.rest.application.ports.CommentRepository;
import de.thi.inf.cnd.rest.domain.CommentService;
import de.thi.inf.cnd.rest.domain.model.Comment;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public Comment addComment(UUID postId, String text) {
        Comment comment = new Comment();
        comment.setText(text);
        comment.setDate(LocalDateTime.now());
        // The postId is passed to the repository adapter to handle the relationship
        this.commentRepository.save(comment, postId);
        return comment;
    }

    @Override
    public List<Comment> getCommentsByPostId(UUID postId) {
        return this.commentRepository.getCommentsByPostId(postId);
    }
}
