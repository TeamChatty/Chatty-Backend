package com.chatty.dto.chat.request;

import com.chatty.entity.chat.ChatRoom;
import com.chatty.entity.user.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ChatRoomCreateRequest {

    @NotNull(message = "receiverId(수신자)는 필수로 입력해야 합니다.")
    private Long receiverId;

    @Builder
    public ChatRoomCreateRequest(final Long receiverId) {
        this.receiverId = receiverId;
    }

    public ChatRoom toEntity(final User sender, final User receiver) {
        return ChatRoom.builder()
                .sender(sender)
                .receiver(receiver)
                .build();
    }
}
