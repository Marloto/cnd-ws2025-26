package de.thi.inf.cnd.rest.adapter.outgoing.rest.post;

import de.thi.inf.cnd.rest.application.ports.PostRepository;
import de.thi.inf.cnd.rest.domain.model.Post;
import de.thi.inf.cnd.rest.domain.model.PostInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class JpaPostRepositoryImpl implements PostRepository {
    private static final Logger logger = LoggerFactory.getLogger(JpaPostRepositoryImpl.class);

    private final JpaPostCrudRepository repository;

    public JpaPostRepositoryImpl(JpaPostCrudRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(PostInfo post) {
        logger.info("REPOSITORY: Saving post with ID: {}", post.getId());
        PostEntity postEntity = new PostEntity();
        postEntity.setId(post.getId());
        postEntity.setTitle(post.getTitle());
        postEntity.setContent(post.getContent());
        postEntity.setDate(post.getDate());
        postEntity.setUserRef(post.getUserRef());
        this.repository.save(postEntity);
        logger.debug("REPOSITORY: Post {} saved to database", post.getId());
    }

    @Override
    public void update(PostInfo post) {
        logger.info("REPOSITORY: Updating post with ID: {}", post.getId());
        Optional<PostEntity> existingEntity = this.repository.findById(post.getId());
        if (existingEntity.isPresent()) {
            PostEntity entity = existingEntity.get();
            entity.setTitle(post.getTitle());
            entity.setContent(post.getContent());
            entity.setDate(post.getDate());
            entity.setUserRef(post.getUserRef());
            this.repository.save(entity);
            logger.debug("REPOSITORY: Post {} updated in database", post.getId());
        }
    }

    @Override
    public void delete(UUID id) {
        logger.info("REPOSITORY: Deleting post with ID: {}", id);
        this.repository.deleteById(id);
        logger.debug("REPOSITORY: Post {} deleted from database", id);
    }

    @Override
    public PostInfo getPost(UUID id) {
        logger.debug("REPOSITORY: Fetching post with ID: {}", id);
        Optional<PostEntity> entity = this.repository.findById(id);
        if (entity.isPresent()) {
            logger.debug("REPOSITORY: Found post {}", id);
            return entityToDomain(entity.get());
        }
        logger.debug("REPOSITORY: Post {} not found", id);
        return null;
    }

    @Override
    public Iterable<PostInfo> getPosts() {
        logger.debug("REPOSITORY: Fetching all posts from database");
        List<PostInfo> posts = new ArrayList<>();
        this.repository.findAll().forEach(entity -> posts.add(entityToDomain(entity)));
        logger.debug("REPOSITORY: Retrieved {} posts from database", posts.size());
        return posts;
    }

    private PostInfo entityToDomain(PostEntity entity) {
        // Use all-args constructor to set ID (since it has @Setter(AccessLevel.NONE))
        // Constructor order: id, title, content, date, userRef, comments
        return new PostInfo(
                entity.getId(),
                entity.getTitle(),
                entity.getContent(),
                entity.getDate(),
                entity.getUserRef()
        );
    }
}
