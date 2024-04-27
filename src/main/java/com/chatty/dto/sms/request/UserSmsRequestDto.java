package com.chatty.dto.sms.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class UserSmsRequestDto {

    @NotBlank(message = "전화번호 입력은 필수입니다.")
    @Pattern(regexp = "^\\d{11}$", message = "올바른 번호 형식을 입력해주세요.")
    private String mobileNumber;
}
