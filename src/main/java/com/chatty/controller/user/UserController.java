package com.chatty.controller.user;

import com.chatty.dto.ApiResponse;
import com.chatty.dto.interest.request.InterestRequest;
import com.chatty.dto.user.request.*;
import com.chatty.dto.user.response.UserResponse;
import com.chatty.dto.user.response.UserResponseDto;
import com.chatty.service.user.UserService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("users")
public class UserController {

    private final UserService userService;

    @Operation(summary = "로그인", description = "로그인에 필요한 정보를 입력한 후, 로그인을 완료합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "회원가입 실패",
            content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "E-003", value = """
                                    {
                                        "errorCode": "003",
                                        "status": "400",
                                        "message": "존재하지 않는 유저 입니다."
                                    }
                                    """),
                            @ExampleObject(name = "E-007", value = """
                                    {
                                        "errorCode": "007",
                                        "status": "400",
                                        "message": "유효하지 않은 인증 번호 입니다."
                                    }
                                    """),
                    }
            )
    )
    @PostMapping("/login")
    public ApiResponse<UserResponseDto> login(@Valid @RequestBody UserRequestDto userRequestDto) {
        log.info("[UserController/login] 로그인 시작");
        return ApiResponse.ok(userService.login(userRequestDto));
    }

    @PostMapping("/logout")
    public ApiResponse<String> logout(Authentication authentication) {
        log.info("UserController - logout");
        return ApiResponse.ok(userService.logout(authentication.getName()));
    }


    @Operation(summary = "회원가입", description = "sms인증으로 1차 회원가입을 완료합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "회원가입 실패",
            content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "E-007", value = """
                                    {
                                        "errorCode": "007",
                                        "status": "400",
                                        "message": "유효하지 않은 인증 번호입니다."
                                    }
                                    """),
                            @ExampleObject(name = "E-008", value = """
                                    {
                                        "errorCode": "008",
                                        "status": "400",
                                        "message": "이미 존재하는 유저 입니다."
                                    }
                                    """),
                    }
            )
    )
    @PostMapping("/join")
    public ApiResponse<UserResponseDto> join(@Valid @RequestBody UserRequestDto userRequestDto) {
        log.info("[UserController/join] 회원 가입 시작");
        return ApiResponse.ok(userService.join(userRequestDto));
    }

    @Hidden
    @Operation(summary = "최종 회원가입", description = "회원가입에 필요한 정보를 입력한 후, 회원가입을 완료합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "회원가입 실패",
            content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "E-003", value = """
                                    {
                                        "errorCode": "003",
                                        "status": "400",
                                        "message": "존재하지 않는 유저 입니다."
                                    }
                                    """),
                            @ExampleObject(name = "E-006", value = """
                                    {
                                        "errorCode": "006",
                                        "status": "400",
                                        "message": "이미 존재하는 닉네임입니다."
                                    }
                                    """)
                    }
            )
    )
    @PutMapping("/update")
    public ApiResponse<UserResponse> joinComplete(@Valid @RequestBody UserJoinRequest request,
                                                  final Authentication authentication) {

        return ApiResponse.ok(userService.joinComplete(authentication.getName(), request));
    }

    @Operation(summary = "닉네임 변경", description = "닉네임을 변경합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "닉네임 변경 실패",
            content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "E-006", value = """
                                    {
                                        "errorCode": "006",
                                        "status": "400",
                                        "message": "이미 존재하는 닉네임입니다."
                                    }
                                    """),
                    }
            )
    )
    @PutMapping("/nickname")
    public ApiResponse<UserResponse> updateNickname(@Valid @RequestBody UserNicknameRequest request,
                                                    final Authentication authentication) {
        return ApiResponse.ok(userService.updateNickname(authentication.getName(), request));
    }

    @Operation(summary = "성별 변경", description = "성별을 변경합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "성별 변경 실패",
            content = @Content(
                    examples = {
                            @ExampleObject(name = "E-003", value = """
                                    {
                                        "errorCode": "003",
                                        "status": "400",
                                        "message": "존재 하지 않는 유저입니다."
                                    }
                                    """),
                    }
            )
    )
    @PutMapping("/gender")
    public ApiResponse<UserResponse> updateGender(@Valid @RequestBody UserGenderRequest request,
                                                  final Authentication authentication) {
        return ApiResponse.ok(userService.updateGender(authentication.getName(), request));
    }

    @Operation(summary = "생년월일 변경", description = "생년월일을 변경합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "생년월일 변경 실패",
            content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "E-003", value = """
                                    {
                                        "errorCode": "003",
                                        "status": "400",
                                        "message": "존재 하지 않는 유저입니다."
                                    }
                                    """),
                    }
            )
    )
    @PutMapping("/birth")
    public ApiResponse<UserResponse> updateBirth(@Valid @RequestBody UserBirthRequest request,
                                                 final Authentication authentication) {

        return ApiResponse.ok(userService.updateBirth(authentication.getName(), request));
    }

    @Operation(summary = "MBTI 변경", description = "MBTI를 변경합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "MBTI변경 실패",
            content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "E-003", value = """
                                    {
                                        "errorCode": "003",
                                        "status": "400",
                                        "message": "존재하지 않는 유저 입니다."
                                    }
                                    """),
                    }
            )
    )
    @PutMapping("/mbti")
    public ApiResponse<UserResponse> updateMbti(@Valid @RequestBody UserMbtiRequest request,
                                                final Authentication authentication) {

        return ApiResponse.ok(userService.updateMbti(authentication.getName(), request));
    }

    @Hidden
    @Operation(summary = "주소 변경", description = "주소를 변경합니다.")
    @PutMapping("/coordinate")
    public ApiResponse<UserResponse> updateCoordinate(@Valid @RequestBody UserCoordinateRequest request,
                                                      final Authentication authentication) {

        return ApiResponse.ok(userService.updateCoordinate(authentication.getName(), request));
    }

    @Operation(summary = "프로필 이미지 변경", description = "프로필 이미지를 변경합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "프로필 이미지 변경 실패",
            content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "E-003", description = "존재하지 않는 유저일 때", value = """
                                    {
                                        "errorCode": "003",
                                        "status": "400",
                                        "message": "존재하지 않는 유저 입니다."
                                    }
                                    """),
                            @ExampleObject(name = "E-016", description = "jpg, png, jpeg 확장자가 아닐 때", value = """
                                    {
                                        "errorCode": "016",
                                        "status": "400",
                                        "message": "올바르지 않은 확장자입니다."
                                    }
                                    """)
                    }
            )
    )
    @PutMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<UserResponse> updateImage(@RequestParam("image") MultipartFile image,
                                                 final Authentication authentication) throws IOException {
        return ApiResponse.ok(userService.updateImage(authentication.getName(), image));
    }

    @Operation(summary = "디바이스 토큰 변경", description = "디바이스 토큰을 변경합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "디바이스 토큰 변경 실패",
            content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "E-003", description = "존재하지 않는 유저일 때", value = """
                                    {
                                        "errorCode": "003",
                                        "status": "400",
                                        "message": "존재하지 않는 유저 입니다."
                                    }
                                    """)
                    }
            )
    )
    @PutMapping("/deviceToken")
    public ApiResponse<String> updateDeviceToken(@RequestBody @Valid UserDeviceTokenRequest request, final Authentication authentication) {
        return ApiResponse.ok(userService.updateDeviceToken(authentication.getName(), request));
    }

    @Operation(summary = "관심사 변경", description = "관심사를 변경합니다.")
    @PutMapping("/interests")
    public ApiResponse<UserResponse> updateInterest(@RequestBody @Valid InterestRequest request, Authentication authentication) {
        return ApiResponse.ok(userService.updateInterests(request, authentication.getName()));
    }

    @Operation(summary = "주소 변경", description = "주소를 변경합니다.")
    @PutMapping("/address")
    public ApiResponse<UserResponse> updateAddress(@RequestBody @Valid UserAddressRequest request, Authentication authentication) {
        return ApiResponse.ok(userService.updateAddress(request, authentication.getName()));
    }

    @Operation(summary = "직업 변경", description = "직업을 변경합니다.")
    @PutMapping("/job")
    public ApiResponse<UserResponse> updateJob(@RequestBody @Valid UserJobRequest request, Authentication authentication) {
        return ApiResponse.ok(userService.updateJob(request, authentication.getName()));
    }

    @Operation(summary = "학교 변경", description = "학교를 변경합니다.")
    @PutMapping("/school")
    public ApiResponse<UserResponse> updateSchool(@RequestBody @Valid UserSchoolRequest request, Authentication authentication) {
        return ApiResponse.ok(userService.updateSchool(request, authentication.getName()));
    }

    @Operation(summary = "자기소개 변경", description = "자기소개를 변경합니다.")
    @PutMapping("/introduce")
    public ApiResponse<UserResponse> updateIntroduce(@RequestBody @Valid UserIntroduceRequest request, Authentication authentication) {
        return ApiResponse.ok(userService.updateIntroduce(request, authentication.getName()));
    }

    @GetMapping("/my/profile")
    public ApiResponse<UserResponse> getMyProfile(Authentication authentication) {
        return ApiResponse.ok(userService.getMyProfile(authentication.getName()));
    }

}
