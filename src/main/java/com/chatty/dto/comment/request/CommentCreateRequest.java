package com.chatty.dto.comment.request;

import com.chatty.entity.comment.Comment;
import com.chatty.entity.post.Post;
import com.chatty.entity.user.User;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CommentCreateRequest {

    @NotBlank(message = "내용은 필수로 입력해야 됩니다.")
    private String content;

    private Comment parent;

    @Builder
    public CommentCreateRequest(final String content, final Comment parent) {
        this.content = content;
        this.parent = parent;
    }

    public Comment toEntity(final Post post, final User user) {
        return Comment.builder()
                .post(post)
                .user(user)
                .content(content)
                .parent(parent)
                .build();
    }
}
