package com.bist.mini.post.dto;

import com.bist.mini.post.entity.Tag;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostTagDto {
    private Long postId;
    private Tag tag;
}
