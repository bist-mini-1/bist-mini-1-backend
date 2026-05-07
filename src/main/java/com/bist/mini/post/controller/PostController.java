package com.bist.mini.post.controller;

import com.bist.mini.common.ApiResponse;
import com.bist.mini.common.jwt.JwtProvider;
import com.bist.mini.post.entity.Post;
import com.bist.mini.post.service.PostService;
import com.bist.mini.post.dto.PostRequest;
import com.bist.mini.post.dto.PostResponse;
import com.bist.mini.post.dto.PostPageResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.*;
import java.util.List;

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
    public ApiResponse<PostResponse> createPost(
            HttpServletRequest httpRequest,
            @RequestBody @Valid PostRequest postRequest
    ) {
        String authorization = httpRequest.getHeader("Authorization");
        Long memberId = jwtProvider.getMemberIdFromToken(authorization);
        PostResponse created = postService.createPost(postRequest.toEntity(memberId), postRequest);
        return ApiResponse.success(created);
    }

    @Operation(summary = "게시글 목록 조회 (페이지네이션)", description = "전체 공개 게시글을 페이지 단위로 조회합니다.")
    @GetMapping
    public ApiResponse<PostPageResponse> getPostList(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "한 페이지당 크기 (최대 100)", example = "10")
            @RequestParam(defaultValue = "10") int size
    ) {
        PostPageResponse pageResponse = postService.getPostListWithPagination(page, size);
        return ApiResponse.success(pageResponse);
    }

    @Operation(summary = "게시글 상세 조회", description = "게시글 ID로 단일 게시글을 조회합니다. 자신이 아닌 글이면 조회수를 증가시킵니다.")
    @GetMapping("/{id}")
    public ApiResponse<PostResponse> getPostDetail(
            @PathVariable Long id,
            HttpServletRequest httpRequest
    ) {
        Long memberId = null;
        try {
            String authorization = httpRequest.getHeader("Authorization");
            if (authorization != null && !authorization.isEmpty()) {
                memberId = jwtProvider.getMemberIdFromToken(authorization);
            }
        } catch (Exception e) {
            // JWT 파싱 실패 시 계속 진행 (비로그인 사용자)
        }

        PostResponse post;
        if (memberId != null) {
            post = postService.getPostDetailWithViewCount(id, memberId);
        } else {
            post = postService.getPostDetail(id);
            postService.incrementViewCount(id);
        }
        return ApiResponse.success(post);
    }

    @Operation(summary = "게시글 수정", description = "Authorization 헤더의 JWT에서 memberId를 추출하여 본인의 게시글을 수정합니다.")
    @PutMapping("/{id}")
    public ApiResponse<PostResponse> updatePost(
            @PathVariable Long id,
            HttpServletRequest httpRequest,
            @RequestBody @Valid PostRequest postRequest
    ) {
        String authorization = httpRequest.getHeader("Authorization");
        Long memberId = jwtProvider.getMemberIdFromToken(authorization);
        Post post = postRequest.toEntity(memberId);
        post.setPostId(id);
        PostResponse updated = postService.updatePost(post, postRequest);
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
    public ApiResponse<List<PostResponse>> getTempPostList(
            HttpServletRequest httpRequest
    ) {
        String authorization = httpRequest.getHeader("Authorization");
        Long memberId = jwtProvider.getMemberIdFromToken(authorization);
        List<PostResponse> tempPosts = postService.getTempPostList(memberId);
        return ApiResponse.success(tempPosts);
    }
}