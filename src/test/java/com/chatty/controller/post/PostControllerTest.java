package com.chatty.controller.post;

import com.chatty.dto.post.request.PostRequest;
import com.chatty.dto.post.response.PostListResponse;
import com.chatty.service.post.PostService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PostController.class)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PostService postService;

    @BeforeEach
    void setUp() {
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken("01012345678", "", List.of()));
    }

    @DisplayName("게시글을 등록한다.")
    @Test
    void createPost() throws Exception {
        // given
        MockMultipartFile image = new MockMultipartFile("images", "image.jpg", "image/jpeg", new byte[]{123, 123});
        List<MultipartFile> images = List.of(image);

        PostRequest request = PostRequest.builder()
                .title("제목")
                .content("내용")
//                .images(images)
                .build();

        // when // then
        mockMvc.perform(
                        multipart("/v1/post")
                                .file(image)
                                .with(csrf())
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .param("title", "제목")
                                .param("content", "내용")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("게시글을 등록할 때, 제목은 필수값이다.")
    @Test
    void createPostWithoutTitle() throws Exception {
        // given
        MockMultipartFile image = new MockMultipartFile("images", "image.jpg", "image/jpeg", new byte[]{123, 123});
        List<MultipartFile> images = List.of(image);

        PostRequest request = PostRequest.builder()
//                .title("제목")
                .content("내용")
                .images(images)
                .build();

        // when // then
        mockMvc.perform(
                        multipart("/v1/post")
                                .file(image)
                                .with(csrf())
                                .contentType(MediaType.MULTIPART_FORM_DATA)
//                                .param("title", "제목")
                                .param("content", "내용")
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("제목은 필수로 입력해야 됩니다."));
    }

    @DisplayName("게시글을 등록할 때, 내용은 필수값이다.")
    @Test
    void createPostWithoutContent() throws Exception {
        // given
        MockMultipartFile image = new MockMultipartFile("images", "image.jpg", "image/jpeg", new byte[]{123, 123});
        List<MultipartFile> images = List.of(image);

        PostRequest request = PostRequest.builder()
                .title("제목")
//                .content("내용")
                .images(images)
                .build();

        // when // then
        mockMvc.perform(
                        multipart("/v1/post")
                                .file(image)
                                .with(csrf())
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .param("title", "제목")
//                                .param("content", "내용")
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("내용은 필수로 입력해야 됩니다."));
    }

    @DisplayName("게시글을 등록할 때, 사진은 최대 5장만 업로드할 수 있다.")
    @Test
    void createPostLimitImageSize() throws Exception {
        // given
        MockMultipartFile image = new MockMultipartFile("images", "image.jpg", "image/jpeg", new byte[]{123, 123});
        List<MultipartFile> images = List.of(image);

        PostRequest request = PostRequest.builder()
                .title("제목")
                .content("내용")
                .images(images)
                .build();

        // when // then
        mockMvc.perform(
                        multipart("/v1/post")
                                .file(image)
                                .file(image)
                                .file(image)
                                .file(image)
                                .file(image)
                                .file(image)
                                .with(csrf())
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .param("title", "제목")
                                .param("content", "내용")
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("사진은 최대 5개까지만 업로드 가능합니다."));
    }

    @DisplayName("게시글을 조회한다.")
    @Test
    void getPost() throws Exception {
        // given

        // when // then
        mockMvc.perform(
                        get("/v1/post/{postId}", 1L)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("게시글 목록을 조회한다.")
    @Test
    void getPostList() throws Exception {
        // given
        List<PostListResponse> result = List.of();

        when(postService.getPostList("01012341234")).thenReturn(result);

        // when // then
        mockMvc.perform(
                        get("/v1/posts")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }
}