package com.bist.mini.comment.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 댓글 엔티티
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "댓글 상세 정보")
public class Comment {

    @Schema(description = "댓글 고유 ID", example = "1")
    private Long commentId;

    @Schema(description = "게시글 ID", example = "1")
    private Long postId;

    @Schema(description = "작성자 ID", example = "1")
    private Long memberId;

    @Schema(description = "부모 댓글 ID (대댓글인 경우)", example = "null")
    private Long parentId;

    @Schema(description = "댓글 내용", example = "정말 유익한 포스팅이네요!")
    private String content;

    @Schema(description = "삭제 여부 (Y/N)", example = "N")
    private String isDeleted;

    @Schema(description = "등록일", example = "2024-05-06T14:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "수정일", example = "2024-05-06T15:00:00")
    private LocalDateTime updatedAt;

    @Schema(description = "삭제일", example = "null")
    private LocalDateTime deletedAt;

}
