package com.chatty.dto.like.response;

import com.chatty.entity.like.CommentLike;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CommentLikeResponse {

    private Long commentLikeId;

    private Long commentId;

    private Long userId;

    private Long writerId;

    @Builder
    public CommentLikeResponse(final Long commentLikeId, final Long commentId, final Long userId, final Long writerId) {
        this.commentLikeId = commentLikeId;
        this.commentId = commentId;
        this.userId = userId;
        this.writerId = writerId;
    }

    public static CommentLikeResponse of(final CommentLike commentLike) {
        return CommentLikeResponse.builder()
                .commentLikeId(commentLike.getId())
                .commentId(commentLike.getComment().getId())
                .userId(commentLike.getUser().getId())
                .writerId(commentLike.getComment().getUser().getId())
                .build();
    }
}
