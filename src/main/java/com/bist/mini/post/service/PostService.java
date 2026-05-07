package com.bist.mini.post.service;

import com.bist.mini.common.exception.CustomException;
import com.bist.mini.common.exception.ErrorCode;
import com.bist.mini.post.dao.PostDao;
import com.bist.mini.post.dao.TagDao;
import com.bist.mini.post.dto.PostPageResponse;
import com.bist.mini.post.dto.PostRequest;
import com.bist.mini.post.dto.PostResponse;
import com.bist.mini.post.dto.PostTagDto;
import com.bist.mini.post.entity.Post;
import com.bist.mini.post.entity.Tag;
import com.bist.mini.attachment.service.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

   private final PostDao postDao;
   private final TagDao tagDao;
   private final AttachmentService attachmentService;

   @Transactional
   public PostResponse createPost(Post post, PostRequest postRequest) {
       postDao.insert(post);
       syncPostTags(post.getPostId(), postRequest.getTags());
       String updatedContent = attachmentService.syncPostAttachments(post.getPostId(), postRequest.getTempAttachmentIds(), postRequest.getTempInlineImageIds(), post.getContent());
       if (!updatedContent.equals(post.getContent())) {
           post.setContent(updatedContent);
           postDao.updatePost(post);
       }
       return loadPostResponse(post.getPostId());
   }

   public List<PostResponse> getPostList() {
       List<Post> posts = postDao.findAll();
       return mapToPostResponses(posts);
   }

   public PostPageResponse getPostListWithPagination(int page, int size) {
       if (page < 0) page = 0;
       if (size <= 0 || size > 100) size = 10;

       int offset = page * size;
       List<Post> posts = postDao.findAllWithPage(offset, size);
       long totalElements = postDao.countAll();

       List<PostResponse> content = mapToPostResponses(posts);
       return PostPageResponse.of(content, page, size, totalElements);
   }

   public List<PostResponse> getPostListByMember(Long memberId) {
       List<Post> posts = postDao.findByMemberId(memberId);
       return mapToPostResponses(posts);
   }

   public PostResponse getPostDetail(Long postId) {
       return loadPostResponse(postId);
   }

   @Transactional
   public PostResponse getPostDetailWithViewCount(Long postId, Long memberId) {
       Post post = postDao.findById(postId);
       if (post == null) {
           throw new CustomException(ErrorCode.ENTITY_NOT_FOUND);
       }

       if (!post.getMemberId().equals(memberId)) {
           postDao.updateViewCount(postId);
       }

       return loadPostResponse(postId);
   }

   @Transactional
   public PostResponse updatePost(Post post, PostRequest postRequest) {
       int updated = postDao.updatePost(post);
       if (updated == 0) {
           throw new CustomException(ErrorCode.ENTITY_NOT_FOUND);
       }
       postDao.softDeletePostTagsByPostId(post.getPostId());
       syncPostTags(post.getPostId(), postRequest.getTags());
       String updatedContent = attachmentService.syncPostAttachments(post.getPostId(), postRequest.getTempAttachmentIds(), postRequest.getTempInlineImageIds(), post.getContent());
       if (!updatedContent.equals(post.getContent())) {
           post.setContent(updatedContent);
           postDao.updatePost(post);
       }
       return loadPostResponse(post.getPostId());
   }

   @Transactional
   public void deletePost(Long postId, Long memberId) {
       Post post = postDao.findById(postId);
       if (post == null || !post.getMemberId().equals(memberId)) {
           throw new CustomException(ErrorCode.ENTITY_NOT_FOUND);
       }

       postDao.softDeleteCommentsByPostId(postId);
       postDao.softDeleteAttachmentsByPostId(postId);
       postDao.softDeletePostLikesByPostId(postId);
       postDao.softDeleteBookmarksByPostId(postId);
       postDao.softDeletePostTagsByPostId(postId);

       int deleted = postDao.softDeletePost(postId, memberId);
       if (deleted == 0) {
           throw new CustomException(ErrorCode.ENTITY_NOT_FOUND);
       }
   }

   @Transactional
   public void incrementViewCount(Long postId) {
       postDao.updateViewCount(postId);
   }

   public List<PostResponse> getTempPostList(Long memberId) {
       List<Post> posts = postDao.findTempByMemberId(memberId);
       return mapToPostResponses(posts);
   }

   private PostResponse loadPostResponse(Long postId) {
       Post post = postDao.findById(postId);
       if (post == null) {
           throw new CustomException(ErrorCode.ENTITY_NOT_FOUND);
       }
       List<Tag> tags = tagDao.findTagsByPostId(postId);
       return PostResponse.of(post, tags);
   }

   private List<PostResponse> mapToPostResponses(List<Post> posts) {
       if (posts == null || posts.isEmpty()) {
           return new ArrayList<>();
       }

       List<Long> postIds = posts.stream().map(Post::getPostId).collect(Collectors.toList());
       List<PostTagDto> postTags = tagDao.findTagsByPostIds(postIds);

       Map<Long, List<Tag>> tagsByPostId = postTags.stream()
               .collect(Collectors.groupingBy(
                       PostTagDto::getPostId,
                       Collectors.mapping(PostTagDto::getTag, Collectors.toList())
               ));

       return posts.stream()
               .map(post -> PostResponse.of(post, tagsByPostId.getOrDefault(post.getPostId(), new ArrayList<>())))
               .collect(Collectors.toList());
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
           postDao.insertPostTag(postId, tagId);
       }
   }

   private Long getOrCreateTagId(String tagName) {
       Tag existing = tagDao.findByName(tagName);
       if (existing != null) {
           return existing.getTagId();
       }

       Tag created = Tag.builder()
             .name(tagName)
             .createdAt(LocalDateTime.now())
             .build();
       tagDao.insert(created);
       return created.getTagId();
   }
}
