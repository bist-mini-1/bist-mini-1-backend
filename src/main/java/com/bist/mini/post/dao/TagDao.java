package com.bist.mini.post.dao;

import com.bist.mini.post.entity.Tag;
import com.bist.mini.post.dto.PostTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface TagDao {

    Tag findByName(@Param("name") String name);

    Tag findById(@Param("tagId") Long tagId);

    int insert(Tag tag);

    List<Tag> findTagsByPostId(@Param("postId") Long postId);

    List<PostTag> findTagsByPostIds(@Param("postIds") List<Long> postIds);
}
