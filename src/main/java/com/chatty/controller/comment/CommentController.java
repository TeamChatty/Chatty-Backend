package com.chatty.controller.comment;

import com.chatty.dto.ApiResponse;
import com.chatty.dto.comment.request.CommentCreateRequest;
import com.chatty.dto.comment.request.CommentReplyCreateRequest;
import com.chatty.dto.comment.response.CommentListResponse;
import com.chatty.dto.comment.response.CommentReplyListResponse;
import com.chatty.dto.comment.response.CommentResponse;
import com.chatty.service.comment.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping("/v1/post/{postId}/comment/{commentId}/comment-reply")
    public ApiResponse<CommentResponse> createCommentReply(@PathVariable Long postId,
                                                           @PathVariable Long commentId,
                                                           @Valid @RequestBody CommentReplyCreateRequest request,
                                                           Authentication authentication) {
        log.info("CommentController - CreateCommentReply");
        return ApiResponse.ok(commentService.createCommentReply(postId, commentId, request, authentication.getName()));
    }

    @GetMapping("/v1/post/{postId}/comments")
    public ApiResponse<List<CommentListResponse>> getCommentList(@PathVariable Long postId,
                                                                 Authentication authentication) {
        return ApiResponse.ok(commentService.getCommentList(postId, authentication.getName()));
    }

    @GetMapping("/v2/post/{postId}/comments")
    public ApiResponse<List<CommentListResponse>> getCommentListPages(@PathVariable Long postId,
                                                                      @RequestParam Long lastCommentId,
                                                                      @RequestParam int size,
                                                                      Authentication authentication) {
        return ApiResponse.ok(commentService.getCommentListPages(postId, lastCommentId, size, authentication.getName()));
    }

    @GetMapping("/v1/post/{postId}/comment/{commentId}/comment-replies")
    public ApiResponse<List<CommentReplyListResponse>> getCommentReplyList(@PathVariable Long postId,
                                                                           @PathVariable Long commentId,
                                                                           Authentication authentication) {
        return ApiResponse.ok(commentService.getCommentReplyList(postId, commentId, authentication.getName()));
    }

    @GetMapping("/v2/comment-replies/{commentId}")
    public ApiResponse<List<CommentReplyListResponse>> getCommentReplyListPages(@PathVariable Long commentId,
                                                                           @RequestParam Long lastCommentId,
                                                                           @RequestParam int size,
                                                                           Authentication authentication) {
        return ApiResponse.ok(commentService.getCommentReplyListPages(commentId, lastCommentId, size, authentication.getName()));
    }

    @GetMapping("/v1/my-comments")
    public ApiResponse<List<CommentListResponse>> getMyCommentListPages(@RequestParam Long lastCommentId,
                                                                        @RequestParam int size,
                                                                        Authentication authentication) {
        return ApiResponse.ok(commentService.getMyCommentListPages(lastCommentId, size, authentication.getName()));
    }
}
