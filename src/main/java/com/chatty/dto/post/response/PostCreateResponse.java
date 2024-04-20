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
public class PostCreateResponse {

    private Long id;

    private String title;

    private String content;

    private Long userId;

    private String nickname;

    private String profileImage;

    private List<String> postImages;

    private int viewCount;

    @Builder
    public PostCreateResponse(final Long id, final String title, final String content, final Long userId, final String nickname, final String profileImage, final List<String> postImages, final int viewCount) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.postImages = postImages;
        this.viewCount = viewCount;
    }

    public static PostCreateResponse of(final Post post, final User user) {
        return PostCreateResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .postImages(post.getPostImages().stream()
                        .map(PostImage::getImage)
                        .collect(Collectors.toList()))
                .nickname(user.getNickname())
                .userId(user.getId())
                .profileImage(user.getImageUrl())
                .viewCount(post.getViewCount())
                .build();
    }
}
