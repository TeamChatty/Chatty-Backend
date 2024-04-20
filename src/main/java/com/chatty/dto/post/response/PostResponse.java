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

    private String title;

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

    @Builder
    public PostResponse(final Long postId, final String title, final String content, final Long userId, final String nickname, final String profileImage, final List<String> postImages, final int viewCount, final long likeCount, final long commentCount, final boolean isLike, final boolean isOwner) {
        this.postId = postId;
        this.title = title;
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
    }

    public static PostResponse of(final Post post, final User user, final boolean isLike, final boolean isOwner) {
        return PostResponse.builder()
                .postId(post.getId())
                .title(post.getTitle())
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
                .build();
    }
}
