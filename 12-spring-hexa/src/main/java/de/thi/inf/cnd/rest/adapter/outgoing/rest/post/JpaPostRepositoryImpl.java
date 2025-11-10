package de.thi.inf.cnd.rest.adapter.outgoing.rest.post;

import de.thi.inf.cnd.rest.application.ports.PostRepository;
import de.thi.inf.cnd.rest.domain.model.Post;
import de.thi.inf.cnd.rest.domain.model.PostInfo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class JpaPostRepositoryImpl implements PostRepository {

    private final JpaPostCrudRepository repository;

    public JpaPostRepositoryImpl(JpaPostCrudRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(PostInfo post) {
        PostEntity postEntity = new PostEntity();
        postEntity.setId(post.getId());
        postEntity.setTitle(post.getTitle());
        postEntity.setContent(post.getContent());
        postEntity.setDate(post.getDate());
        postEntity.setUserRef(post.getUserRef());
        this.repository.save(postEntity);
    }

    @Override
    public void update(PostInfo post) {
        Optional<PostEntity> existingEntity = this.repository.findById(post.getId());
        if (existingEntity.isPresent()) {
            PostEntity entity = existingEntity.get();
            entity.setTitle(post.getTitle());
            entity.setContent(post.getContent());
            entity.setDate(post.getDate());
            entity.setUserRef(post.getUserRef());
            this.repository.save(entity);
        }
    }

    @Override
    public void delete(UUID id) {
        this.repository.deleteById(id);
    }

    @Override
    public PostInfo getPost(UUID id) {
        Optional<PostEntity> entity = this.repository.findById(id);
        if (entity.isPresent()) {
            return entityToDomain(entity.get());
        }
        return null;
    }

    @Override
    public Iterable<PostInfo> getPosts() {
        List<PostInfo> posts = new ArrayList<>();
        this.repository.findAll().forEach(entity -> posts.add(entityToDomain(entity)));
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
