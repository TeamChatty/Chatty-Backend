package com.chatty.dto.comment.response;

import com.chatty.entity.comment.Comment;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CommentReplyListResponse {

    private Long postId;

    private Long commentId;
    private Long parentId;
    private String content;
    private LocalDateTime createdAt;


    private Long userId;
    private String nickname;
    private String imageUrl;

    @Builder
    public CommentReplyListResponse(final Long postId, final Long commentId, final Long parentId, final String content, final LocalDateTime createdAt, final Long userId, final String nickname, final String imageUrl) {
        this.postId = postId;
        this.commentId = commentId;
        this.parentId = parentId;
        this.content = content;
        this.createdAt = createdAt;
        this.userId = userId;
        this.nickname = nickname;
        this.imageUrl = imageUrl;
    }

    public static CommentReplyListResponse of(final Comment comment) {
        return CommentReplyListResponse.builder()
                .postId(comment.getPost().getId())
                .commentId(comment.getId())
                .parentId(comment.getParent().getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .userId(comment.getUser().getId())
                .nickname(comment.getUser().getNickname())
                .imageUrl(comment.getUser().getImageUrl())
                .build();
    }
}
