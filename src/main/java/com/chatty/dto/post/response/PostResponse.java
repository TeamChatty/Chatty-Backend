package com.chatty.dto.post.response;

import com.chatty.entity.post.Post;
import com.chatty.entity.post.PostImage;
import com.chatty.entity.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PostResponse {

    private Long postId;

    private String content;

    private Long userId;

    private String nickname;

    private String profileImage;

    private List<String> postImages;

    private int viewCount;

    private long likeCount;

    private long commentCount;

    private boolean isLike;

    private boolean isOwner;

    private boolean isBookmark;

    @Builder
    public PostResponse(final Long postId, final String content, final Long userId, final String nickname, final String profileImage, final List<String> postImages, final int viewCount, final long likeCount, final long commentCount, final boolean isLike, final boolean isOwner, final boolean isBookmark) {
        this.postId = postId;
        this.content = content;
        this.userId = userId;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.postImages = postImages;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.isLike = isLike;
        this.isOwner = isOwner;
        this.isBookmark = isBookmark;
    }

    public static PostResponse of(final Post post, final User user, final boolean isLike, final boolean isOwner, final boolean isBookmark) {
        return PostResponse.builder()
                .postId(post.getId())
                .content(post.getContent())
                .postImages(post.getPostImages().stream()
                        .map(PostImage::getImage)
                        .collect(Collectors.toList()))
                .nickname(user.getNickname())
                .userId(user.getId())
                .profileImage(user.getImageUrl())
                .viewCount(post.getViewCount())
                .likeCount(post.getPostLikes().size())
                .commentCount(post.getComments().size())
                .isLike(isLike)
                .isOwner(isOwner)
                .isBookmark(isBookmark)
                .build();
    }
}
