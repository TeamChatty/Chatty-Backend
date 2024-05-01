package com.chatty.dto.check.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CompleteResponseDto {
    private String accessToken;
    private String refreshToken;

    public static CompleteResponseDto of(String accessToken, String refreshToken){
        return CompleteResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
