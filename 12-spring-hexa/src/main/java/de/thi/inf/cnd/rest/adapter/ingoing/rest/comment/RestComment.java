package de.thi.inf.cnd.rest.adapter.ingoing.rest.comment;

import de.thi.inf.cnd.rest.domain.CommentService;
import de.thi.inf.cnd.rest.domain.model.Comment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/posts/{postId}/comments")
public class RestComment {

    private final CommentService commentService;

    public RestComment(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public Iterable<CommentResponse> getComments(@PathVariable UUID postId) {
        List<CommentResponse> responses = new ArrayList<>();
        this.commentService.getCommentsByPostId(postId).forEach(comment ->
                responses.add(new CommentResponse(comment.getId(), comment.getText(), comment.getDate())));
        return responses;
    }

    @PostMapping
    public ResponseEntity<CommentResponse> addComment(@PathVariable UUID postId, @RequestBody CreateCommentRequest request) {
        Comment comment = this.commentService.addComment(postId, request.getText());
        if (comment == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        CommentResponse response = new CommentResponse(comment.getId(), comment.getText(), comment.getDate());
        return ResponseEntity.ok(response);
    }
}
