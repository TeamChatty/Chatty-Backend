package com.chatty.dto.post.request;

import com.chatty.entity.post.Post;
import com.chatty.entity.user.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PostRequest {


    @NotBlank(message = "내용은 필수로 입력해야 됩니다.")
    private String content;

    @Size(max = 5, message = "사진은 최대 5개까지만 업로드 가능합니다.")
    private List<MultipartFile> images;

    @Builder
    public PostRequest(final String content, final List<MultipartFile> images) {
        this.content = content;
        this.images = images;
    }

    public Post toEntity(final User user) {
        return Post.builder()
                .user(user)
                .content(content)
                .build();
    }
}
