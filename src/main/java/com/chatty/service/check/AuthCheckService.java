package com.chatty.service.check;

import com.chatty.constants.Code;
import com.chatty.dto.check.request.CheckRequestDto;
import com.chatty.dto.check.request.CompleteRequestDto;
import com.chatty.dto.check.request.ProblemRequestDto;
import com.chatty.dto.check.request.ProfileRequestDto;
import com.chatty.dto.check.response.CheckResponseDto;
import com.chatty.dto.check.response.CompleteResponseDto;
import com.chatty.dto.check.response.ProblemResponseDto;
import com.chatty.dto.check.response.ProfileResponseDto;
import com.chatty.entity.check.AuthCheck;
import com.chatty.entity.user.User;
import com.chatty.exception.CustomException;
import com.chatty.jwt.JwtTokenProvider;
import com.chatty.repository.check.AuthCheckRepository;
import com.chatty.repository.token.RefreshTokenRepository;
import com.chatty.repository.user.UserRepository;
import com.chatty.utils.check.CheckUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthCheckService {

    private final String NICKNAME = "nickname";
    private final String BIRTH = "birth";
    private static final String ANSWER = "정답입니다.";
    private static final String NOT_ANSWER = "틀렸습니다.";

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthCheckRepository authCheckRepository;

    @Transactional
    public ProblemResponseDto createNicknameProblem(ProblemRequestDto problemRequestDto) {
        String mobileNumber = problemRequestDto.getMobileNumber();
        LocalDate now = LocalDate.now();

        User user = userRepository.findUserByMobileNumber(mobileNumber)
                .orElseThrow(() -> new CustomException(Code.NOT_EXIST_USER));
        String nickname = user.getNickname();

        AuthCheck authCheck = authCheckRepository.findAuthCheckByUserId(user.getId())
                .orElseGet(() ->
                        authCheckRepository.save(AuthCheck.of(user.getId(), false, false, now, 0)
                        )
                );
        isExceeded(authCheck);

        return ProblemResponseDto.of(CheckUtils.createNicknameProblem(nickname));
    }

    public ProblemResponseDto createBirthProblem(ProblemRequestDto problemRequestDto) {
        String mobileNumber = problemRequestDto.getMobileNumber();
        LocalDate birth = userRepository.findUserByMobileNumber(mobileNumber)
                .orElseThrow(() -> new CustomException(Code.NOT_EXIST_USER)).getBirth();

        User user = userRepository.findUserByMobileNumber(mobileNumber)
                .orElseThrow(() -> new CustomException(Code.NOT_EXIST_USER));

        AuthCheck authCheck = authCheckRepository.findAuthCheckByUserId(user.getId())
                .orElseThrow(() -> new CustomException(Code.NOT_EXIST_USER));
        isExceeded(authCheck);

        return ProblemResponseDto.of(CheckUtils.createBirthProblem(birth));
    }

    @Transactional
    public CheckResponseDto checkNickName(CheckRequestDto checkRequestDto) {
        String mobileNumber = checkRequestDto.getMobileNumber();

        User user = userRepository.findUserByMobileNumber(mobileNumber)
                .orElseThrow(() -> new CustomException(Code.NOT_EXIST_USER));

        AuthCheck authCheck = authCheckRepository.findAuthCheckByUserId(user.getId())
                .orElseThrow(() -> new CustomException(Code.NOT_EXIST_USER));
        isExceeded(authCheck);

        boolean isAnswer = false;
        String message = NOT_ANSWER;

        // 매개변수를 넣을 필요가 있나?
        if (checkRequestDto.getAnswer().equals(user.getNickname())) {
            authCheck.updateCheckNicknameToCorrect(true);
            isAnswer = true;
            message = ANSWER;
        } else {
            authCheck.updateCheckNicknameToIncorrect(false);
        }

        return CheckResponseDto.builder()
                .message(message)
                .isAnswer(isAnswer)
                .build();
    }

    @Transactional
    public CheckResponseDto checkBirth(final CheckRequestDto checkRequestDto) {
        String mobileNumber = checkRequestDto.getMobileNumber();

        User user = userRepository.findUserByMobileNumber(mobileNumber)
                .orElseThrow(() -> new CustomException(Code.NOT_EXIST_USER));

        AuthCheck authCheck = authCheckRepository.findAuthCheckByUserId(user.getId())
                .orElseThrow(() -> new CustomException(Code.NOT_EXIST_USER));
        isExceeded(authCheck);

        boolean isAnswer = false;
        String message = NOT_ANSWER;

        // 매개변수를 넣을 필요가 있나?
        if (checkRequestDto.getAnswer().equals(String.valueOf(user.getBirth().getYear()))) {
            authCheck.updateCheckBirthToCorrect(true);
            isAnswer = true;
            message = ANSWER;
        } else {
            authCheck.updateCheckBirthToIncorrect(false);
        }

        return CheckResponseDto.builder()
                .message(message)
                .isAnswer(isAnswer)
                .build();
    }

    @Transactional
    public CompleteResponseDto complete(CompleteRequestDto completeRequestDto) {
        String mobileNumber = completeRequestDto.getMobileNumber();
        String deviceId = completeRequestDto.getDeviceId();
        String deviceToken = completeRequestDto.getDeviceToken();
        User user = userRepository.findUserByMobileNumber(mobileNumber)
                .orElseThrow(() -> new CustomException(Code.NOT_EXIST_USER));

        AuthCheck authCheck = authCheckRepository.findAuthCheckByUserId(user.getId())
                .orElseThrow(() -> new CustomException(Code.NOT_EXIST_AUTHCHECK));
        isExceeded(authCheck);

        log.info("모든 계정확인 질문 답변을 다 했는지 확인합니다.");
        if (!(authCheck.getCheckBirth() && authCheck.getCheckNickname())) {
            throw new CustomException(Code.NOT_CHECK_ALL_QUESTION);
        }

        log.info("deviceId를 업데이트합니다.");
        user.updateDeviceToken(deviceToken);

        log.info("deviceToken을 업데이트합니다.");
        user.updateDeviceId(deviceId);

        log.info("토큰을 생성합니다.");
        String accessToken = jwtTokenProvider.createAccessToken(mobileNumber, deviceId);
        String refreshToken = jwtTokenProvider.createRefreshToken(mobileNumber, deviceId);

        log.info("refreshToken을 저장합니다.");
        refreshTokenRepository.save(deviceId, refreshToken);

        log.info("AuthCheck 데이터 삭제");
        authCheckRepository.deleteAuthCheckByUserId(user.getId());

        return CompleteResponseDto.of(accessToken, refreshToken);
    }

    public ProfileResponseDto getProfile(final ProfileRequestDto profileRequestDto) {

        log.info("profile 가져오기");
        String mobileNumber = profileRequestDto.getMobileNumber();
        User user = userRepository.findUserByMobileNumber(mobileNumber).orElseThrow(() -> new CustomException(Code.NOT_EXIST_USER));

        log.info("imageUrl : {}", user.getImageUrl());
        return ProfileResponseDto.builder().imageUrl(user.getImageUrl()).build();
    }

    private void isExceeded(final AuthCheck authCheck) {
        if (authCheck.getTryCount() >= 2) {
            LocalDate nextTry = authCheck.getRegisteredTime().plusDays(3);
            throw new CustomException(Code.CHECK_LIMIT_EXCEEDED, "2번 이상 틀렸습니다. " + nextTry + "에 가능합니다.");
        }
    }

    /**
     * 계정 확인 엔티티가 3일 이상 지났을 때, 삭제해주는 기능
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void resetAuthCheck() {
        LocalDate localDate = LocalDate.now().minusDays(3);
        List<AuthCheck> allByCutoffDate = authCheckRepository.findAllByCutoffDate(localDate);

        authCheckRepository.deleteAll(allByCutoffDate);
    }
}
