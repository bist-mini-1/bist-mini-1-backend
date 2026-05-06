package com.bist.mini.post.service;

import com.bist.mini.common.exception.CustomException;
import com.bist.mini.common.exception.ErrorCode;
import com.bist.mini.post.dao.PostDAO;
import com.bist.mini.post.dao.TagDAO;
import com.bist.mini.post.dto.AttachmentUploadResponse;
import com.bist.mini.post.dto.PostRequest;
import com.bist.mini.post.entity.Post;
import com.bist.mini.post.entity.Tag;
import com.bist.mini.post.dto.PostPageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

   private final PostDAO postDAO;
   private final TagDAO tagDAO;
   private final PostAttachmentService postAttachmentService;

   @Transactional
   public Post createPost(Long memberId, PostRequest postRequest) {
      Post post = postRequest.toEntity(memberId);

      // 직접 업로드된 썸네일 파일 처리
      if (postRequest.getThumbnailFile() != null && !postRequest.getThumbnailFile().isEmpty()) {
         List<AttachmentUploadResponse> uploads = postAttachmentService.uploadFiles(
               List.of(postRequest.getThumbnailFile()), "ATTACHMENT");
         if (!uploads.isEmpty()) {
            post.setThumbnail(uploads.get(0).getFileUrl());
         }
      }

      postDAO.insert(post);
      syncPostTags(post.getPostId(), post.getTags());
      postAttachmentService.syncPostAttachments(post.getPostId(), postRequest);
      return loadPost(post.getPostId());
   }

   public List<Post> getPostList() {
      return postDAO.findAll();
   }

   public PostPageResponse getPostListWithPagination(int page, int size) {
      if (page < 0) page = 0;
      if (size <= 0 || size > 100) size = 10;

      int offset = page * size;
      List<Post> posts = postDAO.findAllWithPage(offset, size);
      long totalElements = postDAO.countAll();

      return PostPageResponse.of(posts, page, size, totalElements);
   }

   public List<Post> getPostListByMember(Long memberId) {
      return postDAO.findByMemberId(memberId);
   }

   public Post getPostDetail(Long postId, Long memberId) {
      Post post = postDAO.findById(postId);
      if (post == null) {
         throw new CustomException(ErrorCode.ENTITY_NOT_FOUND);
      }

      // 작성자가 아닌 경우에만 조회수 증가
      if (memberId == null || !post.getMemberId().equals(memberId)) {
         postDAO.updateViewCount(postId);
         post.setViewCount(post.getViewCount() + 1);
      }

      return post;
   }

   @Transactional
   public Post updatePost(Long postId, Long memberId, PostRequest postRequest) {
      Post post = postRequest.toEntity(memberId);
      post.setPostId(postId);

      // 직접 업로드된 썸네일 파일 처리
      if (postRequest.getThumbnailFile() != null && !postRequest.getThumbnailFile().isEmpty()) {
         List<AttachmentUploadResponse> uploads = postAttachmentService.uploadFiles(
               List.of(postRequest.getThumbnailFile()), "ATTACHMENT");
         if (!uploads.isEmpty()) {
            post.setThumbnail(uploads.get(0).getFileUrl());
         }
      }

      int updated = postDAO.updatePost(post);
      if (updated == 0) {
         throw new CustomException(ErrorCode.ENTITY_NOT_FOUND);
      }
      postDAO.softDeletePostTagsByPostId(postId);
      syncPostTags(postId, post.getTags());
      postAttachmentService.syncPostAttachments(postId, postRequest);
      return postDAO.findById(postId);
   }

   @Transactional
   public void deletePost(Long postId, Long memberId) {
      Post post = postDAO.findById(postId);
      if (post == null || !post.getMemberId().equals(memberId)) {
         throw new CustomException(ErrorCode.ENTITY_NOT_FOUND);
      }

      postDAO.softDeleteCommentsByPostId(postId);
      postDAO.softDeleteAttachmentsByPostId(postId);
      postDAO.softDeletePostLikesByPostId(postId);
      postDAO.softDeleteBookmarksByPostId(postId);
      postDAO.softDeletePostTagsByPostId(postId);

      int deleted = postDAO.softDeletePost(postId, memberId);
      if (deleted == 0) {
         throw new CustomException(ErrorCode.ENTITY_NOT_FOUND);
      }
   }

   @Transactional
   public void incrementViewCount(Long postId) {
      postDAO.updateViewCount(postId);
   }

   public List<Post> getTempPostList(Long memberId) {
      return postDAO.findTempByMemberId(memberId);
   }

   private Post loadPost(Long postId) {
      Post post = postDAO.findById(postId);
      if (post == null) {
         throw new CustomException(ErrorCode.ENTITY_NOT_FOUND);
      }
      return post;
   }

   private void syncPostTags(Long postId, List<String> tags) {
      if (tags == null || tags.isEmpty()) {
         return;
      }

      Set<String> normalizedTags = new LinkedHashSet<>();
      for (String tagName : tags) {
         if (tagName == null) {
            continue;
         }
         String normalized = tagName.trim();
         if (!normalized.isEmpty()) {
            normalizedTags.add(normalized);
         }
      }

      for (String tagName : normalizedTags) {
         Long tagId = getOrCreateTagId(tagName);
         postDAO.insertPostTag(postId, tagId);
      }
   }

   private Long getOrCreateTagId(String tagName) {
      Tag existing = tagDAO.findByName(tagName);
      if (existing != null) {
         return existing.getTagId();
      }

      Tag created = Tag.builder()
            .name(tagName)
            .createdAt(LocalDateTime.now())
            .build();
      tagDAO.insert(created);
      return created.getTagId();
   }
}
