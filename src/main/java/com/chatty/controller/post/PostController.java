package com.chatty.controller.post;

import com.chatty.dto.ApiResponse;
import com.chatty.dto.post.request.PostRequest;
import com.chatty.dto.post.response.PostListResponse;
import com.chatty.dto.post.response.PostResponse;
import com.chatty.service.post.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class PostController {

    private final PostService postService;

    @PostMapping(value = "/v1/post", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<PostResponse> createPost(final Authentication authentication,
                                                @Valid PostRequest request) throws IOException {
        return ApiResponse.ok(postService.createPost(authentication.getName(), request));
    }

    @GetMapping("/v1/post/{postId}")
    public ApiResponse<PostResponse> getPost(@PathVariable Long postId,
                                             final Authentication authentication) {
        return ApiResponse.ok(postService.getPost(authentication.getName(), postId));
    }

    @GetMapping("/v1/posts")
    public ApiResponse<List<PostListResponse>> getPostList(final Authentication authentication) {
        return ApiResponse.ok(postService.getPostList(authentication.getName()));
    }

}
