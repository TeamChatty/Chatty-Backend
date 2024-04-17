package com.chatty.service.chat;

import com.chatty.constants.Authority;
import com.chatty.dto.chat.request.ChatMessageRequest;
import com.chatty.dto.chat.response.ChatMessageListResponse;
import com.chatty.dto.chat.response.SimpleMessageResponseDto;
import com.chatty.entity.chat.ChatMessage;
import com.chatty.entity.chat.ChatRoom;
import com.chatty.entity.user.User;
import com.chatty.exception.CustomException;
import com.chatty.repository.chat.ChatRoomRepository;
import com.chatty.repository.chat.MessageRepository;
import com.chatty.repository.user.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ChatMessageServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private ChatService chatMessageService;

    @Autowired
    private MessageRepository chatMessageRepository;

    @AfterEach
    void tearDown() {
        chatMessageRepository.deleteAllInBatch();
        chatRoomRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("채팅방에서 메세지를 전송한다. 1번 유저가 2번 유저에게 메세지를 보낸다. (채팅방 생성 후 10분 초과X)")
    @Test
    void saveMessage() {
        // given
        LocalDateTime now = LocalDateTime.now();
        User user1 = createUser("박지성", "01011112222", "profile1.jpg", true);
        User user2 = createUser("김연아", "01012345678", "profile2.jpg", true);
        userRepository.saveAll(List.of(user1, user2));

        ChatRoom chatRoom = createChatRoom(user1, user2, false);
        chatRoomRepository.save(chatRoom);

        ChatMessageRequest request = ChatMessageRequest.builder()
                .content("안녕하세요.")
                .build();

        // when
        SimpleMessageResponseDto chatMessageResponse = chatMessageService.saveMessage(chatRoom.getRoomId(), request, user1.getMobileNumber(), now);

        // then
        assertThat(chatMessageResponse.getMessageId()).isNotNull();
        assertThat(chatMessageResponse).
                extracting("roomId", "senderId", "receiverId", "content")
                .containsExactlyInAnyOrder(chatRoom.getRoomId(), user1.getId(), user2.getId(), "안녕하세요.");
    }

    @DisplayName("채팅방에서 메세지를 전송할 때, 채팅방 생성 시간이 10분을 초과하고 연장이 안 되어있으면 예외가 발생한다.")
    @Test
    void saveMessageWithoutExtendAndExpiredTime() {
        // given
        User user1 = createUser("박지성", "01011112222", "profile1.jpg", true);
        User user2 = createUser("김연아", "01012345678", "profile2.jpg", true);
        userRepository.saveAll(List.of(user1, user2));

        ChatRoom chatRoom = createChatRoom(user2, user1, false);
        chatRoomRepository.save(chatRoom);
        LocalDateTime nowPlusElevenMinute = chatRoom.getCreatedAt().plusMinutes(11);

        ChatMessageRequest request = ChatMessageRequest.builder()
                .content("안녕하세요.")
                .build();

        // when // then
        assertThatThrownBy(() -> chatMessageService.saveMessage(chatRoom.getRoomId(), request, user2.getMobileNumber(), nowPlusElevenMinute))
                .isInstanceOf(CustomException.class)
                .hasMessage("제한 시간을 초과했습니다.");
    }

    @DisplayName("채팅방에서 메세지를 전송할 때, 10분을 초과하였지만 연장이 되어있으면 메세지가 보내진다.")
    @Test
    void saveMessageExpiredTimeAndExtend() {
        // given
        User user1 = createUser("박지성", "01011112222", "profile1.jpg", true);
        User user2 = createUser("김연아", "01012345678", "profile2.jpg", true);
        userRepository.saveAll(List.of(user1, user2));

        ChatRoom chatRoom = createChatRoom(user2, user1, true);
        chatRoomRepository.save(chatRoom);
        LocalDateTime nowPlusElevenMinute = chatRoom.getCreatedAt().plusMinutes(11);

        ChatMessageRequest request = ChatMessageRequest.builder()
                .content("안녕하세요.")
                .build();

        // when
        SimpleMessageResponseDto chatMessageResponse = chatMessageService.saveMessage(chatRoom.getRoomId(), request, user1.getMobileNumber(), nowPlusElevenMinute);

        // then
        assertThat(chatMessageResponse.getMessageId()).isNotNull();
        assertThat(chatMessageResponse).
                extracting("roomId", "senderId", "receiverId", "content")
                .containsExactlyInAnyOrder(chatRoom.getRoomId(), user1.getId(), user2.getId(), "안녕하세요.");
    }

    @DisplayName("채팅방에서 메세지를 전송할 때, receiver 값은 ChatRoom 안에 있는 sender와 반대 값이다.")
    @Test
    void saveMessageGetReceiver() {
        // given
        LocalDateTime now = LocalDateTime.now();
        User user1 = createUser("박지성", "01011112222", "profile1.jpg", true);
        User user2 = createUser("김연아", "01012345678", "profile2.jpg", true);
        userRepository.saveAll(List.of(user1, user2));

        ChatRoom chatRoom = createChatRoom(user2, user1, false);
        chatRoomRepository.save(chatRoom);

        ChatMessageRequest request = ChatMessageRequest.builder()
                .content("안녕하세요.")
                .build();

        // when
        SimpleMessageResponseDto chatMessageResponse = chatMessageService.saveMessage(chatRoom.getRoomId(), request, user2.getMobileNumber(), now);

        // then
        assertThat(chatMessageResponse.getMessageId()).isNotNull();
        assertThat(chatMessageResponse).
                extracting("roomId", "senderId", "receiverId")
                .containsExactlyInAnyOrder(chatRoom.getRoomId(), user2.getId(), user1.getId());
    }

    @DisplayName("채팅방에서 메세지를 전송할 때, 보내는 사람이 채팅방에 속해있지 않으면 예외가 발생한다.")
    @Test
    void saveMessageWithoutInChatRoom() {
        // given
        LocalDateTime now = LocalDateTime.now();
        User user1 = createUser("박지성", "01011112222", "profile1.jpg", true);
        User user2 = createUser("김연아", "01012345678", "profile2.jpg", true);

        User user3 = createUser("강혜원", "01022222222", "profile3.jpg", true);
        userRepository.saveAll(List.of(user1, user2, user3));

        ChatRoom chatRoom = createChatRoom(user1, user2, false);
        chatRoomRepository.save(chatRoom);

        ChatMessageRequest request = ChatMessageRequest.builder()
                .content("안녕하세요.")
                .build();

        // when // then
        assertThatThrownBy(() -> chatMessageService.saveMessage(chatRoom.getRoomId(), request, user3.getMobileNumber(), now))
                .isInstanceOf(CustomException.class)
                .hasMessage("유저가 채팅방에 존재하지 않습니다.");
    }

    @DisplayName("채팅방에 존재하는 모든 메세지를 불러온다. (1번 유저가 채팅방에 접속했을 때 기준)")
    @Test
    void getMessages() {
        // given
        LocalDateTime now = LocalDateTime.now();
        User user1 = createUser("박지성", "01011112222", "profile1.jpg", true);
        User user2 = createUser("김연아", "01012345678", "profile2.jpg", true);
        userRepository.saveAll(List.of(user1, user2));

        ChatRoom chatRoom = createChatRoom(user1, user2, false);
        chatRoomRepository.save(chatRoom);

        ChatMessage chatMessage1 = createChatMessage(chatRoom, "1번입니다.", user1, user2, now);
        ChatMessage chatMessage2 = createChatMessage(chatRoom, "2번입니다.", user2, user1, now);
        chatMessageRepository.saveAll(List.of(chatMessage1, chatMessage2));

        // when
        List<ChatMessageListResponse> messageListResponse =
                chatMessageService.getMessages(chatRoom.getRoomId(), user1.getMobileNumber());

        // then
        assertThat(messageListResponse).hasSize(2)
                .extracting("yourId", "yourNickname", "senderId", "content", "yourIsRead")
                .containsExactlyInAnyOrder(
                        tuple(user2.getId(), user2.getNickname(), user1.getId(), "1번입니다.", false),
                        tuple(user2.getId(), user2.getNickname(), user2.getId(), "2번입니다.", false)
                );
    }

    @DisplayName("채팅방에 존재하는 모든 메세지를 불러온다. (2번 유저가 채팅방에 접속했을 때 기준)")
    @Test
    void getMessagesWithUser2() {
        // given
        LocalDateTime now = LocalDateTime.now();
        User user1 = createUser("박지성", "01011112222", "profile1.jpg", true);
        User user2 = createUser("김연아", "01012345678", "profile2.jpg", true);
        userRepository.saveAll(List.of(user1, user2));

        ChatRoom chatRoom = createChatRoom(user1, user2, false);
        chatRoomRepository.save(chatRoom);

        ChatMessage chatMessage1 = createChatMessage(chatRoom, "1번입니다.", user1, user2, now);
        ChatMessage chatMessage2 = createChatMessage(chatRoom, "2번입니다.", user2, user1, now);
        chatMessageRepository.saveAll(List.of(chatMessage1, chatMessage2));

        // when
        List<ChatMessageListResponse> messageListResponse =
                chatMessageService.getMessages(chatRoom.getRoomId(), user2.getMobileNumber());

        // then
        assertThat(messageListResponse).hasSize(2)
                .extracting("yourId", "yourNickname", "senderId", "content", "yourIsRead")
                .containsExactlyInAnyOrder(
                        tuple(user1.getId(), user1.getNickname(), user1.getId(), "1번입니다.", false),
                        tuple(user1.getId(), user1.getNickname(), user2.getId(), "2번입니다.", false)
                );
    }

    @DisplayName("채팅방에 존재하는 메세지를 불러올 때, ChatRoom에 속한 유저가 아니면 예외가 발생한다.")
    @Test
    void getMessagesWithoutInChatRoom() {
        // given
        LocalDateTime now = LocalDateTime.now();
        User user1 = createUser("박지성", "01011112222", "profile1.jpg", true);
        User user2 = createUser("김연아", "01012345678", "profile2.jpg", true);

        User user3 = createUser("강혜원", "01022222222", "profile3.jpg", true);
        userRepository.saveAll(List.of(user1, user2, user3));

        ChatRoom chatRoom = createChatRoom(user1, user2, false);
        chatRoomRepository.save(chatRoom);

        ChatMessage chatMessage1 = createChatMessage(chatRoom, "1번입니다.", user1, user2, now);
        ChatMessage chatMessage2 = createChatMessage(chatRoom, "2번입니다.", user2, user1, now);
        chatMessageRepository.saveAll(List.of(chatMessage1, chatMessage2));

        // when // then
        assertThatThrownBy(() -> chatMessageService.getMessages(chatRoom.getRoomId(), user3.getMobileNumber()))
                .isInstanceOf(CustomException.class)
                .hasMessage("유저가 채팅방에 존재하지 않습니다.");
    }

    @DisplayName("상대방이 보낸 메세지를 전부 읽는다. (1번 유저 기준으로 2번이 보낸 메세지를 읽는다.)")
    @Test
    void acknowledge() {
        // given
        LocalDateTime now = LocalDateTime.now();
        User user1 = createUser("박지성", "01011112222", "profile1.jpg", true);
        User user2 = createUser("김연아", "01012345678", "profile2.jpg", true);
        userRepository.saveAll(List.of(user1, user2));

        ChatRoom chatRoom = createChatRoom(user1, user2, false);
        chatRoomRepository.save(chatRoom);

        ChatMessage chatMessage1 = createChatMessage(chatRoom, "1번입니다.", user1, user2, now);
        ChatMessage chatMessage2 = createChatMessage(chatRoom, "2번입니다.", user2, user1, now);
        chatMessageRepository.saveAll(List.of(chatMessage1, chatMessage2));

        // when
        chatMessageService.acknowledge(chatRoom.getRoomId(), user1.getMobileNumber());

        // then
        List<ChatMessage> chatMessageList = chatMessageRepository.findAllByChatRoomAndSenderNotAndIsReadIsFalse(chatRoom, user1);
        assertThat(chatMessageList).isEmpty();

        ChatMessage chatMessage = chatMessageRepository.findById(chatMessage2.getMessageId()).get();
        assertThat(chatMessage.getIsRead()).isTrue();
    }


    private User createUser(final String nickname, final String mobileNumber, final String imageUrl, final boolean blueCheck) {
        return User.builder()
                .mobileNumber(mobileNumber)
                .nickname(nickname)
                .deviceId("123456")
                .authority(Authority.USER)
                .imageUrl(imageUrl)
                .blueCheck(blueCheck)
                .build();
    }

    private ChatRoom createChatRoom(final User sender, final User receiver, final boolean extend) {
        return ChatRoom.builder()
                .sender(sender)
                .receiver(receiver)
                .extend(extend)
                .build();
    }

    private ChatMessage createChatMessage(final ChatRoom chatRoom, final String content, final User sender, final User receiver, final LocalDateTime sendTime) {
        return ChatMessage.builder()
                .chatRoom(chatRoom)
                .content(content)
                .sender(sender)
                .receiver(receiver)
                .isRead(false)
                .sendTime(sendTime)
                .build();
    }

}