package de.thi.inf.cnd.rest.application;

import de.thi.inf.cnd.rest.application.ports.CommentRepository;
import de.thi.inf.cnd.rest.application.ports.PostPublisher;
import de.thi.inf.cnd.rest.application.ports.PostRepository;
import de.thi.inf.cnd.rest.domain.PostService;
import de.thi.inf.cnd.rest.domain.model.Comment;
import de.thi.inf.cnd.rest.domain.model.Post;
import de.thi.inf.cnd.rest.domain.model.PostInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PostServiceImpl implements PostService {
    private static final Logger logger = LoggerFactory.getLogger(PostServiceImpl.class);

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostPublisher postPublisher;

    public PostServiceImpl(PostRepository postRepository, PostPublisher postPublisher,  CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.postPublisher = postPublisher;
        this.commentRepository = commentRepository;
    }

    @Override
    public PostInfo createPost(String title, String content, String userRef) {
        logger.info("SERVICE: Creating post with title '{}' for user '{}'", title, userRef);
        PostInfo post = new PostInfo();
        post.setTitle(title);
        post.setContent(content);
        post.setDate(LocalDateTime.now());
        post.setUserRef(userRef);  // Store authenticated user reference

        this.postRepository.save(post);
        this.postPublisher.publish(post);

        logger.info("SERVICE: Successfully created post with ID: {}", post.getId());
        return post;
    }

    @Override
    public Iterable<PostInfo> findAllPosts() {
        logger.info("SERVICE: Finding all posts");
        Iterable<PostInfo> posts = this.postRepository.getPosts();
        logger.debug("SERVICE: Retrieved posts from repository");
        return posts;
    }

    @Override
    public Post getPost(UUID id) {
        logger.info("SERVICE: Getting post with ID: {}", id);
        PostInfo info = this.postRepository.getPost(id);
        if(info == null) {
            logger.warn("SERVICE: Post with ID {} not found", id);
            return null;
        }
        List<Comment> comments = this.commentRepository.getCommentsByPostId(id);
        Post post = new Post(
            info.getId(),
            info.getTitle(),
            info.getContent(),
            info.getDate(),
            info.getUserRef(),
            comments
        );
        logger.info("SERVICE: Retrieved post {} with {} comments", id, comments.size());
        return post;
    }

    @Override
    public PostInfo removePost(UUID id) {
        logger.info("SERVICE: Removing post with ID: {}", id);
        PostInfo post = this.postRepository.getPost(id);
        if (post != null) {
            this.postRepository.delete(id);
            logger.info("SERVICE: Successfully removed post {}", id);
        } else {
            logger.warn("SERVICE: Post with ID {} not found for removal", id);
        }
        return post;
    }

    @Override
    public PostInfo updatePost(UUID id, String title, String content) {
        logger.info("SERVICE: Updating post with ID: {}", id);
        PostInfo post = this.postRepository.getPost(id);
        if (post != null) {
            post.setTitle(title);
            post.setContent(content);
            post.setDate(LocalDateTime.now());
            this.postRepository.update(post);
            logger.info("SERVICE: Successfully updated post {}", id);
        } else {
            logger.warn("SERVICE: Post with ID {} not found for update", id);
        }
        return post;
    }
}
