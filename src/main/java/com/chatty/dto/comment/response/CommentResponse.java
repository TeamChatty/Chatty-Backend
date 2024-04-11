package com.chatty.dto.comment.response;

import com.chatty.entity.comment.Comment;
import com.chatty.entity.post.Post;
import com.chatty.entity.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CommentResponse {

    private Long commentId;

    private Long postId;

    private Long userId;

    private String content;

    @Builder
    public CommentResponse(final Long commentId, final Long postId, final Long userId, final String content) {
        this.commentId = commentId;
        this.postId = postId;
        this.userId = userId;
        this.content = content;
    }

    public static CommentResponse of(final Comment comment, final Post post, final User user) {
        return CommentResponse.builder()
                .commentId(comment.getId())
                .postId(post.getId())
                .userId(user.getId())
                .content(comment.getContent())
                .build();
    }
}
