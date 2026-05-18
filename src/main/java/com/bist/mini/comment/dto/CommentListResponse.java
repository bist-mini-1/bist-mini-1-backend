package com.bist.mini.comment.dto;

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
public class CommentListResponse {

    private List<CommentResponse> comments;

    private int totalCount; // 기존 호환성용 (totalRoots와 동일하게 설정)

    private int totalComments;

    private int totalRoots;

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
