package com.chatty.dto.sms.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SmsUserResponseDto {
    private String authNumber;
    private int limitNumber;

    public static SmsUserResponseDto of(final String authNumber, final int limitNumber){
        return SmsUserResponseDto.builder()
                .authNumber(authNumber)
                .limitNumber(limitNumber)
                .build();
    }
}
