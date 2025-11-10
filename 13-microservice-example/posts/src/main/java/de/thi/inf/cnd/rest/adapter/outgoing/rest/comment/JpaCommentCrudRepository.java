package de.thi.inf.cnd.rest.adapter.outgoing.rest.comment;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface JpaCommentCrudRepository extends CrudRepository<CommentEntity, UUID> {
    List<CommentEntity> findByPostId(UUID postId);
}
