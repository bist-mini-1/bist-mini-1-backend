package com.bist.mini.comment.controller;

import com.bist.mini.comment.dto.CommentRequest;
import com.bist.mini.comment.dto.CommentResponse;
import com.bist.mini.comment.dto.CommentUpdateRequest;
import com.bist.mini.comment.dto.CommentListResponse;
import com.bist.mini.comment.entity.Comment;
import com.bist.mini.comment.service.CommentService;
import com.bist.mini.common.ApiResponse;
import com.bist.mini.common.annotation.LoginMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 댓글 API 컨트롤러
 */
@Slf4j
@Tag(name = "Comment", description = "댓글 관리 API")
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "댓글 등록", description = "새로운 댓글을 등록합니다. (토큰 인증 기반)")
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
            @Parameter(description = "게시글 ID") @PathVariable("postId") Long postId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        CommentListResponse response = commentService.getCommentsByPost(postId, memberId, page, size);
        return ApiResponse.success(response);
    }

    @Operation(summary = "댓글 좋아요 토글", description = "댓글에 좋아요를 추가하거나 취소합니다.")
    @PostMapping("/{commentId}/like")
    public ApiResponse<Boolean> toggleLike(
            @LoginMember Long memberId,
            @Parameter(description = "댓글 ID") @PathVariable("commentId") Long commentId) {
        boolean isLiked = commentService.toggleLike(commentId, memberId);
        return ApiResponse.success(isLiked);
    }

    @Operation(summary = "댓글 수정", description = "댓글 내용을 수정합니다. (작성자만 가능)")
    @PutMapping("/{commentId}")
    public ApiResponse<CommentResponse> updateComment(
            @LoginMember Long memberId,
            @Parameter(description = "댓글 ID") @PathVariable("commentId") Long commentId,
            @Valid @RequestBody CommentUpdateRequest updateRequest) {
        Comment updated = commentService.updateComment(commentId, updateRequest, memberId);
        // 업데이트된 정보 다시 조회 (1페이지 기준)
        CommentListResponse responseList = commentService.getCommentsByPost(updated.getPostId(), memberId, 1, 10);
        CommentResponse response = responseList.getComments().stream()
                .filter(c -> c.getCommentId().equals(commentId))
                .findFirst().orElse(null);
        return ApiResponse.success(response);
    }

    @Operation(summary = "댓글 삭제", description = "댓글을 삭제 처리합니다.")
    @DeleteMapping("/{commentId}")
    public ApiResponse<Void> deleteComment(
            @LoginMember Long memberId,
            @Parameter(description = "댓글 ID") @PathVariable("commentId") Long commentId) {
        commentService.deleteComment(commentId, memberId);
        return ApiResponse.success(null);
    }

    @Operation(summary = "내 댓글 여부 확인", description = "현재 로그인한 사용자가 해당 댓글 작성자인지 확인합니다.")
    @GetMapping("/{commentId}/mine")
    public ApiResponse<Boolean> isMyComment(
            @Parameter(description = "댓글 ID") @PathVariable("commentId") Long commentId,
            @LoginMember Long memberId) {
        log.debug("댓글 작성자 확인 요청: commentId={}, memberId={}", commentId, memberId);
        return ApiResponse.success(commentService.isMyComment(commentId, memberId));
    }
}
