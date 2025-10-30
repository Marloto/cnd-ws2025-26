package de.thi.inf.cnd.rest.controller;

import de.thi.inf.cnd.rest.model.Post;
import de.thi.inf.cnd.rest.repository.PostRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@RestController // Annotationen, dekorieren die Klasse mit Zusatzinformationen
@RequestMapping("/posts")
public class PostController {

    private final PostRepository repository;

    public PostController(PostRepository postRepository) {
        this.repository = postRepository;
    }

    @GetMapping
    public Iterable<Post> listPosts() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Post getPostById(@PathVariable UUID id) {
        Optional<Post> post = repository.findById(id);
        if (post.isPresent()) {
            return post.get();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity addPost(@RequestBody Post post) {
        Post entry = new Post();
        entry.setTitle(post.getTitle());
        entry.setContent(post.getContent());
        entry.setDate(LocalDateTime.now());
        repository.save(entry);
        // Variante 1: Return Post-Objekt mit: return entry;
        // Alternativ 201 Created

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
