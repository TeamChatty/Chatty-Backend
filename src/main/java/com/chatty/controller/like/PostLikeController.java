package com.chatty.controller.like;

import com.chatty.dto.ApiResponse;
import com.chatty.dto.like.response.PostLikeResponse;
import com.chatty.service.like.PostLikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class PostLikeController {

    private final PostLikeService postLikeService;

    @PostMapping("/v1/{postId}/like")
    public ApiResponse<PostLikeResponse> likePost(@PathVariable Long postId,
                                                  Authentication authentication) {
        return ApiResponse.ok(postLikeService.likePost(postId, authentication.getName()));
    }

    @DeleteMapping("/v1/{postId}/like")
    public ApiResponse<PostLikeResponse> unlikePost(@PathVariable Long postId,
                                                    Authentication authentication) {
        return ApiResponse.ok(postLikeService.unlikePost(postId, authentication.getName()));
    }
}
