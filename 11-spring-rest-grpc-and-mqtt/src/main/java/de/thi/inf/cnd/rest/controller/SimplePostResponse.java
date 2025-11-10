package de.thi.inf.cnd.rest.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimplePostResponse {
    private UUID id;
    private String title;
    private String content;
}
