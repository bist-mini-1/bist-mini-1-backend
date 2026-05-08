package com.bist.mini.post.dao;

import com.bist.mini.post.dto.PostListResponse;
import com.bist.mini.post.dto.PostTagResponse;
import com.bist.mini.post.entity.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 게시글 조회 전용 데이터 접근 객체 (MyBatis Mapper)
 */
@Mapper
public interface PostQueryDao {

    List<Post> findAll();

    List<Post> findByMemberId(Long memberId);

    List<Post> findTempByMemberId(Long memberId);

    Post findById(Long id);

    long countAll();

    List<PostTagResponse> selectTagNamesByPostIds(@Param("postIds") List<Long> postIds);

    List<PostListResponse> selectPostList(
            @Param("offset") int offset,
            @Param("size") int size,
            @Param("memberId") Long memberId
    );

    long countPostList();
}
