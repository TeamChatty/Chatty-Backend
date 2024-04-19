package com.chatty.dto.like.response;

import com.chatty.entity.like.PostLike;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PostLikeResponse {

    private Long postLikeId;

    private Long postId;

    private Long userId;

    private Long writerId;

    @Builder
    public PostLikeResponse(final Long postLikeId, final Long postId, final Long userId, final Long writerId) {
        this.postLikeId = postLikeId;
        this.postId = postId;
        this.userId = userId;
        this.writerId = writerId;
    }

    public static PostLikeResponse of(final PostLike postLike) {
        return PostLikeResponse.builder()
                .postId(postLike.getPost().getId())
                .postLikeId(postLike.getId())
                .userId(postLike.getUser().getId())
                .writerId(postLike.getPost().getUser().getId())
                .build();
    }
}
