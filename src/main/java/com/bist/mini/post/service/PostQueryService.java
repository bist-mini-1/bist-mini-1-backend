package com.bist.mini.post.service;

import com.bist.mini.post.dao.PostQueryDao;
import com.bist.mini.post.dto.PostListResponse;
import com.bist.mini.post.dto.PostPageResponse;
import com.bist.mini.post.dto.PostTagResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostQueryService {

    private final PostQueryDao postQueryDao;

    /**
     * 전체 공개 게시글 목록을 페이징 처리하여 조회합니다.
     */
    public PostPageResponse getPostList(int page, int size, Long memberId) {
        int offset = (page - 1) * size;

        List<PostListResponse> posts = postQueryDao.selectPostList(offset, size, memberId);

        if (!posts.isEmpty()) {
            List<Long> postIds = posts.stream()
                    .map(PostListResponse::getPostId)
                    .toList();

            List<PostTagResponse> postTags = postQueryDao.selectTagNamesByPostIds(postIds);

            Map<Long, List<String>> tagMap = postTags.stream()
                    .collect(Collectors.groupingBy(
                            PostTagResponse::getPostId,
                            Collectors.mapping(PostTagResponse::getTagName, Collectors.toList())
                    ));

            for (PostListResponse post : posts) {
                post.setTags(tagMap.getOrDefault(post.getPostId(), List.of()));
            }
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
