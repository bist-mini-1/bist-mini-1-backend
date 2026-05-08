package com.bist.mini.post.controller;

import com.bist.mini.common.annotation.LoginMember;
import com.bist.mini.post.dto.PostPageResponse;
import com.bist.mini.post.service.PostQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 게시글 조회 전용 컨트롤러
 */
@Slf4j
@Tag(name = "Post Query", description = "게시글 조회 API")
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostQueryController {

    private final PostQueryService postQueryService;

    @Operation(summary = "게시글 목록 조회", description = "전체 공개 게시글 목록을 페이징 처리하여 조회합니다.")
    @GetMapping
    public PostPageResponse getPostList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @LoginMember(required = false) Long memberId
    ) {
        return postQueryService.getPostList(page, size, memberId);
    }
}
