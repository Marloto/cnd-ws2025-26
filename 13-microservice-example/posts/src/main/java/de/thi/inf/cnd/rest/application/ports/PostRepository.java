package de.thi.inf.cnd.rest.application.ports;

import de.thi.inf.cnd.rest.domain.model.Post;
import de.thi.inf.cnd.rest.domain.model.PostInfo;

import java.util.UUID;

public interface PostRepository {
    void save(PostInfo post);
    void update(PostInfo post);
    void delete(UUID id);
    PostInfo getPost(UUID id);
    Iterable<PostInfo> getPosts();
}
