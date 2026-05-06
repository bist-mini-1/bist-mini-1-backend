package com.bist.mini.post.dao;

import com.bist.mini.post.entity.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TagDAO {

    Tag findByName(@Param("name") String name);

    Tag findById(@Param("tagId") Long tagId);

    int insert(Tag tag);
}
