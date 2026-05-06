package com.bist.mini.post.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.bist.mini.post.entity.Post;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Post 등록 요청 데이터")
public class PostRequest {

   @NotBlank(message = "게시글 제목 작성은 필수입니다.")
   @Size(min = 1, max = 200, message = "제목은 1자 이상 200자 이내로 입력해 주세요.")
   @Schema(description = "게시글 제목", example = "제목작성")
   private String title;

   @NotBlank(message = "게시글 내용 작성은 필수입니다.")
   @Size(min = 1, message = "내용은 1자 이상 입력해 주세요.")
   @Schema(description = "게시글 내용", example = "게시글 내용 작성")
   private String content;

   @NotBlank(message = "공개 여부를 선택해주세요.")
   @Pattern(regexp = "^[YN]$", message = "공개 여부는 Y 또는 N이어야 합니다.")
   @Schema(description = "공개 여부", example = "Y")
   private String is_public;

   @NotBlank(message = "게시글 임시저장 여부를 선택해주세요.")
   @Pattern(regexp = "^[YN]$", message = "임시저장 여부는 Y 또는 N이어야 합니다.")
   @Schema(description = "임시저장 여부", example = "N")
   private String is_temp;

   @Schema(description = "태그 목록", example = "[\"Spring\", \"JPA\"]")
   private List<String> tags;

   @Schema(description = "게시글에 연결할 일반 첨부파일 ID 목록", example = "[101, 102]")
   private List<Long> attachmentIds;

   @Schema(description = "본문에 삽입된 인라인 이미지 ID 목록", example = "[201, 202]")
   private List<Long> inlineImageIds;


   @Schema(description = "썸네일 이미지 URL", example = "https://example.com/thumbnail.jpg")
   private String thumbnail;

   public Post toEntity(Long memberId) {
      return Post.builder()
            .memberId(memberId)
            .title(this.title.trim())
            .content(this.content.trim())
            .viewCount(0L)
            .likeCount(0L)
            .commentCount(0L)
            .isPublic(this.is_public)
            .isTemp(this.is_temp)
            .isDeleted("N")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .tags(this.tags)
            .build();
   }
}
