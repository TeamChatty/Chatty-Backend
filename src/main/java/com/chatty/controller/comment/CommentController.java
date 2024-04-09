package com.chatty.controller.comment;

import com.chatty.dto.ApiResponse;
import com.chatty.dto.comment.request.CommentCreateRequest;
import com.chatty.dto.comment.response.CommentResponse;
import com.chatty.service.comment.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/v1/post/{postId}/comment")
    public ApiResponse<CommentResponse> createComment(@PathVariable Long postId,
                                                      @Valid @RequestBody CommentCreateRequest request,
                                                      Authentication authentication) {
        log.info("CommentController - CreateComment");
        return ApiResponse.ok(commentService.createComment(postId, request, authentication.getName()));
    }
}
