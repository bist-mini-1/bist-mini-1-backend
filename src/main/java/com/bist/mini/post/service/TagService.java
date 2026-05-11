package com.bist.mini.post.service;

import com.bist.mini.post.dao.TagDao;
import com.bist.mini.post.dto.TagResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagDao tagDao;

    public List<TagResponse> getTagList() {
        return tagDao.selectTagList();
    }
}