package com.bist.mini.post.entity;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "게시글 엔티티")
public class Post {

    @Schema(description = "게시글 ID", example = "1")
    private Long postId;

    @Schema(description = "작성자 ID", example = "10")
    private Long memberId;

    @Schema(description = "게시글 제목", example = "제목입니다")
    private String title;

    @Schema(description = "게시글 내용", example = "내용입니다")
    private String content;

    @Schema(description = "조회수", example = "0")
    private Long viewCount;

    @Schema(description = "좋아요 수", example = "0")
    private Long likeCount;

    @Schema(description = "댓글 수", example = "0")
    private Long commentCount;

    @Schema(description = "게시글 카테고리", example = "기술")
    private String category;

    @Schema(description = "공개 여부 (Y/N)", example = "Y")
    private String isPublic;

    @Schema(description = "임시저장 여부 (Y/N)", example = "N")
    private String isTemp;

    @Schema(description = "삭제 여부 (Y/N)", example = "N")
    private String isDeleted;

    @Schema(description = "썸네일 이미지 URL", example = "https://example.com/thumbnail.jpg")
    private String thumbnail;

    @Schema(description = "생성일시")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시")
    private LocalDateTime updatedAt;

    @Schema(description = "삭제일시")
    private LocalDateTime deletedAt;

    @Schema(description = "태그 목록", example = "[\"Java\", \"Spring\"]")
    private List<String> tags;

}
