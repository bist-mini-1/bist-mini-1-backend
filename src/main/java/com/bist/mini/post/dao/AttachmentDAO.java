package com.bist.mini.post.dao;

import com.bist.mini.post.entity.Attachments;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AttachmentDAO {

    int insert(Attachments attachment);

    List<Attachments> findByIds(@Param("ids") List<Long> ids);

    List<Attachments> findActiveByPostId(@Param("postId") Long postId);

    int bindToPost(@Param("postId") Long postId, @Param("ids") List<Long> ids);

    int softDeleteByIds(@Param("ids") List<Long> ids);
}