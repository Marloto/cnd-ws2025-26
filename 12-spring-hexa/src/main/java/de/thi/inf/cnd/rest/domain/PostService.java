package de.thi.inf.cnd.rest.domain;

import de.thi.inf.cnd.rest.domain.model.Post;
import de.thi.inf.cnd.rest.domain.model.PostInfo;

import java.util.UUID;

public interface PostService {
    PostInfo createPost(String title, String content);
    Iterable<PostInfo> findAllPosts();
    Post getPost(UUID id);
    PostInfo removePost(UUID id);
    PostInfo updatePost(UUID id, String title, String content);
}
