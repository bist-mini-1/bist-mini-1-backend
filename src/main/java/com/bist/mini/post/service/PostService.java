package com.bist.mini.post.service;

import com.bist.mini.common.exception.CustomException;
import com.bist.mini.common.exception.ErrorCode;
import com.bist.mini.post.dao.PostDAO;
import com.bist.mini.post.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

   private final PostDAO postDAO;

   @Transactional
   public Post createPost(Post post) {
       postDAO.insert(post);
       return post;
   }

   public List<Post> getPostList() {
       return postDAO.findAll();
   }

   public List<Post> getPostListByMember(Long memberId) {
       return postDAO.findByMemberId(memberId);
   }

   public Post getPostDetail(Long postId) {
       postDAO.updateViewCount(postId);
       Post post = postDAO.findById(postId);
       if (post == null) {
           throw new CustomException(ErrorCode.ENTITY_NOT_FOUND);
       }
       return post;
   }

   @Transactional
   public Post updatePost(Post post) {
       int updated = postDAO.updatePost(post);
       if (updated == 0) {
           throw new CustomException(ErrorCode.ENTITY_NOT_FOUND);
       }
       return postDAO.findById(post.getPostId());
   }

   @Transactional
   public void deletePost(Long postId, Long memberId) {
       // 게시글 존재 여부 확인
       Post post = postDAO.findById(postId);
       if (post == null || !post.getMemberId().equals(memberId)) {
           throw new CustomException(ErrorCode.ENTITY_NOT_FOUND);
       }

       // 관련 테이블 데이터 소프트 삭제 (재귀적)
       postDAO.softDeleteCommentsByPostId(postId);
       postDAO.softDeleteAttachmentsByPostId(postId);
       postDAO.softDeletePostLikesByPostId(postId);
       postDAO.softDeleteBookmarksByPostId(postId);
       postDAO.softDeletePostTagsByPostId(postId);

       // 게시글 소프트 삭제
       int deleted = postDAO.softDeletePost(postId, memberId);
       if (deleted == 0) {
           throw new CustomException(ErrorCode.ENTITY_NOT_FOUND);
       }
   }

   public List<Post> getTempPostList(Long memberId) {
       return postDAO.findTempByMemberId(memberId);
   }
}

