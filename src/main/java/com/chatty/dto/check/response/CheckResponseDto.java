package com.chatty.dto.check.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CheckResponseDto {
    private String message;
    private boolean isAnswer;
}
