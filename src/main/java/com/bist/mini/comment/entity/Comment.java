package com.bist.mini.comment.entity;

import com.bist.mini.common.enums.DeleteStatus;
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
public class Comment {

    private Long commentId;

    private Long postId;

    private Long memberId;

    private Long parentId;

    private String content;

    private DeleteStatus isDeleted;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    // JOIN을 통해 채워질 필드 (DB 테이블에는 없음)
    private String nickname;

    /** 프로필 이미지 URL (전달용) */
    private String profileImageUrl;

    /**
     * 삭제 여부 확인
     */
    public boolean isDeleted() {
        return DeleteStatus.Y.equals(this.isDeleted);
    }

}
