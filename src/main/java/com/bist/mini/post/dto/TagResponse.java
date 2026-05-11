package com.bist.mini.post.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TagResponse {

    private Long tagId;
    private String name;
    private Long postCount;
}