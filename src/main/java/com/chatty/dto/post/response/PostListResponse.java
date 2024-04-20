package com.chatty.dto.post.response;

import com.chatty.entity.post.Post;
import com.chatty.entity.post.PostImage;
import com.chatty.entity.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PostListResponse {

    private Long postId;
    private String title;
    private String content;
    private int viewCount;
    private LocalDateTime createdAt;

    private Long userId;
    private String nickname;
    private String imageUrl;

    private List<String> postImages;

    private long likeCount;

    private long commentCount;

    private boolean isLike;

    private boolean isOwner;

    @Builder
    public PostListResponse(final Long postId, final String title, final String content, final int viewCount, final LocalDateTime createdAt, final Long userId, final String nickname, final String imageUrl, final List<String> postImages, final long likeCount, final long commentCount, final boolean isLike, final boolean isOwner) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.viewCount = viewCount;
        this.createdAt = createdAt;
        this.userId = userId;
        this.nickname = nickname;
        this.imageUrl = imageUrl;
        this.postImages = postImages;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.isLike = isLike;
        this.isOwner = isOwner;
    }

    public static PostListResponse of(final Post post, final User user) {
        return PostListResponse.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .postImages(post.getPostImages().stream()
                        .map(PostImage::getImage)
                        .collect(Collectors.toList()))
                .viewCount(post.getViewCount())
                .createdAt(post.getCreatedAt())
                .userId(post.getUser().getId())
                .nickname(post.getUser().getNickname())
                .imageUrl(post.getUser().getImageUrl())
                .likeCount(post.getPostLikes().size())
                .commentCount(post.getComments().size())
                .isLike(post.getPostLikes().stream()
                        .anyMatch(postLike -> postLike.getUser().getId().equals(user.getId())))
                .isOwner(post.getUser().getId().equals(user.getId()))
                .build();
    }
}
