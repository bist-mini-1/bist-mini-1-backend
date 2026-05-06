package com.bist.mini.post.dto;

import java.time.LocalDateTime;

import com.bist.mini.post.entity.Post;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Post 등록 요청 데이터")
public class PostRequest {

   // @NotBlank(message = "내용(testStr)은 필수입니다.")
   // @Size(max = 100, message = "내용은 100자 이내로 입력해 주세요.")
   // @Schema(description = "테스트 문자열", example = "안녕하세요")
   // private String testStr;
   @NotBlank(message = "게시글 제목 작성은 필수입니다.")
   @Size(max = 200, message = "제목은 200자 이내로 입력해 주세요.")
   @Schema(description = "테스트 Post", example = "제목작성")
   private String title;

   @NotBlank(message = "게시글 내용 작성은 필수입니다.")
   @Schema(description = "테스트 내용", example = "게시글 내용 작성")
   private String content;

   @NotBlank(message = "공개 여부를 선택해주세요.")
   @Schema(description = "공개 여부", example = "Y")
   private String is_public;

   @NotBlank(message = "게시글 임시저장 여부를 선택해주세요.")
   @Schema(description = "임시저장 여부", example = "N")
   private String is_temp;

   public Post toEntity(Long memberId) {
      return Post.builder()
            .memberId(memberId)
            .title(this.title)
            .content(this.content)
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
