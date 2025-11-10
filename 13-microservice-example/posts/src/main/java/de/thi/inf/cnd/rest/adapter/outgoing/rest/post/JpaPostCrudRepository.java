package de.thi.inf.cnd.rest.adapter.outgoing.rest.post;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface JpaPostCrudRepository extends CrudRepository<PostEntity, UUID> {
}
