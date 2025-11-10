package de.thi.inf.cnd.rest.adapter.ingoing.rest.post;

import de.thi.inf.cnd.rest.adapter.ingoing.rest.comment.CommentResponse;
import de.thi.inf.cnd.rest.domain.PostService;
import de.thi.inf.cnd.rest.domain.model.Post;
import de.thi.inf.cnd.rest.domain.model.PostInfo;
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
    private final PostService postService;

    public RestPost(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public Iterable<PostResponse> listPosts() {
        List<PostResponse> responses = new ArrayList<>();
        this.postService.findAllPosts().forEach(post ->
                responses.add(new PostResponse(post.getId(), post.getTitle(), post.getContent(), post.getDate())));
        return responses;
    }

    @GetMapping("/{id}")
    public PostDetailResponse getPostById(@PathVariable UUID id) {
        Post post = this.postService.getPost(id);
        if (post == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        // Map comments from domain model to DTOs
        List<CommentResponse> commentResponses = post.getComments().stream()
                .map(comment -> new CommentResponse(comment.getId(), comment.getText(), comment.getDate()))
                .collect(Collectors.toList());

        return new PostDetailResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getDate(),
                commentResponses
        );
    }

    @PostMapping
    public ResponseEntity addPost(@RequestBody CreatePostRequest request) {
        // Daten validieren
        PostInfo post = this.postService.createPost(request.getTitle(), request.getContent());

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(post.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> updatePost(@PathVariable UUID id, @RequestBody CreatePostRequest request) {
        PostInfo updatedPost = this.postService.updatePost(id, request.getTitle(), request.getContent());
        if (updatedPost == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        // Map domain object to DTO
        PostResponse response = new PostResponse(
                updatedPost.getId(),
                updatedPost.getTitle(),
                updatedPost.getContent(),
                updatedPost.getDate()
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deletePost(@PathVariable UUID id) {
        this.postService.removePost(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
