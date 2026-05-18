package com.bist.mini.post.controller;

import com.bist.mini.common.ApiResponse;
import com.bist.mini.common.annotation.LoginMember;
import com.bist.mini.post.dto.PostRequest;
import com.bist.mini.post.dto.PostResponse;
import com.bist.mini.post.entity.Post;
import com.bist.mini.post.service.PostService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 게시글 API 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ApiResponse<PostResponse> createPost(
            @LoginMember Long memberId,
            @Valid @RequestBody PostRequest postRequest) {
        log.debug("게시글 등록 요청: memberId={}, title={}", memberId, postRequest.getTitle());
        Post post = postRequest.toEntity(memberId);
        Post created = postService.createPost(post, postRequest);
        return ApiResponse.success(postService.convertToResponse(created, memberId));
    }


    @GetMapping("/{postId}")
    public ApiResponse<PostResponse> getPostDetail(
            @PathVariable("postId") Long postId,
            @LoginMember(required = false) Long memberId) {
        Post post = postService.getPostDetailWithViewCount(postId, memberId);
        return ApiResponse.success(postService.convertToResponse(post, memberId));
    }

    @GetMapping("/{postId}/mine")
    public ApiResponse<Boolean> isMyPost(
            @PathVariable("postId") Long postId,
            @LoginMember Long memberId) {
        log.debug("게시글 작성자 확인 요청: postId={}, memberId={}", postId, memberId);
        return ApiResponse.success(postService.isMyPost(postId, memberId));
    }

    @PutMapping("/{postId}")
    public ApiResponse<PostResponse> updatePost(
            @PathVariable("postId") Long postId,
            @LoginMember Long memberId,
            @Valid @RequestBody PostRequest postRequest) {
        log.debug("게시글 수정 요청: postId={}, memberId={}", postId, memberId);
        Post post = postRequest.toEntity(memberId);
        post.setPostId(postId);
        Post updated = postService.updatePost(post, postRequest);
        return ApiResponse.success(postService.convertToResponse(updated, memberId));
    }

    @DeleteMapping("/{postId}")
    public ApiResponse<Void> deletePost(
            @PathVariable("postId") Long postId,
            @LoginMember Long memberId) {
        log.debug("게시글 삭제 요청: postId={}, memberId={}", postId, memberId);
        postService.deletePost(postId, memberId);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/temp/{postId}")
    public ApiResponse<Void> deleteTempPost(
            @PathVariable("postId") Long postId,
            @LoginMember Long memberId) {
        log.debug("임시저장 게시글 삭제 요청: postId={}, memberId={}", postId, memberId);
        postService.deleteTempPost(postId, memberId);
        return ApiResponse.success(null);
    }

    @GetMapping("/temp/list")
    public ApiResponse<List<PostResponse>> getTempPostList(
            @LoginMember Long memberId) {
        List<Post> tempPosts = postService.getTempPostList(memberId);
        return ApiResponse.success(postService.convertToResponses(tempPosts, memberId));
    }

    @GetMapping("/temp/{postId}")
    public ApiResponse<PostResponse> getTempPostDetail(
            @PathVariable("postId") Long postId,
            @LoginMember Long memberId) {
        Post tempPost = postService.getTempPostDetail(postId, memberId);
        return ApiResponse.success(postService.convertToResponse(tempPost, memberId));
    }
}