package com.bist.mini.post.dto;

import com.bist.mini.post.entity.Post;
import com.bist.mini.post.entity.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@Schema(description = "게시글 조회 응답 데이터")
public class PostResponse {

    @Schema(description = "게시글 ID", example = "1")
    private Long postId;

    @Schema(description = "작성자 회원 ID", example = "1")
    private Long memberId;

    @Schema(description = "게시글 제목", example = "게시글 제목입니다.")
    private String title;

    @Schema(description = "게시글 내용", example = "게시글 내용입니다.")
    private String content;

    @Schema(description = "조회수", example = "150")
    private Long viewCount;

    @Schema(description = "좋아요 수", example = "20")
    private Long likeCount;

    @Schema(description = "댓글 수", example = "5")
    private Long commentCount;

    @Schema(description = "공개 여부", example = "Y")
    private String isPublic;

    @Schema(description = "임시저장 여부", example = "N")
    private String isTemp;

    @Schema(description = "작성일시")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시")
    private LocalDateTime updatedAt;

    @Schema(description = "태그 목록")
    private List<Tag> tags;

    public static PostResponse of(Post post, List<Tag> tags) {
        return PostResponse.builder()
                .postId(post.getPostId())
                .memberId(post.getMemberId())
                .title(post.getTitle())
                .content(post.getContent())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .isPublic(post.getIsPublic())
                .isTemp(post.getIsTemp())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .tags(tags)
                .build();
    }
}
