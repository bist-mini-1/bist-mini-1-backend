package com.bist.mini.post.controller;

import com.bist.mini.common.ApiResponse;
import com.bist.mini.common.annotation.LoginMember;
import com.bist.mini.post.dto.PostPageResponse;
import com.bist.mini.post.dto.PostRequest;
import com.bist.mini.post.dto.PostResponse;
import com.bist.mini.post.entity.Post;
import com.bist.mini.post.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 게시글 API 컨트롤러
 */
@Slf4j
@Tag(name = "Post", description = "게시글 관리 API")
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @Operation(summary = "게시글 작성", description = "새로운 게시글을 등록합니다. (토큰 인증 기반)")
    @PostMapping
    public ApiResponse<PostResponse> createPost(
            @LoginMember Long memberId,
            @Valid @RequestBody PostRequest postRequest) {
        log.debug("게시글 등록 요청: memberId={}, title={}", memberId, postRequest.getTitle());
        Post post = postRequest.toEntity(memberId);
        Post created = postService.createPost(post, postRequest);
        return ApiResponse.success(postService.convertToResponse(created, memberId));
    }

    @Operation(summary = "게시글 목록 조회", description = "전체 공개 게시글 목록을 페이징 처리하여 조회합니다.")
    @GetMapping
    public ApiResponse<PostPageResponse> getPostList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(postService.getPostList(page, size));
    }

    @Operation(summary = "게시글 상세 조회", description = "게시글 ID로 단일 게시글을 상세 조회합니다.")
    @GetMapping("/{postId}")
    public ApiResponse<PostResponse> getPostDetail(
            @Parameter(description = "게시글 ID") @PathVariable("postId") Long postId,
            @LoginMember(required = false) Long memberId) {
        Post post = postService.getPostDetailWithViewCount(postId, memberId);
        return ApiResponse.success(postService.convertToResponse(post, memberId));
    }

    @Operation(summary = "게시글 수정", description = "게시글 내용을 수정합니다. (작성자만 가능)")
    @PutMapping("/{postId}")
    public ApiResponse<PostResponse> updatePost(
            @Parameter(description = "게시글 ID") @PathVariable("postId") Long postId,
            @LoginMember Long memberId,
            @Valid @RequestBody PostRequest postRequest) {
        log.debug("게시글 수정 요청: postId={}, memberId={}", postId, memberId);
        Post post = postRequest.toEntity(memberId);
        post.setPostId(postId);
        Post updated = postService.updatePost(post, postRequest);
        return ApiResponse.success(postService.convertToResponse(updated, memberId));
    }

    @Operation(summary = "게시글 삭제", description = "게시글을 삭제 처리합니다. (작성자만 가능)")
    @DeleteMapping("/{postId}")
    public ApiResponse<Void> deletePost(
            @Parameter(description = "게시글 ID") @PathVariable("postId") Long postId,
            @LoginMember Long memberId) {
        log.debug("게시글 삭제 요청: postId={}, memberId={}", postId, memberId);
        postService.deletePost(postId, memberId);
        return ApiResponse.success(null);
    }

    @Operation(summary = "임시저장 게시글 목록 조회", description = "본인이 작성한 임시저장 게시글 목록을 조회합니다.")
    @GetMapping("/temp/list")
    public ApiResponse<List<PostResponse>> getTempPostList(
            @LoginMember Long memberId) {
        List<Post> tempPosts = postService.getTempPostList(memberId);
        return ApiResponse.success(postService.convertToResponses(tempPosts, memberId));
    }
}