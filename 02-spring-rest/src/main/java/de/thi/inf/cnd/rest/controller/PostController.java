package de.thi.inf.cnd.rest.controller;

import de.thi.inf.cnd.rest.model.Post;
import de.thi.inf.cnd.rest.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
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

  private PostRepository postRepository;

  public PostController(PostRepository postRepository) {
    this.postRepository = postRepository;
  }

  @GetMapping
  public Iterable<Post> listPosts() {
    return postRepository.findAll();
  }

  @GetMapping("/{id}")
  public Post getPostById(@PathVariable  UUID id) {
    Optional<Post> post = postRepository.findById(id);
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
    postRepository.save(entry);
    // Variante 1: Return Post-Objekt mit: return entry;
    // Alternativ 201 Created

    URI location = ServletUriComponentsBuilder
      .fromCurrentRequest()
      .path("/{id}")
      .buildAndExpand(entry.getId())
      .toUri();

    return ResponseEntity.created(location).build();
  }
}
