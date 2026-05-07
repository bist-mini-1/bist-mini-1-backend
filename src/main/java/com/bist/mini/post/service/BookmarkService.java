package com.bist.mini.post.service;

import com.bist.mini.common.exception.CustomException;
import com.bist.mini.common.exception.ErrorCode;
import com.bist.mini.post.dao.BookmarkDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookmarkService {

    private final BookmarkDao bookmarkDao;

    @Transactional
    public boolean toggleBookmark(Long postId, Long memberId) {
        if (memberId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        
        int count = bookmarkDao.countBookmark(postId, memberId);
        if (count > 0) {
            bookmarkDao.deleteBookmark(postId, memberId);
            return false;
        } else {
            bookmarkDao.insertBookmark(postId, memberId);
            return true;
        }
    }

    public boolean isBookmarked(Long postId, Long memberId) {
        if (memberId == null)
            return false;
        return bookmarkDao.countBookmark(postId, memberId) > 0;
    }
}
