package com.bist.mini.attachment.dao;

import com.bist.mini.attachment.entity.Attachment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AttachmentDao {

    int insert(Attachment attachment);

    List<Attachment> findByIds(@Param("ids") List<Long> ids);

    List<Attachment> findActiveByPostId(@Param("postId") Long postId);

    int bindToPost(@Param("postId") Long postId, @Param("ids") List<Long> ids);

    int softDeleteByIds(@Param("ids") List<Long> ids);
}
