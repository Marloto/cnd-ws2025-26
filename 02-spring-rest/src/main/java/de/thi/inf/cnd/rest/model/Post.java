package de.thi.inf.cnd.rest.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
public class Post {
  @Id
  @Setter(AccessLevel.NONE)
  private UUID id;

  private String title;
  private String content;
  private LocalDateTime date;
  private UUID userRef;

  public Post() {
    this.id = UUID.randomUUID();
  }
}
