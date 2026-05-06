package com.bist.mini.post.controller;

import com.bist.mini.common.ApiResponse;
import com.bist.mini.common.jwt.JwtProvider;
import com.bist.mini.post.entity.Post;
import com.bist.mini.post.service.PostService;
import com.bist.mini.post.dto.PostRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

/**
 * Post API 컨트롤러
 */
@Tag(name = "Post", description = "Post 관리 API")
@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final JwtProvider jwtProvider;

    @Operation(summary = "게시글 작성", description = "Authorization 헤더의 JWT에서 memberId를 추출하여 게시글을 작성합니다.")
    @PostMapping
    public ApiResponse<Post> createPost(
            HttpServletRequest httpRequest,
            @RequestBody @Valid PostRequest postRequest
    ) {
        String authorization = httpRequest.getHeader("Authorization");
        Long memberId = jwtProvider.getMemberIdFromToken(authorization);
        Post created = postService.createPost(postRequest.toEntity(memberId));
        return ApiResponse.success(created);
    }
}
