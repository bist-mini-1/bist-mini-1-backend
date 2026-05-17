package com.bist.mini.post.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostTagResponse {
    private Long postId;
    private String tagName;
}