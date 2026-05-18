package com.bist.mini.comment.controller;

import com.bist.mini.comment.dto.CommentRequest;
import com.bist.mini.comment.dto.CommentResponse;
import com.bist.mini.comment.dto.CommentUpdateRequest;
import com.bist.mini.comment.dto.CommentListResponse;
import com.bist.mini.comment.entity.Comment;
import com.bist.mini.comment.service.CommentService;
import com.bist.mini.common.ApiResponse;
import com.bist.mini.common.annotation.LoginMember;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 댓글 API 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ApiResponse<CommentResponse> createComment(
            @LoginMember Long memberId,
            @Valid @RequestBody CommentRequest commentRequest) {
        log.debug("댓글 등록 요청: postId={}, memberId={}", commentRequest.getPostId(), memberId);
        Comment comment = commentRequest.toEntity(memberId);
        commentService.createComment(comment);
        // 등록 후 최신 댓글 목록(1페이지) 반환
        CommentListResponse response = commentService.getCommentsByPost(comment.getPostId(), memberId, 1, 10);
        return ApiResponse.success(response.getComments().stream()
                .filter(c -> c.getContent().equals(comment.getContent()))
                .findFirst().orElse(null));
    }

    @GetMapping("/post/{postId}")
    public ApiResponse<CommentListResponse> getCommentsByPost(
            @LoginMember(required = false) Long memberId,
            @PathVariable("postId") Long postId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        CommentListResponse response = commentService.getCommentsByPost(postId, memberId, page, size);
        return ApiResponse.success(response);
    }

    @PostMapping("/{commentId}/like")
    public ApiResponse<Boolean> toggleLike(
            @LoginMember Long memberId,
            @PathVariable("commentId") Long commentId) {
        boolean isLiked = commentService.toggleLike(commentId, memberId);
        return ApiResponse.success(isLiked);
    }

    @PutMapping("/{commentId}")
    public ApiResponse<CommentResponse> updateComment(
            @LoginMember Long memberId,
            @PathVariable("commentId") Long commentId,
            @Valid @RequestBody CommentUpdateRequest updateRequest) {
        Comment updated = commentService.updateComment(commentId, updateRequest, memberId);
        // 업데이트된 정보 다시 조회 (1페이지 기준)
        CommentListResponse responseList = commentService.getCommentsByPost(updated.getPostId(), memberId, 1, 10);
        CommentResponse response = responseList.getComments().stream()
                .filter(c -> c.getCommentId().equals(commentId))
                .findFirst().orElse(null);
        return ApiResponse.success(response);
    }

    @DeleteMapping("/{commentId}")
    public ApiResponse<Void> deleteComment(
            @LoginMember Long memberId,
            @PathVariable("commentId") Long commentId) {
        commentService.deleteComment(commentId, memberId);
        return ApiResponse.success(null);
    }

    @GetMapping("/{commentId}/mine")
    public ApiResponse<Boolean> isMyComment(
            @PathVariable("commentId") Long commentId,
            @LoginMember Long memberId) {
        log.debug("댓글 작성자 확인 요청: commentId={}, memberId={}", commentId, memberId);
        return ApiResponse.success(commentService.isMyComment(commentId, memberId));
    }
}
