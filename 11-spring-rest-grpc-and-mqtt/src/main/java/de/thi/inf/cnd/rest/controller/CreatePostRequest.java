package de.thi.inf.cnd.rest.controller;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreatePostRequest {
    private String title;
    private String content;
}
