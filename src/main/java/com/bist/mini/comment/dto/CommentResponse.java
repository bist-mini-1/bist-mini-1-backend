package com.bist.mini.comment.dto;

import com.bist.mini.comment.entity.Comment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "댓글 응답 데이터")
public class CommentResponse {

    @Schema(description = "댓글 ID", example = "1")
    private Long commentId;

    @Schema(description = "게시글 ID", example = "1")
    private Long postId;

    @Schema(description = "작성자 ID", example = "10")
    private Long memberId;

    @Schema(description = "부모 댓글 ID (대댓글인 경우)", example = "null")
    private Long parentId;

    @Schema(description = "댓글 내용", example = "정말 유익한 포스팅이네요!")
    private String content;

    @Schema(description = "삭제 여부 (Y/N)", example = "N")
    private String isDeleted;

    @Schema(description = "등록일시")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시")
    private LocalDateTime updatedAt;

    @Schema(description = "삭제일시")
    private LocalDateTime deletedAt;

    @Schema(description = "작성자 닉네임")
    private String nickname;

    @Schema(description = "작성자 프로필 이미지")
    private String profileImageUrl;

    @Schema(description = "좋아요 수")
    private int likeCount;

    @Schema(description = "현재 사용자 좋아요 여부")
    private boolean isLiked;

    public static CommentResponse from(Comment comment, String nickname, String profileImageUrl, int likeCount, boolean isLiked) {
        if (comment == null) return null;
        return CommentResponse.builder()
                .commentId(comment.getCommentId())
                .postId(comment.getPostId())
                .memberId(comment.getMemberId())
                .parentId(comment.getParentId())
                .content(comment.getContent())
                .isDeleted(comment.getIsDeleted() != null ? comment.getIsDeleted().name() : null)
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .deletedAt(comment.getDeletedAt())
                .nickname(nickname)
                .profileImageUrl(profileImageUrl)
                .likeCount(likeCount)
                .isLiked(isLiked)
                .build();
    }

}
