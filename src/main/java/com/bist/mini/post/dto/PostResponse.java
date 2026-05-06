package com.bist.mini.post.dto;

import com.bist.mini.post.entity.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "게시글 응답 데이터")
public class PostResponse {

    @Schema(description = "게시글 ID", example = "1")
    private Long postId;

    @Schema(description = "작호자 ID", example = "10")
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

    @Schema(description = "썸네일 이미지 URL", example = "https://example.com/thumbnail.jpg")
    private String thumbnail;

    @Schema(description = "좋아요 여부", example = "false")
    private boolean isLiked;

    @Schema(description = "북마크 여부", example = "false")
    private boolean isBookmarked;

    @Schema(description = "생성일시")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시")
    private LocalDateTime updatedAt;

    @Schema(description = "태그 목록", example = "[\"Java\", \"Spring\"]")
    private List<String> tags;

    public static PostResponse from(Post post) {
        if (post == null) return null;
        return PostResponse.builder()
                .postId(post.getPostId())
                .memberId(post.getMemberId())
                .title(post.getTitle())
                .content(post.getContent())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .category(post.getCategory())
                .isPublic(post.getIsPublic())
                .isTemp(post.getIsTemp())
                .thumbnail(post.getThumbnail())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .tags(post.getTags())
                .build();
    }

    public static List<PostResponse> fromList(List<Post> posts) {
        if (posts == null) return List.of();
        return posts.stream()
                .map(PostResponse::from)
                .collect(Collectors.toList());
    }
}
