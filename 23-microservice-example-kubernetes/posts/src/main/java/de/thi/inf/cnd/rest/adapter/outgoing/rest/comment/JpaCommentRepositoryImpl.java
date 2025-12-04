package de.thi.inf.cnd.rest.adapter.outgoing.rest.comment;

import de.thi.inf.cnd.rest.application.ports.CommentRepository;
import de.thi.inf.cnd.rest.domain.model.Comment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class JpaCommentRepositoryImpl implements CommentRepository {
    private static final Logger logger = LoggerFactory.getLogger(JpaCommentRepositoryImpl.class);

    private final JpaCommentCrudRepository repository;

    public JpaCommentRepositoryImpl(JpaCommentCrudRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(Comment comment, UUID postId) {
        logger.info("REPOSITORY: Saving comment with ID: {} for post {}", comment.getId(), postId);
        CommentEntity entity = new CommentEntity();
        entity.setId(comment.getId());
        entity.setPostId(postId);
        entity.setText(comment.getText());
        entity.setDate(comment.getDate());
        entity.setUserRef(comment.getUserRef());
        this.repository.save(entity);
        logger.debug("REPOSITORY: Comment {} saved to database", comment.getId());
    }

    @Override
    public List<Comment> getCommentsByPostId(UUID postId) {
        logger.debug("REPOSITORY: Fetching comments for post {}", postId);
        List<Comment> comments = new ArrayList<>();
        this.repository.findByPostId(postId).forEach(entity -> comments.add(entityToDomain(entity)));
        logger.debug("REPOSITORY: Retrieved {} comments for post {}", comments.size(), postId);
        return comments;
    }

    @Override
    public Comment getComment(UUID id) {
        logger.debug("REPOSITORY: Fetching comment with ID: {}", id);
        Optional<CommentEntity> entity = this.repository.findById(id);
        if (entity.isPresent()) {
            logger.debug("REPOSITORY: Found comment {}", id);
        } else {
            logger.debug("REPOSITORY: Comment {} not found", id);
        }
        return entity.map(this::entityToDomain).orElse(null);
    }

    private Comment entityToDomain(CommentEntity entity) {
        // Use all-args constructor to set ID (since it has @Setter(AccessLevel.NONE))
        // Constructor order: id, text, date, userRef
        return new Comment(
                entity.getId(),
                entity.getText(),
                entity.getDate(),
                entity.getUserRef()
        );
    }
}
