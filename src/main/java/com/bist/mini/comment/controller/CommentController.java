package com.bist.mini.comment.controller;

import com.bist.mini.comment.dto.CommentRequest;
import com.bist.mini.comment.dto.CommentUpdateRequest;
import com.bist.mini.comment.entity.Comment;
import com.bist.mini.comment.service.CommentService;
import com.bist.mini.common.ApiResponse;
import com.bist.mini.common.exception.CustomException;
import com.bist.mini.common.exception.ErrorCode;
import com.bist.mini.common.jwt.JwtProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 댓글 API 컨트롤러
 */
@Tag(name = "Comment", description = "댓글 API")
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentService commentService;
    private final JwtProvider jwtProvider;

    @Operation(summary = "댓글 등록", description = "새로운 댓글을 등록합니다. (토큰 인증 기반)")
    @PostMapping
    public ApiResponse<Comment> createComment(
            @Parameter(hidden = true) @RequestHeader("Authorization") String token,
            @Valid @RequestBody CommentRequest commentRequest) {
        Long memberId = extractMemberId(token);
        log.debug("댓글 등록 요청: postId={}, memberId={}", commentRequest.getPostId(), memberId);
        Comment created = commentService.createComment(commentRequest, memberId);
        return ApiResponse.success(created);
    }

    @Operation(summary = "게시글별 댓글 목록 조회", description = "특정 게시글의 모든 댓글을 조회합니다.")
    @GetMapping("/post/{postId}")
    public ApiResponse<List<Comment>> getCommentsByPost(
            @Parameter(description = "게시글 ID") @PathVariable("postId") Long postId) {
        List<Comment> comments = commentService.getCommentsByPost(postId);
        return ApiResponse.success(comments);
    }

    @Operation(summary = "댓글 수정", description = "댓글 내용을 수정합니다. (작성자만 가능)")
    @PutMapping("/{commentId}")
    public ApiResponse<Comment> updateComment(
            @Parameter(hidden = true) @RequestHeader("Authorization") String token,
            @Parameter(description = "댓글 ID") @PathVariable("commentId") Long commentId,
            @Valid @RequestBody CommentUpdateRequest updateRequest) {
        Long memberId = extractMemberId(token);
        Comment updated = commentService.updateComment(commentId, updateRequest, memberId);
        return ApiResponse.success(updated);
    }

    @Operation(summary = "댓글 삭제", description = "댓글을 삭제 처리합니다. 자식 댓글이 있는 경우 함께 삭제 목록에 포함되어 반환됩니다.")
    @DeleteMapping("/{commentId}")
    public ApiResponse<List<Comment>> deleteComment(
            @Parameter(hidden = true) @RequestHeader("Authorization") String token,
            @Parameter(description = "댓글 ID") @PathVariable("commentId") Long commentId) {
        Long memberId = extractMemberId(token);
        List<Comment> deletedList = commentService.deleteComment(commentId, memberId);
        return ApiResponse.success(deletedList);
    }

    /**
     * 헤더에서 Bearer 토큰을 제외하고 memberId를 추출하는 헬퍼 메서드
     */
    private Long extractMemberId(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            try {
                return jwtProvider.getMemberIdFromToken(token.substring(7));
            } catch (Exception e) {
                throw new CustomException("유효하지 않은 인증 토큰입니다.", ErrorCode.SAMPLE_ERROR);
            }
        }
        throw new CustomException("인증 헤더가 누락되었거나 형식이 올바르지 않습니다.", ErrorCode.SAMPLE_ERROR);
    }
}
