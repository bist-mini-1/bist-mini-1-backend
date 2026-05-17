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

    /** 특정 유저의 공개 게시글만 조회 (타인 프로필 조회용) */
    List<Post> findPublicByMemberId(Long memberId);

    List<Post> findTempByMemberId(Long memberId);

    Post findById(Long id);

    long countAll();

    List<PostTagResponse> selectTagNamesByPostIds(@Param("postIds") List<Long> postIds);

    List<PostListResponse> selectPostList(
            @Param("offset") int offset,
            @Param("size") int size,
            @Param("keyword") String keyword,
            @Param("sort") String sort,
            @Param("memberId") Long memberId
    );

    long countPostList(
            @Param("keyword") String keyword,
            @Param("sort") String sort,
            @Param("memberId") Long memberId
    );

    List<PostListResponse> selectRecommendedPostsByTags(
            @Param("postId") Long postId,
            @Param("limit") int limit,
            @Param("memberId") Long memberId
    );
}
