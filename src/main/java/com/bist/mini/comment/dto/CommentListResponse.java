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

    @Schema(description = "전체 댓글 수", example = "15")
    private int totalCount;

    @Schema(description = "베스트 댓글 (좋아요 가장 많은 것)")
    private CommentResponse bestComment;

    public static CommentListResponse of(List<CommentResponse> comments, int totalCount, CommentResponse bestComment) {
        return CommentListResponse.builder()
                .comments(comments)
                .totalCount(totalCount)
                .bestComment(bestComment)
                .build();
    }
}
