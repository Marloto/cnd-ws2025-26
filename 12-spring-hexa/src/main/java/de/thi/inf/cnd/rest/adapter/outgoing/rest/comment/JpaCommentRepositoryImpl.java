package de.thi.inf.cnd.rest.adapter.outgoing.rest.comment;

import de.thi.inf.cnd.rest.application.ports.CommentRepository;
import de.thi.inf.cnd.rest.domain.model.Comment;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class JpaCommentRepositoryImpl implements CommentRepository {

    private final JpaCommentCrudRepository repository;

    public JpaCommentRepositoryImpl(JpaCommentCrudRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(Comment comment, UUID postId) {
        CommentEntity entity = new CommentEntity();
        entity.setId(comment.getId());
        entity.setPostId(postId);
        entity.setText(comment.getText());
        entity.setDate(comment.getDate());
        entity.setUserRef(comment.getUserRef());
        this.repository.save(entity);
    }

    @Override
    public List<Comment> getCommentsByPostId(UUID postId) {
        List<Comment> comments = new ArrayList<>();
        this.repository.findByPostId(postId).forEach(entity -> comments.add(entityToDomain(entity)));
        return comments;
    }

    @Override
    public Comment getComment(UUID id) {
        Optional<CommentEntity> entity = this.repository.findById(id);
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
