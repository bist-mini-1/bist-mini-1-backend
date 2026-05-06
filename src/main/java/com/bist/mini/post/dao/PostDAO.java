package com.bist.mini.post.dao;

import com.bist.mini.post.entity.Post;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* Post 데이터 접근 객체 (MyBatis Mapper)
*/
@Mapper
public interface PostDAO {

   List<Post> findAll();

   Post findById(Long id);

   void insert(Post sample);

}
