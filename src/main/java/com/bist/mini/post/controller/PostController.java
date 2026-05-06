package com.bist.mini.post.controller;

import com.bist.mini.common.ApiResponse;
import com.bist.mini.common.jwt.JwtProvider;
import com.bist.mini.post.entity.Post;
import com.bist.mini.post.service.PostService;
import com.bist.mini.post.service.PostAttachmentService;
import com.bist.mini.post.dto.PostRequest;
import com.bist.mini.post.dto.PostResponse;
import com.bist.mini.post.dto.PostPageResponse;
import com.bist.mini.post.dto.AttachmentUploadResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "Post", description = "Post Management API")
@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;
    private final PostAttachmentService postAttachmentService;
    private final JwtProvider jwtProvider;

    @Operation(summary = "Upload Attachments", description = "Upload files first and get attachmentIds.")
    @PostMapping(value = "/attachments/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<List<AttachmentUploadResponse>> uploadAttachments(
            @Parameter(description = "Upload Type (ATTACHMENT or INLINE_IMAGE)", example = "INLINE_IMAGE")
            @RequestPart("uploadType") String uploadType,
            @RequestPart("files") List<MultipartFile> files
    ) {
        return ApiResponse.success(postAttachmentService.uploadFiles(files, uploadType));
    }

    @Operation(summary = "Create Post", description = "Create a post with JWT authentication (Multipart Form Support).")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<PostResponse> createPost(
            @Parameter(hidden = true) @RequestHeader("Authorization") String token,
            @ModelAttribute @Valid PostRequest postRequest
    ) {
        Long memberId = jwtProvider.getMemberIdFromToken(token);
        Post created = postService.createPost(memberId, postRequest);
        return ApiResponse.success(PostResponse.from(created));
    }

    @Operation(summary = "Get Post List", description = "Get public posts with pagination.")
    @GetMapping
    public ApiResponse<PostPageResponse> getPostList(
            @Parameter(description = "Page number (starts from 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size (max 100)", example = "10")
            @RequestParam(defaultValue = "10") int size
    ) {
        PostPageResponse pageResponse = postService.getPostListWithPagination(page, size);
        return ApiResponse.success(pageResponse);
    }

    @Operation(summary = "Get Post Detail", description = "Get a single post by ID.")
    @GetMapping("/{id}")
    public ApiResponse<PostResponse> getPostDetail(
            @PathVariable Long id,
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false) String token
    ) {
        Long memberId = null;
        if (token != null && !token.isEmpty()) {
            try {
                memberId = jwtProvider.getMemberIdFromToken(token);
            } catch (Exception e) {
                // Ignore invalid token
            }
        }

        Post post = postService.getPostDetail(id, memberId);
        return ApiResponse.success(PostResponse.from(post));
    }

    @Operation(summary = "Update Post", description = "Update own post with JWT authentication (Multipart Form Support).")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<PostResponse> updatePost(
            @PathVariable Long id,
            @Parameter(hidden = true) @RequestHeader("Authorization") String token,
            @ModelAttribute @Valid PostRequest postRequest
    ) {
        Long memberId = jwtProvider.getMemberIdFromToken(token);
        Post updated = postService.updatePost(id, memberId, postRequest);
        return ApiResponse.success(PostResponse.from(updated));
    }

    @Operation(summary = "Delete Post", description = "Delete own post with JWT authentication.")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deletePost(
            @PathVariable Long id,
            @Parameter(hidden = true) @RequestHeader("Authorization") String token
    ) {
        Long memberId = jwtProvider.getMemberIdFromToken(token);
        postService.deletePost(id, memberId);
        return ApiResponse.success(null);
    }

    @Operation(summary = "Get Temp Posts", description = "Get own draft posts with JWT authentication.")
    @GetMapping("/temp/list")
    public ApiResponse<List<PostResponse>> getTempPostList(
            @Parameter(hidden = true) @RequestHeader("Authorization") String token
    ) {
        Long memberId = jwtProvider.getMemberIdFromToken(token);
        List<Post> tempPosts = postService.getTempPostList(memberId);
        return ApiResponse.success(PostResponse.fromList(tempPosts));
    }
}
