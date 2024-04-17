package com.chatty.dto.chat.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ChatRoomUpdateExtendRequest {

    @NotBlank(message = "candy또는 ticket을 입력해주세요.")
    private String unlockMethod;

    @Builder
    public ChatRoomUpdateExtendRequest(final String unlockMethod) {
        this.unlockMethod = unlockMethod;
    }


}
