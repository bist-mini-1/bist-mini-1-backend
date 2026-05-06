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


//    public List<Post> getSampleList() {
//        return postDAO.findAll();
//    }

//    public Post getSampleDetail(Long id) {
//        Post post = postDAO.findById(id);
//        if (post == null) {
//            throw new CustomException("존재하지 않는 샘플 ID입니다.", ErrorCode.POST_ERROR);
//        }
//        return post;
//    }

   @Transactional
      public Post createPost(Post post) {
       postDAO.insert(post);
       return post;
   }
}
