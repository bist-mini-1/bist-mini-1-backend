package com.bist.mini.post.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@Schema(description = "Post 페이지 응답 데이터")
public class PostPageResponse {

    @Schema(description = "게시글 목록")
    private List<PostResponse> content;

    @Schema(description = "현재 페이지 (0부터 시작)", example = "0")
    private int currentPage;

    @Schema(description = "한 페이지당 크기", example = "10")
    private int pageSize;

    @Schema(description = "전체 게시글 개수", example = "100")
    private long totalElements;

    @Schema(description = "전체 페이지 수", example = "10")
    private int totalPages;

    @Schema(description = "첫 페이지 여부", example = "true")
    private boolean first;

    @Schema(description = "마지막 페이지 여부", example = "false")
    private boolean last;

    public static PostPageResponse of(List<PostResponse> content, int currentPage, int pageSize, long totalElements) {
        int totalPages = (int) Math.ceil((double) totalElements / pageSize);
        return PostPageResponse.builder()
                .content(content)
                .currentPage(currentPage)
                .pageSize(pageSize)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .first(currentPage == 0)
                .last(currentPage >= totalPages - 1)
                .build();
    }
}
