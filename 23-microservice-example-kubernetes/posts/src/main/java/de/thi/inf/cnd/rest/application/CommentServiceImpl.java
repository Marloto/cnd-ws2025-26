package de.thi.inf.cnd.rest.application;

import de.thi.inf.cnd.rest.application.ports.CommentRepository;
import de.thi.inf.cnd.rest.domain.CommentService;
import de.thi.inf.cnd.rest.domain.model.Comment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CommentServiceImpl implements CommentService {
    private static final Logger logger = LoggerFactory.getLogger(CommentServiceImpl.class);

    private final CommentRepository commentRepository;

    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public Comment addComment(UUID postId, String text, String userRef) {
        logger.info("SERVICE: Adding comment to post {} for user '{}'", postId, userRef);
        Comment comment = new Comment();
        comment.setText(text);
        comment.setDate(LocalDateTime.now());
        comment.setUserRef(userRef);  // Store authenticated user reference
        // The postId is passed to the repository adapter to handle the relationship
        this.commentRepository.save(comment, postId);
        logger.info("SERVICE: Successfully added comment with ID: {}", comment.getId());
        return comment;
    }

    @Override
    public List<Comment> getCommentsByPostId(UUID postId) {
        logger.info("SERVICE: Getting comments for post {}", postId);
        List<Comment> comments = this.commentRepository.getCommentsByPostId(postId);
        logger.info("SERVICE: Found {} comments for post {}", comments.size(), postId);
        return comments;
    }
}
