package com.chatty.dto.bookmark.response;

import com.chatty.dto.post.response.PostListResponse;
import com.chatty.entity.bookmark.Bookmark;
import com.chatty.entity.post.PostImage;
import com.chatty.entity.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class BookmarkListResponse {

    private Long bookmarkId;

    private PostListResponse postListResponse;

    @Builder
    public BookmarkListResponse(final Long bookmarkId, final PostListResponse postListResponse) {
        this.bookmarkId = bookmarkId;
        this.postListResponse = postListResponse;
    }

    public static BookmarkListResponse of(final Bookmark bookmark, final User user, final boolean isLike) {
        return BookmarkListResponse.builder()
                .bookmarkId(bookmark.getId())
                .postListResponse(PostListResponse.of(bookmark.getPost(), user, isLike))
                .build();
    }

    //    private Long postId;
//    private String content;
//    private int viewCount;
//    private LocalDateTime createdAt;
//
//    private Long userId;
//    private String nickname;
//    private String imageUrl;
//
//    private List<String> postImages;
//
//    private long likeCount;
//
//    private long commentCount;
//
//    private boolean isLike;
//
//    private boolean isOwner;
//
//    private boolean isBookmark;
//
//    @Builder
//    public BookmarkListResponse(final Long bookmarkId, final Long postId, final String content, final int viewCount, final LocalDateTime createdAt, final Long userId, final String nickname, final String imageUrl, final List<String> postImages, final long likeCount, final long commentCount, final boolean isLike, final boolean isOwner, final boolean isBookmark) {
//        this.bookmarkId = bookmarkId;
//        this.postId = postId;
//        this.content = content;
//        this.viewCount = viewCount;
//        this.createdAt = createdAt;
//        this.userId = userId;
//        this.nickname = nickname;
//        this.imageUrl = imageUrl;
//        this.postImages = postImages;
//        this.likeCount = likeCount;
//        this.commentCount = commentCount;
//        this.isLike = isLike;
//        this.isOwner = isOwner;
//        this.isBookmark = isBookmark;
//    }
//
//    public static BookmarkListResponse of(final Bookmark bookmark, final User user) {
//        return BookmarkListResponse.builder()
//                .bookmarkId(bookmark.getId())
//                .postId(bookmark.getPost().getId())
//                .content(bookmark.getPost().getContent())
//                .postImages(bookmark.getPost().getPostImages().stream()
//                        .map(PostImage::getImage)
//                        .toList())
//                .viewCount(bookmark.getPost().getViewCount())
//                .createdAt(bookmark.getPost().getCreatedAt())
//                .userId(bookmark.getPost().getUser().getId())
//                .build();
//    }
}
