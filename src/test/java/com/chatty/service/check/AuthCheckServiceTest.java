package com.chatty.service.check;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.chatty.constants.Code;
import com.chatty.dto.check.request.CheckRequestDto;
import com.chatty.dto.check.request.CompleteRequestDto;
import com.chatty.dto.check.request.ProblemRequestDto;
import com.chatty.dto.check.response.CompleteResponseDto;
import com.chatty.dto.check.response.ProblemResponseDto;
import com.chatty.entity.check.AuthCheck;
import com.chatty.entity.user.User;
import com.chatty.exception.CustomException;
import com.chatty.jwt.JwtTokenProvider;
import com.chatty.repository.check.AuthCheckRepository;
import com.chatty.repository.token.RefreshTokenRepository;
import com.chatty.repository.user.UserRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AuthCheckServiceTest {

    @InjectMocks
    private AuthCheckService authCheckService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private AuthCheckRepository authCheckRepository;

    @Test
    @DisplayName("닉네임과 관련된 문제를 만든다.")
    void createNicknameProblem() throws Exception{
        //given
        ProblemRequestDto problemRequestDto = ProblemRequestDto.builder().mobileNumber("01077265108").build();
        User user = User.builder().id(1L).build();
        AuthCheck authCheck = AuthCheck.of(user.getId(), false, false);

        //when
        when(userRepository.findUserByMobileNumber(problemRequestDto.getMobileNumber())).thenReturn(Optional.of(user));
        when(authCheckRepository.save(any())).thenReturn(authCheck);
        ProblemResponseDto problemResponseDto = authCheckService.createNicknameProblem(problemRequestDto);

        //then
        assertThat(problemResponseDto.getProblem()).isNotEmpty();
    }

    @Test
    @DisplayName("닉네임과 관련된 문제를 만들시, 계정이 존재하지 않을 경우 에러가 발생한다. ")
    void createNicknameProblemWithoutUser() throws Exception{
        //given
        ProblemRequestDto problemRequestDto = ProblemRequestDto.builder().mobileNumber("01077265108").build();
        User user = User.builder().id(1L).build();

        //when
        when(userRepository.findUserByMobileNumber(problemRequestDto.getMobileNumber())).thenThrow(new CustomException(
                Code.NOT_EXIST_USER));

        //then
        assertThatThrownBy(() -> authCheckService.createNicknameProblem(problemRequestDto))
                .isInstanceOf(CustomException.class)
                .hasMessage("존재 하지 않는 유저 입니다.");
    }

//    @Test
//    @DisplayName("닉네임 문제의 정답을 확인한다.")
//    void checkNickname() throws Exception{
//        //given
//        CheckRequestDto checkRequestDto = CheckRequestDto.builder().mobileNumber("01012341234").answer("무야호").build();
//        User user = User.builder().id(1L).nickname("무야호").build();
//        AuthCheck authCheck = AuthCheck.of(user.getId(), false, false);
//
//        //when, then
//        when(userRepository.findUserByMobileNumber(checkRequestDto.getMobileNumber())).thenReturn(Optional.of(user));
//        when(authCheckRepository.findAuthCheckByUserId(user.getId())).thenReturn(Optional.of(authCheck));
//        authCheckService.checkNickName(checkRequestDto);
//    }

//    @Test
//    @DisplayName("닉네임 문제의 정답을 확인할 시, 계정이 존재하지 않을 경우 에러가 발생한다.")
//    void checkNicknameProblemWithoutUser() throws Exception{
//        //given
//        CheckRequestDto checkRequestDto = CheckRequestDto.builder().mobileNumber("01012341234").answer("무야호").build();
//
//        //when
//        when(userRepository.findUserByMobileNumber(checkRequestDto.getMobileNumber())).thenThrow(new CustomException(
//                Code.NOT_EXIST_USER));
//
//        //then
//        assertThatThrownBy(() -> authCheckService.checkNickName(checkRequestDto))
//                .isInstanceOf(CustomException.class)
//                .hasMessage("존재 하지 않는 유저 입니다.");
//    }

//    @Test
//    @DisplayName("닉네임 문제의 정답을 확인할 시, 계정확인이 존재하지 않을 경우 에러가 발생한다. ")
//    void checkNicknameWithoutAuthCheck() throws Exception{
//        //given
//        CheckRequestDto checkRequestDto = CheckRequestDto.builder().mobileNumber("01012341234").answer("무야호").build();
//        User user = User.builder().id(1L).nickname("무야호").build();
//
//        //when
//        when(userRepository.findUserByMobileNumber(checkRequestDto.getMobileNumber())).thenReturn(Optional.of(user));
//        when(authCheckRepository.findAuthCheckByUserId(user.getId())).thenThrow(new CustomException(Code.NOT_EXIST_AUTHCHECK));
//
//        //then
//        assertThatThrownBy(() -> authCheckService.checkNickName(checkRequestDto))
//                .isInstanceOf(CustomException.class)
//                .hasMessage("계정 확인 이력이 존재하지 않습니다.");
//    }


    @Test
    @DisplayName("태어난 연도와 관련된 문제를 만든다.")
    void createBirthProblem() throws Exception{
        //given
        ProblemRequestDto problemRequestDto = ProblemRequestDto.builder().mobileNumber("01077265108").build();
        User user = User.builder().id(1L).birth(LocalDate.of(1999,1,1)).build();

        //when
        when(userRepository.findUserByMobileNumber(problemRequestDto.getMobileNumber())).thenReturn(Optional.of(user));
        ProblemResponseDto problemResponseDto = authCheckService.createBirthProblem(problemRequestDto);

        //then
        assertThat(problemResponseDto.getProblem()).isNotEmpty();
    }

    @Test
    @DisplayName("태어난 연도와 관련된 문제를 만들때, 유저가 존재하지 않으면 에러가 발생한다.")
    void createBirthProblemWithoutUser() throws Exception{
        //given
        ProblemRequestDto problemRequestDto = ProblemRequestDto.builder().mobileNumber("01077265108").build();
        User user = User.builder().id(1L).birth(LocalDate.of(1999,1,1)).build();

        //when
        when(userRepository.findUserByMobileNumber(problemRequestDto.getMobileNumber())).thenThrow(new CustomException(Code.NOT_EXIST_USER));

        //then
        assertThatThrownBy(() -> authCheckService.createBirthProblem(problemRequestDto))
                .isInstanceOf(CustomException.class)
                .hasMessage("존재 하지 않는 유저 입니다.");
    }

    @Test
    @DisplayName("태어난 연도의 정답을 확인한다.")
    void checkBirth() throws Exception{
        //given
        CheckRequestDto checkRequestDto = CheckRequestDto.builder().mobileNumber("01012341234").answer("1999").build();
        User user = User.builder().id(1L).birth(LocalDate.of(1999,1,1)).build();
        AuthCheck authCheck = AuthCheck.of(user.getId(), false, false);

        //when, then
        when(userRepository.findUserByMobileNumber(checkRequestDto.getMobileNumber())).thenReturn(Optional.of(user));
        when(authCheckRepository.findAuthCheckByUserId(user.getId())).thenReturn(Optional.of(authCheck));
        authCheckService.checkBirth(checkRequestDto);
    }

    @Test
    @DisplayName("태어난 연도의 정답을 확인시, 유저가 존재하지 않으면 에러가 발생한다.")
    void checkBirthWithoutUser() throws Exception{
        //given
        CheckRequestDto checkRequestDto = CheckRequestDto.builder().mobileNumber("01012341234").answer("1999").build();

        //when
        when(userRepository.findUserByMobileNumber(checkRequestDto.getMobileNumber())).thenThrow(new CustomException(Code.NOT_EXIST_USER));

        //then
        assertThatThrownBy(() -> authCheckService.checkBirth(checkRequestDto))
                .isInstanceOf(CustomException.class)
                        .hasMessage("존재 하지 않는 유저 입니다.");
    }

    @Test
    @DisplayName("태어난 연도의 정답을 확인시, 계정 확인이 존재하지 않으면 에러가 발생한다.")
    void checkBirthWithoutAuthCheck() throws Exception{
        //given
        CheckRequestDto checkRequestDto = CheckRequestDto.builder().mobileNumber("01012341234").answer("1999").build();
        User user = User.builder().id(1L).birth(LocalDate.of(1999,1,1)).build();

        //when
        when(userRepository.findUserByMobileNumber(checkRequestDto.getMobileNumber())).thenReturn(Optional.of(user));
        when(authCheckRepository.findAuthCheckByUserId(user.getId())).thenThrow(new CustomException(Code.NOT_EXIST_AUTHCHECK));

        //then
        assertThatThrownBy(() -> authCheckService.checkBirth(checkRequestDto))
                .isInstanceOf(CustomException.class)
                        .hasMessage("계정 확인 이력이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("모든 계정확인 질문 답변을 완료한다.")
    void complete() throws Exception{
        //given
        CompleteRequestDto completeRequestDto = CompleteRequestDto.builder().mobileNumber("01012341234").deviceId("123123").build();
        User user = User.builder().id(1L).build();
        AuthCheck authCheck = AuthCheck.of(1L,true,true);

        //when
        when(userRepository.findUserByMobileNumber(completeRequestDto.getMobileNumber())).thenReturn(Optional.of(user));
        when(authCheckRepository.findAuthCheckByUserId(user.getId())).thenReturn(Optional.of(authCheck));
        when(jwtTokenProvider.createAccessToken(any(),any())).thenReturn("abc");
        when(jwtTokenProvider.createRefreshToken(any(),any())).thenReturn("abc");
        CompleteResponseDto completeResponseDto = authCheckService.complete(completeRequestDto);

        //then
        assertThat(completeResponseDto.getAccessToken()).isNotEmpty();
        assertThat(completeResponseDto.getRefreshToken()).isNotEmpty();
    }

    @Test
    @DisplayName("모든 계정확인 질문 답변을 완료시, 유저가 존재하지 않으면 에러가 발생한다.")
    void completeWithoutUser() throws Exception{
        //given
        CompleteRequestDto completeRequestDto = CompleteRequestDto.builder().mobileNumber("01012341234").deviceId("123123").build();

        //when
        when(userRepository.findUserByMobileNumber(completeRequestDto.getMobileNumber())).thenThrow(new CustomException(Code.NOT_EXIST_USER));

        //then
        assertThatThrownBy(() -> authCheckService.complete(completeRequestDto))
                .isInstanceOf(CustomException.class)
                .hasMessage("존재 하지 않는 유저 입니다.");
    }

    @Test
    @DisplayName("모든 계정확인 질문 답변을 완료시, 계정 확인 이력이 존재하지 않으면 에러가 발생한다.")
    void completeWithoutAuthcheck() throws Exception{
        //given
        CompleteRequestDto completeRequestDto = CompleteRequestDto.builder().mobileNumber("01012341234").deviceId("123123").build();
        User user = User.builder().id(1L).build();

        //when
        when(userRepository.findUserByMobileNumber(completeRequestDto.getMobileNumber())).thenReturn(Optional.of(user));
        when(authCheckRepository.findAuthCheckByUserId(user.getId())).thenThrow(new CustomException(Code.NOT_EXIST_AUTHCHECK));

        //then
        assertThatThrownBy(() -> authCheckService.complete(completeRequestDto))
                .isInstanceOf(CustomException.class)
                .hasMessage("계정 확인 이력이 존재하지 않습니다.");

    }

    @Test
    @DisplayName("모든 계정확인 질문 답변을 완료하지 않으면 에러가 발생한다.")
    void completeNot() throws Exception{
        //given
        CompleteRequestDto completeRequestDto = CompleteRequestDto.builder().mobileNumber("01012341234").deviceId("123123").build();
        User user = User.builder().id(1L).build();
        AuthCheck authCheck = AuthCheck.of(1L,false,false);

        //when
        when(userRepository.findUserByMobileNumber(completeRequestDto.getMobileNumber())).thenReturn(Optional.of(user));
        when(authCheckRepository.findAuthCheckByUserId(user.getId())).thenReturn(Optional.of(authCheck));

        //then
        assertThatThrownBy(() -> authCheckService.complete(completeRequestDto))
                .isInstanceOf(CustomException.class)
                .hasMessage("계정 확인 질문을 전부 완료해야 합니다.");
    }
}