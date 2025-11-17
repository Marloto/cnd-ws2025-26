package de.thi.inf.cnd.rest.adapter.ingoing.rest.post;

import de.thi.inf.cnd.rest.adapter.ingoing.rest.auth.AuthenticatedUser;
import de.thi.inf.cnd.rest.adapter.ingoing.rest.auth.JwtService;
import de.thi.inf.cnd.rest.adapter.ingoing.rest.comment.CommentResponse;
import de.thi.inf.cnd.rest.domain.PostService;
import de.thi.inf.cnd.rest.domain.model.Post;
import de.thi.inf.cnd.rest.domain.model.PostInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/posts")
public class RestPost {
    private static final Logger logger = LoggerFactory.getLogger(RestPost.class);

    private final PostService postService;
    private final JwtService jwtService;

    public RestPost(PostService postService, JwtService jwtService) {
        this.postService = postService;
        this.jwtService = jwtService;
    }

    @GetMapping
    public Iterable<PostResponse> listPosts() {
        logger.info("REST: GET /posts - Listing all posts");
        List<PostResponse> responses = new ArrayList<>();
        this.postService.findAllPosts().forEach(post ->
                responses.add(new PostResponse(post.getId(), post.getTitle(), post.getContent(), post.getDate(), post.getUserRef())));
        logger.info("REST: GET /posts - Returned {} posts", responses.size());
        return responses;
    }

    @GetMapping("/{id}")
    public PostDetailResponse getPostById(@PathVariable UUID id) {
        logger.info("REST: GET /posts/{} - Fetching post details", id);
        Post post = this.postService.getPost(id);
        if (post == null) {
            logger.warn("REST: GET /posts/{} - Post not found", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        // Map comments from domain model to DTOs
        List<CommentResponse> commentResponses = post.getComments().stream()
                .map(comment -> new CommentResponse(comment.getId(), comment.getText(), comment.getDate()))
                .collect(Collectors.toList());

        logger.info("REST: GET /posts/{} - Returned post with {} comments", id, commentResponses.size());
        return new PostDetailResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getDate(),
                post.getUserRef(),
                commentResponses
        );
    }

    @PostMapping
    public ResponseEntity addPost(@RequestHeader("Authorization") String authHeader,
                                   @RequestBody CreatePostRequest request) {
        logger.info("REST: POST /posts - Creating new post with title: {}", request.getTitle());
        // Check authentication (JWT validation)
        AuthenticatedUser user = jwtService.validateAuthHeader(authHeader);
        if (user == null) {
            logger.warn("REST: POST /posts - Unauthorized request");
            return ResponseEntity.status(401).body("Unauthorized");
        }

        // Create post with user reference
        PostInfo post = this.postService.createPost(request.getTitle(), request.getContent(), user.getUserId());

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(post.getId())
                .toUri();

        logger.info("REST: POST /posts - Created post with ID: {} for user: {}", post.getId(), user.getUserId());
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> updatePost(@RequestHeader("Authorization") String authHeader,
                                                     @PathVariable UUID id,
                                                     @RequestBody CreatePostRequest request) {
        logger.info("REST: PUT /posts/{} - Updating post", id);
        // Check authentication (JWT validation)
        AuthenticatedUser user = jwtService.validateAuthHeader(authHeader);
        if (user == null) {
            logger.warn("REST: PUT /posts/{} - Unauthorized request", id);
            return ResponseEntity.status(401).body(null);
        }

        try {
            PostInfo updatedPost = this.postService.updatePost(id, request.getTitle(), request.getContent(), user.getUserId());
            if (updatedPost == null) {
                logger.warn("REST: PUT /posts/{} - Post not found", id);
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
            // Map domain object to DTO
            PostResponse response = new PostResponse(
                    updatedPost.getId(),
                    updatedPost.getTitle(),
                    updatedPost.getContent(),
                    updatedPost.getDate(),
                    updatedPost.getUserRef()
            );
            logger.info("REST: PUT /posts/{} - Successfully updated post", id);
            return ResponseEntity.ok(response);
        } catch (SecurityException e) {
            logger.warn("REST: PUT /posts/{} - Forbidden: {}", id, e.getMessage());
            return ResponseEntity.status(403).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deletePost(@RequestHeader("Authorization") String authHeader,
                                      @PathVariable UUID id) {
        logger.info("REST: DELETE /posts/{} - Deleting post", id);
        // Check authentication (JWT validation)
        AuthenticatedUser user = jwtService.validateAuthHeader(authHeader);
        if (user == null) {
            logger.warn("REST: DELETE /posts/{} - Unauthorized request", id);
            return ResponseEntity.status(401).body("Unauthorized");
        }

        try {
            this.postService.removePost(id, user.getUserId());
            logger.info("REST: DELETE /posts/{} - Successfully deleted post", id);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (SecurityException e) {
            logger.warn("REST: DELETE /posts/{} - Forbidden: {}", id, e.getMessage());
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }
}
