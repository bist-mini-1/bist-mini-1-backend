package com.bist.mini.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 댓글 목록 응답 DTO (페이징 정보 포함)
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "댓글 목록 응답 (전체 개수 포함)")
public class CommentListResponse {

    @Schema(description = "댓글 리스트")
    private List<CommentResponse> comments;

    @Schema(description = "전체 댓글 수 (답글 포함)", example = "15")
    private int totalCount; // 기존 호환성용 (totalRoots와 동일하게 설정)

    @Schema(description = "전체 댓글 수 (답글 포함)", example = "15")
    private int totalComments;

    @Schema(description = "전체 최상위 댓글 수", example = "10")
    private int totalRoots;

    @Schema(description = "베스트 댓글 (좋아요 가장 많은 것)")
    private CommentResponse bestComment;

    public static CommentListResponse of(List<CommentResponse> comments, int totalComments, int totalRoots, CommentResponse bestComment) {
        return CommentListResponse.builder()
                .comments(comments)
                .totalCount(totalRoots) // 기존 페이징 로직 호환을 위해 totalRoots 설정
                .totalComments(totalComments)
                .totalRoots(totalRoots)
                .bestComment(bestComment)
                .build();
    }
}
