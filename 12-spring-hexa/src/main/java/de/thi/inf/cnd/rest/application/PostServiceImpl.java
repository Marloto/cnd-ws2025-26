package de.thi.inf.cnd.rest.application;

import de.thi.inf.cnd.rest.application.ports.CommentRepository;
import de.thi.inf.cnd.rest.application.ports.PostPublisher;
import de.thi.inf.cnd.rest.application.ports.PostRepository;
import de.thi.inf.cnd.rest.domain.PostService;
import de.thi.inf.cnd.rest.domain.model.Comment;
import de.thi.inf.cnd.rest.domain.model.Post;
import de.thi.inf.cnd.rest.domain.model.PostInfo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostPublisher postPublisher;

    public PostServiceImpl(PostRepository postRepository, PostPublisher postPublisher,  CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.postPublisher = postPublisher;
        this.commentRepository = commentRepository;
    }
    
    @Override
    public PostInfo createPost(String title, String content) {
        PostInfo post = new PostInfo();
        post.setTitle(title);
        post.setContent(content);
        post.setDate(LocalDateTime.now());

        this.postRepository.save(post);
        this.postPublisher.publish(post);

        return post;
    }

    @Override
    public Iterable<PostInfo> findAllPosts() {
        return this.postRepository.getPosts();
    }

    @Override
    public Post getPost(UUID id) {
        PostInfo info = this.postRepository.getPost(id);
        if(info == null) return null;
        List<Comment> comments = this.commentRepository.getCommentsByPostId(id);
        Post post = new Post(
            info.getId(),
            info.getTitle(),
            info.getContent(),
            info.getDate(),
            info.getUserRef(),
            comments
        );
        return post;
    }

    @Override
    public PostInfo removePost(UUID id) {
        PostInfo post = this.postRepository.getPost(id);
        if (post != null) {
            this.postRepository.delete(id);
        }
        return post;
    }

    @Override
    public PostInfo updatePost(UUID id, String title, String content) {
        PostInfo post = this.postRepository.getPost(id);
        if (post != null) {
            post.setTitle(title);
            post.setContent(content);
            post.setDate(LocalDateTime.now());
            this.postRepository.update(post);
        }
        return post;
    }
}
