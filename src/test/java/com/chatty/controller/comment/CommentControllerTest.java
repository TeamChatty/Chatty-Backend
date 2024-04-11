package com.chatty.controller.comment;

import com.chatty.dto.comment.request.CommentCreateRequest;
import com.chatty.dto.comment.request.CommentReplyCreateRequest;
import com.chatty.dto.comment.response.CommentListResponse;
import com.chatty.dto.comment.response.CommentReplyListResponse;
import com.chatty.service.comment.CommentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentService commentService;

    @BeforeEach
    void setUp() {
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken("01012345678", "", List.of()));
    }

    @DisplayName("댓글을 등록한다.")
    @Test
    void createComment() throws Exception {
        // given
        CommentCreateRequest request = CommentCreateRequest.builder()
                .content("내용")
                .build();

        // when // then
        mockMvc.perform(
                        post("/v1/post/{postId}/comment", 1L).with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("댓글을 등록할 때, 내용은 꼭 입력해야한다.")
    @Test
    void createCommentWithoutContent() throws Exception {
        // given
        CommentCreateRequest request = CommentCreateRequest.builder()
//                .content("내용")
                .build();

        // when // then
        mockMvc.perform(
                        post("/v1/post/{postId}/comment", 1L).with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("대댓글(Reply)을 등록한다.")
    @Test
    void createCommentReply() throws Exception {
        // given
        CommentReplyCreateRequest request = CommentReplyCreateRequest.builder()
                .content("내용")
                .build();

        // when // then
        mockMvc.perform(
                        post("/v1/post/{postId}/comment/{commentId}/comment-reply", 1L, 1L).with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("대댓글(Reply)을 등록할 때, 내용은 필수로 입력해야한다.")
    @Test
    void createCommentReplyWithoutContent() throws Exception {
        // given
        CommentReplyCreateRequest request = CommentReplyCreateRequest.builder()
//                .content("내용")
                .build();

        // when // then
        mockMvc.perform(
                        post("/v1/post/{postId}/comment/{commentId}/comment-reply", 1L, 1L).with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("특정 게시글의 댓글 목록을 조회한다.")
    @Test
    void getCommentList() throws Exception {
        // given
        List<CommentListResponse> result = List.of();

        when(commentService.getCommentList(1L, "01012345678")).thenReturn(result);

        // when // then
        mockMvc.perform(
                        get("/v1/post/{postId}/comments", 1L).with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("대댓글 목록을 조회한다.")
    @Test
    void getCommentReplyList() throws Exception {
        // given
        List<CommentReplyListResponse> result = List.of();

        when(commentService.getCommentReplyList(1L, 1L, "01012345678")).thenReturn(result);

        // when // then
        mockMvc.perform(
                        get("/v1/post/{postId}/comment/{commentId}/comment-replies", 1L, 1L)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }
}