package com.chatty.controller.auth;

import com.chatty.dto.ApiResponse;
import com.chatty.dto.auth.request.AuthRequestDto;
import com.chatty.dto.auth.response.AuthResponseDto;
import com.chatty.dto.sms.request.UserSmsRequestDto;
import com.chatty.dto.sms.response.SmsUserResponseDto;
import com.chatty.service.auth.AuthService;
import com.chatty.service.sms.SmsService;
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
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final SmsService smsService;

    @Operation(summary = "토큰 재발급", description = "refreshToken을 사용해서 토큰을 재발급합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "토큰 재발급 실패",
            content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "E-001", value = """
                                    {
                                        "errorCode": "001",
                                        "status": "400",
                                        "message": "refreshToken이 유효성 검증을 실패했습니다."
                                    }
                                    """),
                            @ExampleObject(name = "E-009", value = """
                                    {
                                        "errorCode": "009",
                                        "status": "400",
                                        "message": "refreshToken이 만료되었습니다."
                                    }
                                    """),
                    }
            )
    )
    @PostMapping("/refresh")
    public ApiResponse<AuthResponseDto> refresh(@Valid @RequestBody AuthRequestDto authRequestDto) {
        return ApiResponse.ok(authService.reissueTokens(authRequestDto));
    }

    @Operation(summary = "accessToken 유효성 확인", description = "accessToken 유효성을 검증 합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "accessToken 검증 실패",
            content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "E-001", value = """
                                    {
                                        "errorCode": "001",
                                        "status": "400",
                                        "message": "유효성 검증을 실패했습니다."
                                    }
                                    """),
                            @ExampleObject(name = "E-002", value = """
                                    {
                                        "errorCode": "002",
                                        "status": "400",
                                        "message": "accessToken이 만료되었습니다."
                                    }
                                    """),
                    }
            )
    )
    @PostMapping("/token")
    public ApiResponse<String> token(){
        return ApiResponse.ok(authService.checkAccessToken());
    }
//    @Operation(summary = "accessToken 유효성 확인", description = "accessToken 유효성을 검증 합니다.")
//    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "accessToken 검증 실패",
//            content = @Content(mediaType = "application/json",
//                    examples = {
//                            @ExampleObject(name = "E-001", value = """
//                                    {
//                                        "errorCode": "001",
//                                        "status": "400",
//                                        "message": "유효성 검증을 실패했습니다."
//                                    }
//                                    """),
//                            @ExampleObject(name = "E-002", value = """
//                                    {
//                                        "errorCode": "002",
//                                        "status": "400",
//                                        "message": "accessToken이 만료되었습니다."
//                                    }
//                                    """),
//                    }
//            )
//    )
//    @PostMapping("/token")
//    public ApiResponse<String> token(@Valid @RequestBody CheckTokenDto checkTokenDto){
//        return ApiResponse.ok(authService.checkAccessToken(checkTokenDto));
//    }

    @Operation(summary = "번호 인증 요청", description = "전화번호로 sms 인증요청을 합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "번호 인증 실패",
            content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "E-005", value = """
                                    {
                                        "errorCode": "005",
                                        "status": "400",
                                        "message": "naver에서 sms전송을 실패했습니다."
                                    }
                                    """),
                    }
            )
    )
    @PostMapping("/mobile")
    public ApiResponse<SmsUserResponseDto> mobile(@Valid @RequestBody UserSmsRequestDto userSmsRequestDto) throws Exception {
        log.info("번호 인증 요청");
        return ApiResponse.ok(smsService.saveSms(userSmsRequestDto));
    }
}