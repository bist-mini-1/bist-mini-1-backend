package com.bist.mini.post.controller;

import com.bist.mini.post.dto.TagResponse;
import com.bist.mini.post.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping
    public List<TagResponse> getTagList() {
        return tagService.getTagList();
    }
}