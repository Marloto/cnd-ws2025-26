package de.thi.inf.cnd.rest.adapter.ingoing.rest.comment;

import de.thi.inf.cnd.rest.adapter.ingoing.rest.auth.AuthenticatedUser;
import de.thi.inf.cnd.rest.adapter.ingoing.rest.auth.JwtService;
import de.thi.inf.cnd.rest.domain.CommentService;
import de.thi.inf.cnd.rest.domain.model.Comment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/posts/{postId}/comments")
public class RestComment {
    private static final Logger logger = LoggerFactory.getLogger(RestComment.class);

    private final CommentService commentService;
    private final JwtService jwtService;

    public RestComment(CommentService commentService, JwtService jwtService) {
        this.commentService = commentService;
        this.jwtService = jwtService;
    }

    @GetMapping
    public Iterable<CommentResponse> getComments(@PathVariable UUID postId) {
        logger.info("REST: GET /posts/{}/comments - Fetching comments for post", postId);
        List<CommentResponse> responses = new ArrayList<>();
        this.commentService.getCommentsByPostId(postId).forEach(comment ->
                responses.add(new CommentResponse(comment.getId(), comment.getText(), comment.getDate())));
        logger.info("REST: GET /posts/{}/comments - Returned {} comments", postId, responses.size());
        return responses;
    }

    @PostMapping
    public ResponseEntity<CommentResponse> addComment(@RequestHeader("Authorization") String authHeader,
                                                        @PathVariable UUID postId,
                                                        @RequestBody CreateCommentRequest request) {
        logger.info("REST: POST /posts/{}/comments - Adding comment to post", postId);
        // Check authentication (JWT validation)
        AuthenticatedUser user = jwtService.validateAuthHeader(authHeader);
        if (user == null) {
            logger.warn("REST: POST /posts/{}/comments - Unauthorized request", postId);
            return ResponseEntity.status(401).body(null);
        }

        Comment comment = this.commentService.addComment(postId, request.getText(), user.getUserId());
        if (comment == null) {
            logger.warn("REST: POST /posts/{}/comments - Post not found", postId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        CommentResponse response = new CommentResponse(comment.getId(), comment.getText(), comment.getDate());
        logger.info("REST: POST /posts/{}/comments - Created comment with ID: {} for user: {}", postId, comment.getId(), user.getUserId());
        return ResponseEntity.ok(response);
    }
}
