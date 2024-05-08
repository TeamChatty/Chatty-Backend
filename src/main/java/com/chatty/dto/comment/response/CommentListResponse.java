package com.chatty.dto.comment.response;

import com.chatty.entity.comment.Comment;
import com.chatty.entity.post.Post;
import com.chatty.entity.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CommentListResponse {

    private Long postId;

    private Long commentId;
    private String content;
    private LocalDateTime createdAt;
    private int childCount;

    private Long userId;
    private String nickname;
    private String imageUrl;

    private long likeCount;

    private boolean isLike;

    private boolean isOwner;

    @Builder
    public CommentListResponse(final Long postId, final Long commentId, final String content, final LocalDateTime createdAt, final int childCount, final Long userId, final String nickname, final String imageUrl, final long likeCount, final boolean isLike, final boolean isOwner) {
        this.postId = postId;
        this.commentId = commentId;
        this.content = content;
        this.createdAt = createdAt;
        this.childCount = childCount;
        this.userId = userId;
        this.nickname = nickname;
        this.imageUrl = imageUrl;
        this.likeCount = likeCount;
        this.isLike = isLike;
        this.isOwner = isOwner;
    }

    public static CommentListResponse of(final Comment comment, final User user) {
        return CommentListResponse.builder()
                .postId(comment.getPost().getId())
                .commentId(comment.getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .childCount(comment.getChildren().size())
                .userId(comment.getUser().getId())
                .nickname(comment.getUser().getNickname())
                .imageUrl(comment.getUser().getImageUrl())
                .likeCount(comment.getCommentLikes().size())
                .isLike(comment.getCommentLikes().stream()
                        .anyMatch(commentLike -> commentLike.getUser().getId().equals(user.getId())))
                .isOwner(comment.getUser().getId().equals(user.getId()))
                .build();
    }
}
