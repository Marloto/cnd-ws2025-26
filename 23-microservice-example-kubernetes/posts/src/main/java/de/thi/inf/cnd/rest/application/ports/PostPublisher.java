package de.thi.inf.cnd.rest.application.ports;

import de.thi.inf.cnd.rest.domain.model.Post;
import de.thi.inf.cnd.rest.domain.model.PostInfo;

public interface PostPublisher {
    void publish(PostInfo post);
}
