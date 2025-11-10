package de.thi.inf.cnd.rest.controller;

import de.thi.inf.cnd.rest.model.Post;
import de.thi.inf.cnd.rest.repository.PostRepository;
import de.thi.inf.cnd.rest.services.PostService;
import de.thi.inf.cnd.rest.services.PublisherService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController // Annotationen, dekorieren die Klasse mit Zusatzinformationen
@RequestMapping("/posts")
public class PostController {

    private final PostRepository repository;
    private final PublisherService publisherService;
    private final PostService postService;

    public PostController(PostRepository postRepository, PublisherService publisherService,  PostService postService) {
        this.repository = postRepository;
        this.publisherService = publisherService;
        this.postService = postService;
    }

    @GetMapping
    public Iterable<SimplePostResponse> listPosts() {
        List<SimplePostResponse> responses = new ArrayList<>();
        repository.findAll().forEach((el) -> responses.add(new SimplePostResponse(el.getId(), el.getTitle(), el.getContent())));
        return responses;
    }

    @GetMapping("/{id}")
    public SimplePostResponse getPostById(@PathVariable UUID id) {
        Optional<Post> post = repository.findById(id);
        if (post.isPresent()) {
            return new  SimplePostResponse(post.get().getId(), post.get().getTitle(), post.get().getContent());
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity addPost(@RequestBody CreatePostRequest post) {
        Post entry = this.postService.createPost(post.getTitle(), post.getContent());

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(entry.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity updatePost(@PathVariable UUID id, @RequestBody Post post) {
        Optional<Post> existingPost = repository.findById(id);
        if (existingPost.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        Post entry = existingPost.get();
        entry.setTitle(post.getTitle());
        entry.setContent(post.getContent());
        entry.setDate(LocalDateTime.now());
        repository.save(entry);

        return ResponseEntity.ok(entry);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deletePost(@PathVariable UUID id) {
        repository.deleteById(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
