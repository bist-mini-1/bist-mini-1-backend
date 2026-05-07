package com.bist.mini.post.service;

import com.bist.mini.post.dao.PostQueryDao;
import com.bist.mini.post.dto.PostListResponse;
import com.bist.mini.post.dto.PostPageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostQueryService {

    private final PostQueryDao postQueryDao;

    /**
     * 전체 공개 게시글 목록을 페이징 처리하여 조회합니다.
     */
    public PostPageResponse getPostList(int page, int size) {
        int offset = (page - 1) * size;

        List<PostListResponse> posts = postQueryDao.selectPostList(offset, size);

        for (PostListResponse post : posts) {
            List<String> tags = postQueryDao.selectTagNamesByPostId(post.getPostId());
            post.setTags(tags);
        }

        long totalCount = postQueryDao.countPostList();
        int totalPages = (int) Math.ceil((double) totalCount / size);

        return PostPageResponse.builder()
                .posts(posts)
                .page(page)
                .size(size)
                .totalCount(totalCount)
                .totalPages(totalPages)
                .build();
    }
}
