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


}