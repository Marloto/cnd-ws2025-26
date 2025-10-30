package de.thi.inf.cnd.rest.repository;

import de.thi.inf.cnd.rest.model.Post;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

/**
 * Erlaubt Zugriff auf Datenbankobjekte
 */
public interface PostRepository extends CrudRepository<Post, UUID> {

}
