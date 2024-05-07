package com.chatty.controller.like;

import com.chatty.dto.ApiResponse;
import com.chatty.dto.like.response.CommentLikeResponse;
import com.chatty.service.like.CommentLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class CommentLikeController {

    private final CommentLikeService commentLikeService;

    @PostMapping("/v1/comment-like/{commentId}")
    public ApiResponse<CommentLikeResponse> likeComment(@PathVariable Long commentId,
                                                        Authentication authentication) {
        return ApiResponse.ok(commentLikeService.likeComment(commentId, authentication.getName()));
    }
}
