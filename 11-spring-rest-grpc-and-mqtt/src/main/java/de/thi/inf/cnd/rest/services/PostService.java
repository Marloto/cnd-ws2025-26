package de.thi.inf.cnd.rest.services;

import de.thi.inf.cnd.rest.model.Post;
import de.thi.inf.cnd.rest.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PostService {

    private final PostRepository repository;
    private final PublisherService publisherService;

    public PostService(PostRepository postRepository, PublisherService publisherService) {
        this.repository = postRepository;
        this.publisherService = publisherService;
    }

    public Post createPost(String title, String content) {
        // Neues Objekt erzeugen
        Post entry = new Post();
        entry.setTitle(title);
        entry.setContent(content);
        entry.setDate(LocalDateTime.now());

        // Speichern
        repository.save(entry);

        // Event versenden
        this.publisherService.publishNewPost(entry);

        return entry;
    }
}
