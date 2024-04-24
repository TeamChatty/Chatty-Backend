package com.chatty.dto.chat.response;

import com.chatty.entity.chat.ChatRoom;
import com.chatty.entity.user.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ChatRoomDataResponse {

    private Long roomId;

    private Long partnerId;

    private boolean isExtend;

    private String partnerNickname;

    private String partnerImageUrl;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime chatRoomCreatedTime;

    @Builder
    public ChatRoomDataResponse(final Long roomId, final Long partnerId, final boolean isExtend, final String partnerNickname, final String partnerImageUrl, final LocalDateTime chatRoomCreatedTime) {
        this.roomId = roomId;
        this.partnerId = partnerId;
        this.isExtend = isExtend;
        this.partnerNickname = partnerNickname;
        this.partnerImageUrl = partnerImageUrl;
        this.chatRoomCreatedTime = chatRoomCreatedTime;
    }

    public static ChatRoomDataResponse of(final ChatRoom chatRoom, final User user) {
        User partner = null;
        if (chatRoom.getSender().equals(user)) {
            partner = chatRoom.getReceiver();
        } else {
            partner = chatRoom.getSender();
        }
        return ChatRoomDataResponse.builder()
                .roomId(chatRoom.getRoomId())
                .partnerId(partner.getId())
                .isExtend(chatRoom.isExtend())
                .chatRoomCreatedTime(chatRoom.getCreatedAt())
                .partnerNickname(partner.getNickname())
                .partnerImageUrl(partner.getImageUrl())
                .build();
    }
}
