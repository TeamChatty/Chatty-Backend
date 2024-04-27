package com.chatty.controller.check;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.chatty.dto.check.request.CheckRequestDto;
import com.chatty.dto.check.request.CompleteRequestDto;
import com.chatty.dto.check.request.ProblemRequestDto;
import com.chatty.service.check.AuthCheckService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = AuthCheckController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthCheckControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthCheckService authCheckService;

    @Test
    @DisplayName("닉네임과 관련된 문제를 가져온다.")
    void getNicknameProblem() throws Exception{
        //given
        ProblemRequestDto problemRequestDto = ProblemRequestDto.builder().mobileNumber("01012341234").build();

        //when,then
        mockMvc.perform(
                post("/check/problem/nickname")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(problemRequestDto))
        )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("닉네임과 관련된 문제를 가져올시, mobileNumber는 필수 값이다.")
    void getNicknameProblemWithoutMobileNumber() throws Exception{
        //given
        ProblemRequestDto problemRequestDto = ProblemRequestDto.builder().mobileNumber("").build();

        //when, then
        mockMvc.perform(
                        post("/check/problem/nickname").with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(problemRequestDto))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("휴대폰 번호는 필수로 입력해야 합니다."));
    }

    @Test
    @DisplayName("닉네임이 정답인지 확인한다.")
    void checkNickname() throws Exception{
        //given
        CheckRequestDto checkRequestDto = CheckRequestDto.builder().mobileNumber("01012341234").answer("정답").build();

        //when,then
        mockMvc.perform(
                        post("/check/nickname")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(checkRequestDto))
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("닉네임이 정답인지 확인시, 전화번호는 필수 값이다.")
    void checkNicknameWithoutMobileNumber() throws Exception{
        //given
        CheckRequestDto checkRequestDto = CheckRequestDto.builder().mobileNumber("").answer("정답").build();

        //when,then
        mockMvc.perform(
                        post("/check/nickname")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(checkRequestDto))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("휴대폰 번호는 필수로 입력해야 합니다."));
    }

    @Test
    @DisplayName("닉네임이 정답인지 확인시, 정답은 필수 값이다.")
    void checkNicknameWithoutAnswer() throws Exception{
        //given
        CheckRequestDto checkRequestDto = CheckRequestDto.builder().mobileNumber("01012341234").answer("").build();

        //when,then
        mockMvc.perform(
                        post("/check/nickname")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(checkRequestDto))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("정답은 필수로 입력해야 합니다."));
    }

    @Test
    @DisplayName("출생연도와 관련된 문제를 가져온다.")
    void getBirthProblem() throws Exception{
        //given
        ProblemRequestDto problemRequestDto = ProblemRequestDto.builder().mobileNumber("01012341234").build();

        //when,then
        mockMvc.perform(
                        post("/check/problem/birth")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(problemRequestDto))
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("출생연도와 관련된 문제를 가져올시, 전화번호는 필수 값이다.")
    void getBirthProblemWithoutMobileNumber() throws Exception{
        //given
        ProblemRequestDto problemRequestDto = ProblemRequestDto.builder().mobileNumber("").build();

        //when,then
        mockMvc.perform(
                        post("/check/problem/birth")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(problemRequestDto))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("휴대폰 번호는 필수로 입력해야 합니다."));
    }

    @Test
    @DisplayName("출생연도가 정답인지 확인한다.")
    void checkBirth() throws Exception{
        //given
        CheckRequestDto checkRequestDto = CheckRequestDto.builder().mobileNumber("01012341234").answer("정답").build();

        //when,then
        mockMvc.perform(
                        post("/check/birth")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(checkRequestDto))
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("출생연도가 정답인지 확인할때, 휴대폰 번호는 필수 값이다.")
    void checkBirthWithoutMobileNumber() throws Exception{
        //given
        CheckRequestDto checkRequestDto = CheckRequestDto.builder().mobileNumber("").answer("정답").build();

        //when,then
        mockMvc.perform(
                        post("/check/nickname")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(checkRequestDto))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("휴대폰 번호는 필수로 입력해야 합니다."));
    }

    @Test
    @DisplayName("출생연도가 정답인지 확인할때, 정답은 필수 값이다.")
    void checkBirthWithoutAnswer() throws Exception{
        //given
        CheckRequestDto checkRequestDto = CheckRequestDto.builder().mobileNumber("01012341234").answer("").build();

        //when,then
        mockMvc.perform(
                        post("/check/nickname")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(checkRequestDto))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("정답은 필수로 입력해야 합니다."));
    }

    @Test
    @DisplayName("계정 확인이 완료되었습니다.")
    void complete() throws Exception{
        //given
        CompleteRequestDto completeRequestDto = CompleteRequestDto.builder().mobileNumber("01012341234").deviceId("123123").build();
        //when,then
        mockMvc.perform(
                        post("/check/complete")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(completeRequestDto))
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("계정 확인 완료시, 전화번호는 필수 값이다.")
    void completeWithoutMobileNumber() throws Exception{
        //given
        CompleteRequestDto completeRequestDto = CompleteRequestDto.builder().mobileNumber("").deviceId("123123").build();

        //when,then
        mockMvc.perform(
                        post("/check/complete")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(completeRequestDto))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("휴대폰 번호는 필수로 입력해야 합니다."));
    }

    @Test
    @DisplayName("계정 확인 완료시,디바이스 아이디는 필수 값이다.")
    void completeWithoutDeviceId() throws Exception{
        //given
        CompleteRequestDto completeRequestDto = CompleteRequestDto.builder().mobileNumber("01012341234").deviceId("").build();

        //when,then
        mockMvc.perform(
                        post("/check/complete")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(completeRequestDto))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("기기 번호는 필수로 입력해야 합니다."));
    }

}
