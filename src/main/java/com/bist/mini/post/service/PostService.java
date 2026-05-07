package com.bist.mini.post.service;

import com.bist.mini.common.exception.CustomException;
import com.bist.mini.common.exception.ErrorCode;
import com.bist.mini.post.dao.PostDao;
import com.bist.mini.post.dao.TagDao;
import com.bist.mini.post.dto.PostListResponse;
import com.bist.mini.post.dto.PostRequest;
import com.bist.mini.post.entity.Post;
import com.bist.mini.post.entity.Tag;
import com.bist.mini.attachment.service.AttachmentService;
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

   private final PostDao postDao;
   private final TagDao tagDao;
   private final AttachmentService attachmentService;

   @Transactional
   public Post createPost(Post post, PostRequest postRequest) {
       postDao.insert(post);
       syncPostTags(post.getPostId(), post.getTags());
       String updatedContent = attachmentService.syncPostAttachments(post.getPostId(), postRequest.getTempAttachmentIds(), postRequest.getTempInlineImageIds(), post.getContent());
       if (!updatedContent.equals(post.getContent())) {
           post.setContent(updatedContent);
           postDao.updatePost(post);
       }
       return loadPost(post.getPostId());
   }

//   public List<Post> getPostList() {
//       return postDao.findAll();
//   }

//   public PostPageResponse getPostListWithPagination(int page, int size) {
//       if (page < 0) page = 0;
//       if (size <= 0 || size > 100) size = 10;
//
//       int offset = page * size;
//       List<Post> posts = postDAO.findAllWithPage(offset, size);
//       long totalElements = postDAO.countAll();
//
//       return PostPageResponse.of(posts, page, size, totalElements);
//   }

   public List<Post> getPostListByMember(Long memberId) {
       return postDao.findByMemberId(memberId);
   }

   public Post getPostDetail(Long postId) {
       Post post = postDao.findById(postId);
       if (post == null) {
           throw new CustomException(ErrorCode.ENTITY_NOT_FOUND);
       }
       return post;
   }

   public Post getPostDetailWithViewCount(Long postId, Long memberId) {
       Post post = postDao.findById(postId);
       if (post == null) {
           throw new CustomException(ErrorCode.ENTITY_NOT_FOUND);
       }

       if (!post.getMemberId().equals(memberId)) {
           postDao.updateViewCount(postId);
       }

       return post;
   }

   @Transactional
   public Post updatePost(Post post, PostRequest postRequest) {
       int updated = postDao.updatePost(post);
       if (updated == 0) {
           throw new CustomException(ErrorCode.ENTITY_NOT_FOUND);
       }
       postDao.softDeletePostTagsByPostId(post.getPostId());
       syncPostTags(post.getPostId(), post.getTags());
       String updatedContent = attachmentService.syncPostAttachments(post.getPostId(), postRequest.getTempAttachmentIds(), postRequest.getTempInlineImageIds(), post.getContent());
       if (!updatedContent.equals(post.getContent())) {
           post.setContent(updatedContent);
           postDao.updatePost(post);
       }
       return postDao.findById(post.getPostId());
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

   public List<Post> getTempPostList(Long memberId) {
       return postDao.findTempByMemberId(memberId);
   }

   private Post loadPost(Long postId) {
       Post post = postDao.findById(postId);
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

    public List<PostListResponse> getPostList() {
        List<PostListResponse> posts = postDao.selectPostList();

        for (PostListResponse post : posts) {
            List<String> tags = postDao.selectTagNamesByPostId(post.getPostId());
            post.setTags(tags);
        }

        return posts;
    }
}
