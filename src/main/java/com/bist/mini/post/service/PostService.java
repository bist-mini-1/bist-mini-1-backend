package com.bist.mini.post.service;

import com.bist.mini.common.exception.CustomException;
import com.bist.mini.common.exception.ErrorCode;
import com.bist.mini.post.dao.PostDao;
import com.bist.mini.post.dao.TagDao;
import com.bist.mini.post.dto.PostPageResponse;
import com.bist.mini.post.dto.PostListResponse;
import com.bist.mini.post.dto.PostRequest;
import com.bist.mini.post.dto.PostResponse;
import com.bist.mini.post.dto.PostTagDto;
import com.bist.mini.post.entity.Post;
import com.bist.mini.post.entity.Tag;
import com.bist.mini.attachment.service.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostDao postDao;
    private final TagDao tagDao;
    private final LikeService likeService;
    private final BookmarkService bookmarkService;
    private final AttachmentService attachmentService;

    @Transactional
    public Post createPost(Post post, PostRequest postRequest) {
        validateMember(post.getMemberId());

        postDao.insert(post);
        syncPostTags(post.getPostId(), postRequest.getTags());
        String updatedContent = attachmentService.syncPostAttachments(post.getPostId(),
                postRequest.getTempAttachmentIds(), postRequest.getTempInlineImageIds(), post.getContent());
        if (!updatedContent.equals(post.getContent())) {
            post.setContent(updatedContent);
            postDao.updatePost(post);
        }
        return postDao.findById(post.getPostId());
    }

    public PostPageResponse getPostList(int page, int size) {
        int offset = (page - 1) * size;

        List<PostListResponse> posts = postDao.selectPostList(offset, size);

        for (PostListResponse post : posts) {
            List<String> tags = postDao.selectTagNamesByPostId(post.getPostId());
            post.setTags(tags);
        }

        long totalCount = postDao.countPostList();
        int totalPages = (int) Math.ceil((double) totalCount / size);

        return PostPageResponse.builder()
                .posts(posts)
                .page(page)
                .size(size)
                .totalCount(totalCount)
                .totalPages(totalPages)
                .build();
    }

    public List<Post> getPostList() {
        return postDao.findAll();
    }

    public List<Post> getPostListByMember(Long memberId) {
        return postDao.findByMemberId(memberId);
    }

    public Post getPostById(Long postId) {
        Post post = postDao.findById(postId);
        if (post == null) {
            throw new CustomException(ErrorCode.ENTITY_NOT_FOUND);
        }
        return post;
    }

    @Transactional
    public Post getPostDetailWithViewCount(Long postId, Long memberId) {
        Post post = postDao.findById(postId);
        if (post == null) {
            throw new CustomException(ErrorCode.ENTITY_NOT_FOUND);
        }

        if (memberId == null || !post.getMemberId().equals(memberId)) {
            postDao.updateViewCount(postId);
        }

        return postDao.findById(postId);
    }

    @Transactional
    public Post updatePost(Post post, PostRequest postRequest) {
        validateMember(post.getMemberId());

        Post existingPost = postDao.findById(post.getPostId());
        if (existingPost == null) {
            throw new CustomException(ErrorCode.ENTITY_NOT_FOUND);
        }

        // 권한 체크
        if (!existingPost.getMemberId().equals(post.getMemberId())) {
            throw new CustomException(ErrorCode.POST_ACCESS_DENIED);
        }

        int updated = postDao.updatePost(post);
        if (updated == 0) {
            throw new CustomException(ErrorCode.ENTITY_NOT_FOUND);
        }

        postDao.softDeletePostTagsByPostId(post.getPostId());
        syncPostTags(post.getPostId(), postRequest.getTags());
        String updatedContent = attachmentService.syncPostAttachments(post.getPostId(),
                postRequest.getTempAttachmentIds(), postRequest.getTempInlineImageIds(), post.getContent());
        if (!updatedContent.equals(post.getContent())) {
            post.setContent(updatedContent);
            postDao.updatePost(post);
        }
        return postDao.findById(post.getPostId());
    }

    @Transactional
    public void deletePost(Long postId, Long memberId) {
        validateMember(memberId);

        Post post = postDao.findById(postId);
        if (post == null) {
            throw new CustomException(ErrorCode.ENTITY_NOT_FOUND);
        }

        // 권한 체크
        if (!post.getMemberId().equals(memberId)) {
            throw new CustomException(ErrorCode.POST_ACCESS_DENIED);
        }

        postDao.softDeleteCommentsByPostId(postId);
        postDao.softDeleteAttachmentsByPostId(postId);
        postDao.softDeletePostLikesByPostId(postId);
        postDao.softDeleteBookmarksByPostId(postId);
        postDao.softDeletePostTagsByPostId(postId);

        int deleted = postDao.softDeletePost(postId, memberId);
        if (deleted == 0) {
            throw new CustomException(ErrorCode.ENTITY_NOT_FOUND);
        }
    }

    @Transactional
    public void incrementViewCount(Long postId) {
        postDao.updateViewCount(postId);
    }

    public List<Post> getTempPostList(Long memberId) {
        validateMember(memberId);
        return postDao.findTempByMemberId(memberId);
    }

    public PostResponse convertToResponse(Post post, Long memberId) {
        if (post == null) {
            return null;
        }
        List<Tag> tags = tagDao.findTagsByPostId(post.getPostId());
        boolean isLiked = likeService.isLiked(post.getPostId(), memberId);
        boolean isBookmarked = bookmarkService.isBookmarked(post.getPostId(), memberId);

        return PostResponse.of(post, tags, isLiked, isBookmarked);
    }

    public List<PostResponse> convertToResponses(List<Post> posts, Long memberId) {
        if (posts == null || posts.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> postIds = posts.stream().map(Post::getPostId).collect(Collectors.toList());
        List<PostTagDto> postTags = tagDao.findTagsByPostIds(postIds);

        Map<Long, List<Tag>> tagsByPostId = postTags.stream()
                .collect(Collectors.groupingBy(
                        PostTagDto::getPostId,
                        Collectors.mapping(PostTagDto::getTag, Collectors.toList())));

        return posts.stream()
                .map(post -> {
                    List<Tag> tags = tagsByPostId.getOrDefault(post.getPostId(), new ArrayList<>());
                    boolean isLiked = likeService.isLiked(post.getPostId(), memberId);
                    boolean isBookmarked = bookmarkService.isBookmarked(post.getPostId(), memberId);
                    return PostResponse.of(post, tags, isLiked, isBookmarked);
                })
                .collect(Collectors.toList());
    }

    private void syncPostTags(Long postId, List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return;
        }

        Set<String> normalizedTags = new LinkedHashSet<>();
        for (String tagName : tags) {
            if (tagName == null) {
                continue;
            }
            String normalized = tagName.trim();
            if (!normalized.isEmpty()) {
                normalizedTags.add(normalized);
            }
        }

        for (String tagName : normalizedTags) {
            Long tagId = getOrCreateTagId(tagName);
            postDao.insertPostTag(postId, tagId);
        }
    }

    private Long getOrCreateTagId(String tagName) {
        Tag existing = tagDao.findByName(tagName);
        if (existing != null) {
            return existing.getTagId();
        }

        Tag created = Tag.builder()
                .name(tagName)
                .createdAt(LocalDateTime.now())
                .build();
        tagDao.insert(created);
        return created.getTagId();
    }

    private void validateMember(Long memberId) {
        if (memberId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
    }
}
