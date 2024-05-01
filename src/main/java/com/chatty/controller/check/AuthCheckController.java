package com.chatty.controller.check;

import com.chatty.dto.ApiResponse;
import com.chatty.dto.check.request.CheckRequestDto;
import com.chatty.dto.check.request.CompleteRequestDto;
import com.chatty.dto.check.request.ProblemRequestDto;
import com.chatty.dto.check.request.ProfileRequestDto;
import com.chatty.dto.check.response.CompleteResponseDto;
import com.chatty.dto.check.response.ProfileResponseDto;
import com.chatty.dto.check.response.ProblemResponseDto;
import com.chatty.service.check.AuthCheckService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/check")
public class AuthCheckController {

    private final AuthCheckService authCheckService;

    @Operation(summary = "문제 제공 - 닉네임", description = "계정확인시, 닉네임에 관련한 문제를 가져옵니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "문제 제공 실패",
            content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "E-003", value = """
                                    {
                                        "errorCode": "003",
                                        "status": "400",
                                        "message": "존재 하지 않는 유저 입니다."
                                    }
                                    """)
                    }
            )
    )
    @PostMapping("/problem/nickname")
    public ApiResponse<ProblemResponseDto> getNicknameProblem(@Valid @RequestBody ProblemRequestDto problemRequestDto) {
        log.info("[AuthCheckController/checkBirth] 닉네임 문제 획득");
        return ApiResponse.ok(authCheckService.createNicknameProblem(problemRequestDto));
    }

    @Operation(summary = "계정확인 - 닉네임 정답 확인하기", description = "계정확인시, 닉네임에 관련한 정답을 확인합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "닉네임 계정확인 실패",
            content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "E-003", value = """
                                    {
                                        "errorCode": "003",
                                        "status": "400",
                                        "message": "존재 하지 않는 유저 입니다."
                                    }
                                    """),
                            @ExampleObject(name = "E-019", value = """
                                    {
                                        "errorCode": "019",
                                        "status": "400",
                                        "message": "계정 확인에 실패했습니다."
                                    }
                                    """),
                            @ExampleObject(name = "E-020", value = """
                                    {
                                        "errorCode": "020",
                                        "status": "400",
                                        "message": "계정 확인 이력이 존재하지 않습니다."
                                    }
                                    """)
                    }
            )
    )
    @PostMapping("/nickname")
    public ApiResponse<String> checkNickname(@Valid @RequestBody CheckRequestDto checkRequestDto) {
        log.info("[AuthCheckController/checkBirth] 닉네임 확인");
        authCheckService.checkNickName(checkRequestDto);
        return ApiResponse.ok("닉네임 확인을 성공했습니다.");
    }

    @Operation(summary = "문제 제공 - 출생 년도", description = "계정확인시, 출생년도에 관련한 문제를 가져옵니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "문제 제공 실패",
            content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "E-003", value = """
                                    {
                                        "errorCode": "003",
                                        "status": "400",
                                        "message": "존재 하지 않는 유저 입니다."
                                    }
                                    """)
                    }
            )
    )
    @PostMapping("/problem/birth")
    public ApiResponse<ProblemResponseDto> getBirthProblem(@Valid @RequestBody ProblemRequestDto problemRequestDto) {
        log.info("[AuthCheckController/checkBirth] 출생 년도 문제 획득");
        return ApiResponse.ok(authCheckService.createBirthProblem(problemRequestDto));
    }

    @Operation(summary = "계정확인 - 출생년도 정답 확인하기", description = "계정확인시, 출생년도에 관련한 정답을 확인합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "출생년도 계정확인 실패",
            content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "E-003", value = """
                                    {
                                        "errorCode": "003",
                                        "status": "400",
                                        "message": "존재 하지 않는 유저 입니다."
                                    }
                                    """),
                            @ExampleObject(name = "E-019", value = """
                                    {
                                        "errorCode": "019",
                                        "status": "400",
                                        "message": "계정 확인에 실패했습니다."
                                    }
                                    """),
                            @ExampleObject(name = "E-020", value = """
                                    {
                                        "errorCode": "020",
                                        "status": "400",
                                        "message": "계정 확인 이력이 존재하지 않습니다."
                                    }
                                    """)
                    }
            )
    )
    @PostMapping("/birth")
    public ApiResponse<String> checkBirth(@Valid @RequestBody CheckRequestDto checkRequestDto) {
        log.info("[AuthCheckController/checkBirth] 출생 연도 월일 확인");
        authCheckService.checkBirth(checkRequestDto);
        return ApiResponse.ok("출생 연도 확인을 성공했습니다.");
    }


    @Operation(summary = "계정 확인 완료", description = "계정 확인을 완료합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "계정 확인 완료 실패",
            content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "E-003", value = """
                                    {
                                        "errorCode": "003",
                                        "status": "400",
                                        "message": "존재 하지 않는 유저 입니다."
                                    }
                                    """),
                            @ExampleObject(name = "E-019", value = """
                                    {
                                        "errorCode": "019",
                                        "status": "400",
                                        "message": "계정 확인에 실패했습니다."
                                    }
                                    """),
                            @ExampleObject(name = "E-020", value = """
                                    {
                                        "errorCode": "020",
                                        "status": "400",
                                        "message": "계정 확인 이력이 존재하지 않습니다."
                                    }
                                    """),
                            @ExampleObject(name = "E-021", value = """
                                    {
                                        "errorCode": "021",
                                        "status": "400",
                                        "message": "계정 확인 질문을 전부 완료해야 합니다."
                                    }
                                    """)
                    }
            )
    )
    @PostMapping("/complete")
    public ApiResponse<CompleteResponseDto> complete(@Valid @RequestBody CompleteRequestDto completeRequestDto) {
        log.info("[AuthCheckController/complete] 계정 확인 완료");
        return ApiResponse.ok(authCheckService.complete(completeRequestDto));
    }

    @PostMapping("/profile")
    public ApiResponse<ProfileResponseDto> getProfile(@Valid @RequestBody ProfileRequestDto profileRequestDto) {
        log.info("[getProfile] 전화번호로 프로필 url 가져오기");
        return ApiResponse.ok(authCheckService.getProfile(profileRequestDto));
    }
}
