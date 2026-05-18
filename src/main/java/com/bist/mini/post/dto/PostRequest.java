package com.bist.mini.post.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.bist.mini.post.entity.Post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostRequest {

   @NotBlank(message = "게시글 제목 작성은 필수입니다.")
   @Size(min = 1, max = 200, message = "제목은 1자 이상 200자 이내로 입력해 주세요.")
   private String title;

   @NotBlank(message = "게시글 내용 작성은 필수입니다.")
   @Size(min = 1, message = "내용은 1자 이상 입력해 주세요.")
   private String content;

   @NotBlank(message = "공개 여부를 선택해주세요.")
   @Pattern(regexp = "^[YN]$", message = "공개 여부는 Y 또는 N이어야 합니다.")
   private String is_public;

   @NotBlank(message = "게시글 임시저장 여부를 선택해주세요.")
   @Pattern(regexp = "^[YN]$", message = "임시저장 여부는 Y 또는 N이어야 합니다.")
   private String is_temp;

   private List<String> tags;

   private List<String> tempAttachmentIds;

   private List<String> tempInlineImageIds;

   private String thumbnail;

   private Long tempPostId;

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
            .build();
   }
}
