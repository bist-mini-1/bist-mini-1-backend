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

    @Operation(summary = "게시글 목록 조회", description = "전체 공개 게시글을 조회합니다.")
    @GetMapping
    public ApiResponse<java.util.List<Post>> getPostList() {
        java.util.List<Post> posts = postService.getPostList();
        return ApiResponse.success(posts);
    }

    @Operation(summary = "게시글 상세 조회", description = "게시글 ID로 단일 게시글을 조회하고 조회수를 증가시킵니다.")
    @GetMapping("/{id}")
    public ApiResponse<Post> getPostDetail(@PathVariable Long id) {
        Post post = postService.getPostDetail(id);
        return ApiResponse.success(post);
    }

    @Operation(summary = "게시글 수정", description = "Authorization 헤더의 JWT에서 memberId를 추출하여 본인의 게시글을 수정합니다.")
    @PutMapping("/{id}")
    public ApiResponse<Post> updatePost(
            @PathVariable Long id,
            HttpServletRequest httpRequest,
            @RequestBody @Valid PostRequest postRequest
    ) {
        String authorization = httpRequest.getHeader("Authorization");
        Long memberId = jwtProvider.getMemberIdFromToken(authorization);
        Post post = postRequest.toEntity(memberId);
        post.setPostId(id);
        Post updated = postService.updatePost(post);
        return ApiResponse.success(updated);
    }

    @Operation(summary = "게시글 삭제", description = "Authorization 헤더의 JWT에서 memberId를 추출하여 본인의 게시글을 삭제합니다.")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deletePost(
            @PathVariable Long id,
            HttpServletRequest httpRequest
    ) {
        String authorization = httpRequest.getHeader("Authorization");
        Long memberId = jwtProvider.getMemberIdFromToken(authorization);
        postService.deletePost(id, memberId);
        return ApiResponse.success(null);
    }

    @Operation(summary = "임시저장 게시글 목록 조회", description = "Authorization 헤더의 JWT에서 memberId를 추출하여 임시저장 게시글을 조회합니다.")
    @GetMapping("/temp/list")
    public ApiResponse<java.util.List<Post>> getTempPostList(
            HttpServletRequest httpRequest
    ) {
        String authorization = httpRequest.getHeader("Authorization");
        Long memberId = jwtProvider.getMemberIdFromToken(authorization);
        java.util.List<Post> tempPosts = postService.getTempPostList(memberId);
        return ApiResponse.success(tempPosts);
    }
}
