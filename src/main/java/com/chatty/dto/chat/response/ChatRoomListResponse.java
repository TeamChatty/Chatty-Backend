package com.chatty.dto.chat.response;

import com.chatty.entity.chat.ChatRoom;
import com.chatty.entity.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ChatRoomListResponse {

    private Long roomId;
    private Long senderId;
    private String senderNickname;
    private String senderImageUrl;
    private boolean blueCheck;
    private LocalDateTime lastMessageCreatedTime;
    private String lastMessage;
    private Integer unreadMessageCount;
    private LocalDateTime chatRoomCreatedTime;

    @Builder
    public ChatRoomListResponse(final Long roomId, final Long senderId, final String senderNickname, final String senderImageUrl, final boolean blueCheck, final LocalDateTime lastMessageCreatedTime, final String lastMessage, final Integer unreadMessageCount, final LocalDateTime chatRoomCreatedTime) {
        this.roomId = roomId;
        this.senderId = senderId;
        this.senderNickname = senderNickname;
        this.senderImageUrl = senderImageUrl;
        this.blueCheck = blueCheck;
        this.lastMessageCreatedTime = lastMessageCreatedTime;
        this.lastMessage = lastMessage;
        this.unreadMessageCount = unreadMessageCount;
        this.chatRoomCreatedTime = chatRoomCreatedTime;
    }

    public static ChatRoomListResponse of(final ChatRoom chatRoom, final Long me) {
        User sender = getSender(chatRoom, me);
        return ChatRoomListResponse.builder()
                .roomId(chatRoom.getRoomId())
                .senderId(sender.getId())
                .senderNickname(sender.getNickname())
                .senderImageUrl(sender.getImageUrl())
                .blueCheck(sender.isBlueCheck())
                .lastMessage(getLastMessage(chatRoom))
//                .messages(chatRoom.getChatMessages().stream()
//                        .map(ChatMessage::getContent)
//                        .toList())
                .lastMessageCreatedTime(getLastMessageCreatedAt(chatRoom))
                .unreadMessageCount(getUnreadMessageCount(chatRoom, me))
                .chatRoomCreatedTime(chatRoom.getCreatedAt())
                .build();
    }

    // senderId 값을 내 로그인 중인 ID 값이 아닌 상대방 ID 값으로 지정한다.
    // 채팅방 목록을 뿌릴 때, 상대방 프사와 닉네임으로 뿌려줘야하는데 sender 가 상대방임.
    private static User getSender(final ChatRoom chatRoom, final Long me) {
        if (chatRoom.getSender().getId().equals(me)) {
            return chatRoom.getReceiver();
        }

        return chatRoom.getSender();
    }

    private static String getLastMessage(final ChatRoom chatRoom) {
        if (chatRoom.getChatMessages().isEmpty()) {
            return null;
        }

        return chatRoom.getChatMessages().get(chatRoom.getChatMessages().size() - 1).getContent();
    }

    private static LocalDateTime getLastMessageCreatedAt(final ChatRoom chatRoom) {
        if (chatRoom.getChatMessages().isEmpty()) {
            return chatRoom.getCreatedAt();
        }

        return chatRoom.getChatMessages().get(chatRoom.getChatMessages().size() - 1).getSendTime();
    }

    private static int getUnreadMessageCount(final ChatRoom chatRoom, final Long me) {
        if (chatRoom.getChatMessages().isEmpty()) {
            return 0;
        }

        return chatRoom.getChatMessages().stream()
                .reduce(0, (sum, message) ->
                        sum + ((!message.getIsRead() && !message.getSender().getId().equals(me)) ? 1 : 0)
                        , Integer::sum);
    }
}
