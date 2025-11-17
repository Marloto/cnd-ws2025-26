package de.thi.inf.cnd.rest.adapter.ingoing.grpc;

import de.thi.inf.cnd.hexa.grpc.*;
import de.thi.inf.cnd.rest.domain.CommentService;
import de.thi.inf.cnd.rest.domain.PostService;
import de.thi.inf.cnd.rest.domain.model.Post;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@GrpcService
public class GrpcStatisticService extends StatisticServiceGrpc.StatisticServiceImplBase {

    private final PostService postService;
    private final CommentService commentService;

    public GrpcStatisticService(PostService postService, CommentService commentService) {
        this.postService = postService;
        this.commentService = commentService;
    }

    @Override
    public void countComments(CountCommentsRequest request, StreamObserver<CountCommentsResponse> responseObserver) {
        UUID postId = UUID.fromString(request.getPostId());
        int count = this.commentService.getCommentsByPostId(postId).size();

        CountCommentsResponse response = CountCommentsResponse.newBuilder()
                .setCount(count)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void listPostIds(ListPostIdsRequest request, StreamObserver<ListPostIdsResponse> responseObserver) {
        List<String> postIds = new ArrayList<>();
        this.postService.findAllPosts().forEach(post -> postIds.add(post.getId().toString()));

        ListPostIdsResponse response = ListPostIdsResponse.newBuilder()
                .addAllPostId(postIds)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
