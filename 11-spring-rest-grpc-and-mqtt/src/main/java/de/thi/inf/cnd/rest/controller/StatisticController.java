package de.thi.inf.cnd.rest.controller;

import com.google.protobuf.Empty;
import de.thi.inf.cnd.grpc.CountCommentsRequest;
import de.thi.inf.cnd.grpc.CountCommentsResponse;
import de.thi.inf.cnd.grpc.ListPostIdsRequest;
import de.thi.inf.cnd.grpc.ListPostIdsResponse;
import de.thi.inf.cnd.grpc.StatisticServiceGrpc.StatisticServiceImplBase;
import de.thi.inf.cnd.rest.model.Comment;
import de.thi.inf.cnd.rest.model.Post;
import de.thi.inf.cnd.rest.repository.PostRepository;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@GrpcService
public class StatisticController extends StatisticServiceImplBase {
    private final PostRepository postRepository;

    public StatisticController(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public void countComments(CountCommentsRequest request, StreamObserver<CountCommentsResponse> responseObserver) {
        // Suchen des Posts nach ID, Commentare zählen, Resp erzeugen
        Optional<Post> post = this.postRepository.findById(UUID.fromString(request.getPostId()));

        CountCommentsResponse.Builder responseBuilder = CountCommentsResponse.newBuilder();
        if (post.isPresent()) {
            int size = post.get().getComments().size();
            responseBuilder.setCount(size);
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void listPostIds(ListPostIdsRequest request, StreamObserver<ListPostIdsResponse> responseObserver) {
        Iterable<Post> all = this.postRepository.findAll();
        ListPostIdsResponse.Builder resp = ListPostIdsResponse.newBuilder();
        for(Post post : all) {
            resp.addPostId(post.getId().toString());
        }

        responseObserver.onNext(resp.build());
        responseObserver.onCompleted();
    }
}