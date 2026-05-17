package com.bist.mini.post.service;

import com.bist.mini.common.exception.CustomException;
import com.bist.mini.common.exception.ErrorCode;
import com.bist.mini.post.dao.LikeDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeService {

    private final LikeDao likeDao;

    @Transactional
    public boolean toggleLike(Long postId, Long memberId) {
        if (memberId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        
        int count = likeDao.countLike(postId, memberId);
        if (count > 0) {
            likeDao.deleteLike(postId, memberId);
            likeDao.updateLikeCount(postId, -1);
            return false;
        } else {
            likeDao.insertLike(postId, memberId);
            likeDao.updateLikeCount(postId, 1);
            return true;
        }
    }

    public boolean isLiked(Long postId, Long memberId) {
        if (memberId == null)
            return false;
        return likeDao.countLike(postId, memberId) > 0;
    }
}
